package my.kopring.setting.jwt

import mu.KotlinLogging
import my.kopring.setting.service.domain.UserAcctDomService
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class JwtUserDetailsService(
    private val userAcctDomService: UserAcctDomService,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): JwtUserDetails {
        val user = userAcctDomService.getByUsername(username)

        return JwtUserDetails(user)
    }
}