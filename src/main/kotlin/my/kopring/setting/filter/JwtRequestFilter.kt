package my.kopring.setting.filter

import mu.KotlinLogging
import my.kopring.setting.jwt.JwtTokenHandler
import my.kopring.setting.jwt.JwtUserDetailsService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

class JwtRequestFilter(
    private val jwtTokenHandler: JwtTokenHandler,
    private val jwtUserDetailsService: JwtUserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader: String? = request.getHeader("Authorization") ?: return filterChain.doFilter(request, response);
        val accessToken: String = authorizationHeader?.substring("Bearer ".length) ?: return filterChain.doFilter(request, response)

        val username = jwtTokenHandler.validateAccessTokenAndGetUsername(accessToken, response)
        val userDetails = jwtUserDetailsService.loadUserByUsername(username)
        jwtTokenHandler.checkInvalidToken(userDetails.user.userId!!, accessToken)

        val authorities = listOf(SimpleGrantedAuthority("ROLE_${userDetails.user.roleType.name}"))
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
            userDetails.user, null, authorities
        )
        SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken;

        filterChain.doFilter(request, response)
    }
}