package my.kopring.setting.collect.model

import my.kopring.setting.enums.StatsLogType

class PingContentStats(
    var logType: StatsLogType? = null,
    var liveId: String? = null,
    var join: Boolean? = null
) : BaseStats() {
}