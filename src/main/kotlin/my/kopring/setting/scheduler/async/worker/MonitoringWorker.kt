package my.kopring.setting.scheduler.async.worker

import mu.KotlinLogging
import my.kopring.setting.scheduler.async.job.MonitoringJob
import my.kopring.setting.scheduler.feeder.repository.MonitoringWorkerOccupyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
@Scope("prototype")
class MonitoringWorker : Runnable {
    var workerId = 0
    var job: MonitoringJob? = null

    @Autowired
    private lateinit var monitoringWorkerOccupyRepository: MonitoringWorkerOccupyRepository

    override fun run() {
        if(job == null || job?.id.isNullOrBlank()) {
            log.error("worker[$workerId] invalid liveId!")
            return
        }

        var occupiedId: String? = null;
        try {
            val id = job!!.id
            if(!monitoringWorkerOccupyRepository.occupy(id)) {
                log.warn("live stream [$workerId]:[$id] 점유 실패")
                return
            }
            occupiedId = id
            log.debug { "live stream [$workerId]:[$occupiedId] 점유 성공 " }
            // Do some business logic...
            // .....
            // .....
        } catch(t: Throwable) {
            log.error("worker [$workerId] error!", t)
        } finally {
            occupiedId?.let { monitoringWorkerOccupyRepository.release(occupiedId) }

        }
    }
}