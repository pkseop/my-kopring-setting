package my.kopring.setting.repository

import my.kopring.setting.entity.UserAcct
import my.kopring.setting.enums.UserState
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserAcctRepository : JpaRepository<UserAcct, Long> {
    fun findByUserId(userId: String): Optional<UserAcct>
    fun findByServiceIdAndUserIdAndDeleted(serviceId: String, userId: String, deleted: Boolean): Optional<UserAcct>
    fun existsByServiceIdAndUserIdAndDeleted(serviceId: String, userId: String, deleted: Boolean): Boolean
    fun findByServiceId(serviceId: String): List<UserAcct>

    fun findByUsernameAndDeleted(username: String, deleted: Boolean): Optional<UserAcct>

    fun findByEmailAndDeleted(email: String, deleted: Boolean): Optional<UserAcct>

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun countByServiceIdAndDeleted(serviceId: String, deleted: Boolean): Long

    fun findByState(state: UserState): List<UserAcct>
}