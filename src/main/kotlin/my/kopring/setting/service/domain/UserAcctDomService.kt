package my.kopring.setting.service.domain

import my.kopring.setting.cache.CacheName
import my.kopring.setting.entity.UserAcct
import my.kopring.setting.enums.UserState
import my.kopring.setting.exception.EntityNotFoundException
import my.kopring.setting.repository.UserAcctRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class UserAcctDomService(
    private val userAcctRepository: UserAcctRepository,
) {
    @Cacheable(value = [CacheName.USER_ACCT], key = "'username-' + #a0")
    fun getByUsername(username: String): UserAcct {
        val op = userAcctRepository.findByUsernameAndDeleted(username, false)
        if(op.isEmpty) {
            throw EntityNotFoundException("User with username[$username] not found")
        }
        return op.get()
    }

    fun getByServiceIdAndUserId(serviceId: String, userId: String): UserAcct{
        return userAcctRepository.findByServiceIdAndUserIdAndDeleted(serviceId, userId, false)
            .orElseThrow{throw EntityNotFoundException("User with userId[$userId] not found")}
    }

    fun getByEmail(email:String): UserAcct {
        return userAcctRepository.findByEmailAndDeleted(email, false)
            .orElseThrow{ throw EntityNotFoundException("User with email not found")}
    }

    fun getByServiceId(serviceId: String): List<UserAcct> {
        return userAcctRepository.findByServiceId(serviceId)
    }

    fun create(userAcct: UserAcct): UserAcct {
        return this.save(userAcct)
    }

    @CacheEvict(value = [CacheName.USER_ACCT], key = "'username-' + #userAcct.username")
    fun update(userAcct: UserAcct): UserAcct {
        return this.save(userAcct)
    }

    fun existUserByUsername(username: String): Boolean{
        return userAcctRepository.existsByUsername(username)
    }

    fun existUserByEmail(email: String): Boolean{
        return userAcctRepository.existsByEmail(email)
    }

    fun countUserByServiceId(serviceId: String): Long {
        return userAcctRepository.countByServiceIdAndDeleted(serviceId, false)
    }

    private fun save(userAcct: UserAcct): UserAcct {
        return userAcctRepository.save(userAcct)
    }

    fun getAllUserByState(state: UserState): List<UserAcct> {
        return userAcctRepository.findByState(state)
    }
}