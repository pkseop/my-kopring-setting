package my.kopring.setting.exception

import my.kopring.setting.utils.DateTimeUtils

data class ErrorResponse(
    val status: Int,
    val error: String,
    val errors: Any?,
    val message: String?,
    val path: String,
    val timestamp: Long = DateTimeUtils.currentMills()
)
