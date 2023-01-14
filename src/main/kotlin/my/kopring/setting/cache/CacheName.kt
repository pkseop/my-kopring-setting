package my.kopring.setting.cache

import my.kopring.setting.const.Constants

object CacheName {
    private const val SHARED_PREFIX: String = "shared:cache"
    private const val PREFIX = Constants.SERVICE_NAME + ":api"


    const val SERVICE_SECRET = "${PREFIX}:service-secret"
    const val SERVICE_ACCT = "${PREFIX}:service-acct"
    const val USER_ACCT = "${PREFIX}:user-acct"
    const val CHANNEL = "${PREFIX}:channel"
    const val CHAT_COLOR: String = "${PREFIX}:chat-color"
    const val GAME_SESSION: String = "${PREFIX}:game-session"
    const val SHORT_FORM = "${PREFIX}:shorform"

    val TTL_MAP: MutableMap<String, Int> = mutableMapOf();

    init {
        TTL_MAP[SERVICE_SECRET] = 600
        TTL_MAP[SERVICE_ACCT] = 5
        TTL_MAP[CHANNEL] = 5
        TTL_MAP[CHAT_COLOR] = 3600
        TTL_MAP[GAME_SESSION] = 600
        TTL_MAP[SHORT_FORM] = 5
    }
}