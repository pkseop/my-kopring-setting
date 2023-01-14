package my.kopring.setting.jwt

import my.kopring.setting.entity.UserAcct
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class JwtUserDetails(
    val user: UserAcct
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf<GrantedAuthority>(SimpleGrantedAuthority(user.roleType.name))
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true;
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}