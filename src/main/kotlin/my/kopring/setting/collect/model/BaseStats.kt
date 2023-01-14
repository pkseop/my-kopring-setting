package my.kopring.setting.collect.model

import java.util.UUID

abstract class BaseStats(
    var serviceId: String? = null,
    var userId: String? = null,
    var userHash: String? = null,
    var uuid: UUID? = null,
    var `when`: Long? = null,
    var ip: String? = null,
) {
}