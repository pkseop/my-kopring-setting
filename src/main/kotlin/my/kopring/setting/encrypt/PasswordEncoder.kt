package biz.gripcloud.admin.encrypt

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional

object PasswordEncoder {

    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder();

    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        if (rawPassword === null || encodedPassword === null) {
            return false
        }

        if (passwordEncoder.matches(rawPassword, encodedPassword)){
            return true
        }

        return try {
            rawPassword == AESCryptor.decrypt(encodedPassword)
        } catch (e: Exception){
            false
        }
    }

    fun encode(rawPassword: String): String {
        return Optional.ofNullable(rawPassword)
            .map(passwordEncoder::encode)
            .orElse(null)
    }
}