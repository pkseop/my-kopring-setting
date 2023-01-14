package my.kopring.setting.scheduler.feeder.repository

import my.kopring.setting.cache.RedisValueCommander
import my.kopring.setting.const.Constants
import org.springframework.stereotype.Repository

@Repository
class MonitoringWorkerOccupyRepository : RedisValueCommander<String>() {
    override fun baseKey(): String {
        return "worker:occupy:monitoring"
    }

    override fun cleanup(objKey: String?) {
    }

    fun occupy(id: String?): Boolean {
        return setIfNotExist(id, System.currentTimeMillis().toString(), 5 * Constants.ONE_MINUTE)
    }

    fun release(id: String?): Boolean {
        setTTL(id, 500) //종료되면 500ms동안은 방어
        return true
    }
}