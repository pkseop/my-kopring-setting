package my.kopring.setting.service.domain

import my.kopring.setting.entity.RefreshTokenId
import my.kopring.setting.repository.RefreshTokenIdRepository
import my.kopring.setting.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class RefreshTokenIdDomService(
    private val refreshTokenIdRepository: RefreshTokenIdRepository
) {

    fun get(id: String): RefreshTokenId {
        return refreshTokenIdRepository.findById(id)
            .orElseThrow{ throw EntityNotFoundException("Refresh token id [$id] not found") }
    }

    private fun save(refreshTokenId: RefreshTokenId): RefreshTokenId {
        return refreshTokenIdRepository.save(refreshTokenId)
    }

    fun create(id: String, userId: String): RefreshTokenId {
        val refreshTokenId = RefreshTokenId(id, userId)
        return this.save(refreshTokenId)
    }

    fun update(refreshTokenId: RefreshTokenId): RefreshTokenId {
        return this.save(refreshTokenId)
    }

    fun used(userId: String): Int {
        return refreshTokenIdRepository.used(userId)
    }
}