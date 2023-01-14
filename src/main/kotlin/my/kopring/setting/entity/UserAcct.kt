package my.kopring.setting.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import my.kopring.setting.converter.RoleTypeConverter
import my.kopring.setting.converter.UserStateConverter
import my.kopring.setting.enums.RoleType
import my.kopring.setting.enums.UserState
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class UserAcct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var seq: Long? = null,
    var userId: String? = null,
    var serviceId: String,
    var username: String,
    @JsonIgnore
    var password: String,
    var name: String,
    @Convert(converter = UserStateConverter::class)
    var state: UserState,
    @Convert(converter = RoleTypeConverter::class)
    var roleType: RoleType,
) : BaseTimeEntity() {
}