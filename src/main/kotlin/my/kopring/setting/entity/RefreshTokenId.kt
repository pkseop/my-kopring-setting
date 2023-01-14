package my.kopring.setting.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class RefreshTokenId(
    @Id
    val id: String,
    val userId: String,
    var used: Boolean = false
) : BaseTimeEntity() {

}