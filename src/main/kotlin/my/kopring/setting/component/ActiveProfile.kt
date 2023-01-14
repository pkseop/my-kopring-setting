package my.kopring.setting.component

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ActiveProfile(
    @Value("\${spring.profiles.active:default}")
    private val activeProfile: String
) {
    fun isProd(): Boolean {
        return activeProfile == "prod"
    }

    fun isDev(): Boolean {
        return activeProfile == "dev"
    }

    fun isLocal(): Boolean {
        return activeProfile == "default"
    }

    fun get(): String {
        return activeProfile
    }
}