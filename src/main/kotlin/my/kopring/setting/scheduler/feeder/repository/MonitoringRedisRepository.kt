package my.kopring.setting.scheduler.feeder.repository

import my.kopring.setting.cache.RedisHashCommander
import my.kopring.setting.scheduler.feeder.bean.MonitoringInfo
import org.springframework.stereotype.Component

@Component
class MonitoringRedisRepository : RedisHashCommander<MonitoringInfo>() {

    private val BASE_KEY = "liveMonitoring"

    override fun baseKey(): String {
        return this.BASE_KEY
    }

    override fun cleanup(objKey: String?) {
    }

    fun updateHealthy(id: String, name: String, healthy: Boolean): Boolean {
        var info = get(id)
        val timestamp = System.currentTimeMillis()
        var affected = false
        if (info == null) {
            info = MonitoringInfo(
                id = id,
                name = name,
                healthy = healthy,
                lastCheckTimestamp = timestamp,
                createAtTimestamp = timestamp
            )
            affected = true
        } else {
            affected = info.healthy !== healthy
            info.healthy = healthy
            info.lastCheckTimestamp = timestamp
        }
        set(id, info)
        return affected
    }

    fun isHealthy(id: String): Boolean {
        val info = get(id)
        return info != null && info.healthy
    }

    fun isExist(id: String): Boolean {
        return contains(id)
    }

    fun closeLive(id: String): Boolean {
        delete(id)
        return true
    }

    fun getAllStreams(): List<MonitoringInfo>? {
        return values()
    }
}