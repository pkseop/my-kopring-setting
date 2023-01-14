package my.kopring.setting.scheduler.feeder.bean

class MonitoringInfo (
    var id: String? = null,
    var name: String? = null,
    var healthy: Boolean = false,
    var lastCheckTimestamp: Long = 0,
    var createAtTimestamp: Long = 0
) {
}