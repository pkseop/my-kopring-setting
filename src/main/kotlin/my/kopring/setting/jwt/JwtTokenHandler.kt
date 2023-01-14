package my.kopring.setting.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import mu.KotlinLogging
import my.kopring.setting.entity.RefreshTokenId
import my.kopring.setting.exception.EntityNotFoundException
import my.kopring.setting.repository.redis.InvalidatedAccessTokenRepository
import my.kopring.setting.service.domain.RefreshTokenIdDomService
import my.kopring.setting.utils.IdGenUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

@Component
class JwtTokenHandler(
    @Value("\${jwt.access.token.secret}")
    private val accessTokenSecret: String,
    @Value("\${jwt.access.token.expiration.hours}")
    private val accessTokenExpirationHours: Long,
    @Value("\${jwt.refresh.token.secret}")
    private val refreshTokenSecret: String,
    @Value("\${jwt.refresh.token.expiration.hours}")
    private val refreshTokenExpirationHours: Long,

    private val refreshTokenIdDomService: RefreshTokenIdDomService,
    private val jwtUserDetailsService: JwtUserDetailsService,

    private val invalidatedAccessTokenRepository: InvalidatedAccessTokenRepository
) {
    private lateinit var accessTokenAlg: Algorithm
    private lateinit var accessTokenVerifier: JWTVerifier

    private lateinit var refreshTokenAlg: Algorithm
    private lateinit var refreshTokenVerifier: JWTVerifier

    companion object {
        const val USERNAME = "username"
        const val ROLE = "role"
        const val CREATED_AT = "createdAt"
    }

    @PostConstruct
    fun postConstruct() {
        this.accessTokenAlg = Algorithm.HMAC256(accessTokenSecret)
        this.accessTokenVerifier = JWT.require(this.accessTokenAlg).build()

        this.refreshTokenAlg = Algorithm.HMAC256(refreshTokenSecret)
        this.refreshTokenVerifier = JWT.require(this.refreshTokenAlg).build();
    }

    private fun toInstant(expirationMillisec: Long): Instant {
        val now = Instant.now().toEpochMilli()
        val expiration = now + expirationMillisec
        return Instant.ofEpochMilli(expiration)
    }

    private fun getAccessTokenExpiration(): Instant {
        return this.toInstant(accessTokenExpirationHours * 60 * 60 * 1000)
    }

    private fun getRefreshTokenExpiration(): Instant {
        return this.toInstant(refreshTokenExpirationHours * 60 * 60 * 1000)
    }

    private fun genAccessToken(userDetails: JwtUserDetails): Pair<String, Long> {
        val exp = this.getAccessTokenExpiration()
        val accessToken = JWT.create()
            .withClaim(USERNAME, userDetails.username)
            .withClaim(ROLE, userDetails.user.roleType.value)
            .withClaim(CREATED_AT, System.currentTimeMillis())
            .withExpiresAt(exp)
            .sign(this.accessTokenAlg)

        return Pair<String, Long>(accessToken, exp.toEpochMilli())
    }

    private fun genRefreshToken(userId: String): String {
        val id = IdGenUtils.generate()
        refreshTokenIdDomService.create(id, userId)

        return JWT.create()
            .withSubject(id)
            .withExpiresAt(this.getRefreshTokenExpiration())
            .withClaim(CREATED_AT, System.currentTimeMillis())
            .sign(this.refreshTokenAlg)
    }

    fun genAccessAndRefreshTokens(userDetails: JwtUserDetails): Pair<Pair<String, Long>, String> {
        val accessToken = this.genAccessToken(userDetails)
        val refreshToken = this.genRefreshToken(userDetails.user.userId!!)
        return Pair(accessToken, refreshToken)
    }

    fun validateAccessTokenAndGetUsername(token: String, response: HttpServletResponse): String {
        try {
            val decodedJWT = accessTokenVerifier.verify(token)
            return decodedJWT.getClaim(USERNAME).asString()
        } catch (e: TokenExpiredException) {
            response.setHeader("X-GC-Token-Expired", "1")
            throw e
        }
    }

    fun doRefreshToken(accessToken: String, refreshToken: String): Pair<Pair<String, Long>, String> {
        val decodedJWT = JWT.decode(accessToken)    // verify and get jwt info from access token
        val username = decodedJWT.getClaim(USERNAME).asString()

        val id = refreshTokenVerifier.verify(refreshToken).subject
        val prev: RefreshTokenId?
        try {
            prev = refreshTokenIdDomService.get(id)
        } catch (e: EntityNotFoundException) {
            throw JWTVerificationException("Invalid refresh token")
        }

        if (prev.used) {
            throw JWTVerificationException("Invalid refresh token")
        }
        prev.used = true
        refreshTokenIdDomService.update(prev)


        val jwtUserDetails = jwtUserDetailsService.loadUserByUsername(username)
        val newAccessToken = this.genAccessToken(jwtUserDetails)
        val newRefreshToken = this.genRefreshToken(jwtUserDetails.user.userId!!)

        return Pair(newAccessToken, newRefreshToken)
    }

    // 비번 변경으로 인해 이전 토큰들을 사용할 수 없게 처리.
    fun checkInvalidToken(userId: String, token: String) {
        val invalidatedAt = invalidatedAccessTokenRepository.getInvalidatedAt(userId) ?: return
        val decodedJWT = accessTokenVerifier.verify(token)
        val createdAt = decodedJWT.getClaim(CREATED_AT).asLong()
        if(createdAt < invalidatedAt) {
            throw JWTVerificationException("Invalid access token")
        }
    }

    fun invalidateToken(userId: String) {
        invalidatedAccessTokenRepository.setInvalidatedAt(userId)
        refreshTokenIdDomService.used(userId)
    }
}