package my.kopring.setting.const

object Constants {
    const val SERVICE_NAME = "my"
    const val EXPIRE_FOR_REMOVE: Long = 5 * 60 * 60 * 1000 //5시간
    const val EXPIRE_FOR_INVALID_TOKEN_REMOVE: Long = 12 * 60 * 60 * 1000 //12시간

    const val ONE_MINUTE = 60 * 1000L
}