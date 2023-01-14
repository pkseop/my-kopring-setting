package my.kopring.setting.logger

import mu.KotlinLogging
import my.kopring.setting.collect.model.BaseStats
import my.kopring.setting.utils.JsonUtils
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class StatsContentLogger : AbstractStatsLogger() {

    fun log(stats: BaseStats) {
        val json = JsonUtils.toJsonSkipNullValue(stats)

        if(!json.isNullOrBlank()) {
            log.info(json)
        }
    }
}