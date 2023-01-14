package my.kopring.setting.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class UserState(
    @JsonValue
    val state: Int
) {
    WAIT(0),
    READY(1),
    RUN(2),
    FINISH(3),
    GIVE_UP(99)
    ;

    companion object{
        fun of(value: Int): UserState {
            for(state in values()) {
                if(state.state == value) {
                    return state
                }
            }
            throw IllegalArgumentException("Invalid state [$value]")
        }
    }
}