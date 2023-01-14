package my.kopring.setting.converter

import my.kopring.setting.enums.UserState
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class UserStateConverter : AttributeConverter<UserState, Int> {
    override fun convertToDatabaseColumn(attribute: UserState): Int {
        return attribute.state
    }

    override fun convertToEntityAttribute(dbData: Int): UserState {
        return UserState.of(dbData)
    }
}