package my.kopring.setting.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class RoleType(
    @JsonValue
    val value: Int,
    val description: String
) {
    ROOT(1, "루트"),
    ADMIN(10, "관리자"),
    BROADCASTER(20, "방송자");

    companion object {
        @JsonCreator
        fun of(value: Int): RoleType {
            for (roleType in values()) {
                if (roleType.value == value) {
                    return roleType
                }
            }
            throw IllegalArgumentException("RoleType [$value] is invalid.")
        }
    }
}