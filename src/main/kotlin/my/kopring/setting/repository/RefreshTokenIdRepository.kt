package my.kopring.setting.repository

import my.kopring.setting.entity.RefreshTokenId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

interface RefreshTokenIdRepository : JpaRepository<RefreshTokenId, String> {

    @Transactional
    @Modifying
    @Query("UPDATE RefreshTokenId r SET r.used=true WHERE r.userId=:userId AND r.used=false")
    fun used(@Param("userId") userId: String): Int
}