package my.kopring.setting.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

object JsonUtils {
    val NONNULL_OBJECT_MAPPER: ObjectMapper = ObjectMapper()

    init {
        NONNULL_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        NONNULL_OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    fun toJson(mapper: ObjectMapper, any: Any): String {
        return mapper.writeValueAsString(any)
    }

    fun toJsonSkipNullValue(any: Any): String {
        return toJson(NONNULL_OBJECT_MAPPER, any)
    }
}