package my.kopring.setting.converter

import my.kopring.setting.enums.RoleType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class RoleTypeConverter : AttributeConverter<RoleType, Int> {

    override fun convertToDatabaseColumn(attribute: RoleType): Int {
        return attribute.value
    }

    override fun convertToEntityAttribute(dbData: Int): RoleType {
        return RoleType.of(dbData)
    }

}