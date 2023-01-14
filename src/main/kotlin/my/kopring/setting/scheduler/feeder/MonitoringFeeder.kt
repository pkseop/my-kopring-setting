package my.kopring.setting.scheduler.feeder

import mu.KotlinLogging
import my.kopring.setting.enums.UserState
import my.kopring.setting.scheduler.async.job.MonitoringJob
import my.kopring.setting.scheduler.async.worker.MonitoringWorker
import my.kopring.setting.scheduler.feeder.bean.MonitoringInfo
import my.kopring.setting.scheduler.feeder.repository.MonitoringRedisRepository
import my.kopring.setting.scheduler.feeder.repository.OccupyRedisRepository
import my.kopring.setting.service.domain.UserAcctDomService
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class MonitoringFeeder {
    val MAX_WORKER = 30

    @Autowired
    private lateinit var applicationContext: ApplicationContext
    @Autowired
    private lateinit var userAcctDomService: UserAcctDomService

    @Qualifier("monitoringTaskExecutor")
    @Autowired
    @Lazy
    private lateinit var monitoringTaskExecutor: ThreadPoolTaskExecutor

    @Autowired
    private lateinit var occupyRedisRepository: OccupyRedisRepository
    @Autowired
    private lateinit var monitoringRedisRepository: MonitoringRedisRepository

    @Bean(name = ["monitoringTaskExecutor"])
    fun monitoringTaskExecutor(applicationContext: ApplicationContext): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.maxPoolSize = 30
        executor.corePoolSize = 2
        log.debug("created instance {}", executor)
        return executor
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 2000)
    fun synchronizeMonitoring() {
        if(!occupyRedisRepository.occupy("synclivemonitoring", 4000)) { //4초 점유 여러놈이 덤벼도 한번만 모니터링 한다.
            return
        }

        val userList = userAcctDomService.getAllUserByState(UserState.READY)
        val userIdSets = mutableSetOf<String>()

        for(user in userList) {
            val userId = user.userId!!
            userIdSets.add(userId)

            if(!monitoringRedisRepository.isExist(userId)) {
                monitoringRedisRepository.updateHealthy(userId, user.name, false)
            }
        }

        val monitoringInfos = monitoringRedisRepository.getAllStreams()
        monitoringInfos?.let {
            for(info in monitoringInfos) {
                val id = info.id
                if(!userIdSets.contains(id)) {
                    log.debug("Monitoring : 제거됨 $id");
                    monitoringRedisRepository.delete(id!!)
                }
            }

            if(monitoringInfos.isEmpty()) {
                monitoringRedisRepository.deleteAll()
            }
        }
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 5000)
    private fun wakeupAndFeedingJob() {
        workMonitoring()
        log.debug("${monitoringTaskExecutor.threadPoolExecutor.queue.size} jobs in Queue T:[A:${monitoringTaskExecutor.activeCount}/P:${monitoringTaskExecutor.poolSize}/M:${monitoringTaskExecutor.maxPoolSize}]")
    }

    private fun workMonitoring() {
        val remainQueueSize = monitoringTaskExecutor.threadPoolExecutor.queue.size

        if (remainQueueSize > 0) {
            log.warn("[Stream] 작업 interval 내에 남은 작업이 모두 처리되지 않았음 $remainQueueSize/${this.MAX_WORKER}")
        }

        val allMonitoringInfo = monitoringRedisRepository.getAllStreams()
        allMonitoringInfo?.let {
            for(info in allMonitoringInfo) {
                val workerQueueSize = monitoringTaskExecutor.threadPoolExecutor.queue.size
                if(workerQueueSize > this.MAX_WORKER) {
                    break
                }

                if(occupyRedisRepository.occupy("monitoring", info.id, 1500)) { //점유에 성공하면 2초동안 다른 대상이 모니터링 큐에 넣지 않는다.
                    val worker: MonitoringWorker = applicationContext.getBean(MonitoringWorker::class.java)
                    worker.job = MonitoringJob (
                        id = info.id,
                        name = info.name,
                        requestTimestamp = System.currentTimeMillis()
                    )
                    worker.workerId = (RandomUtils.nextInt() % 100)

                    monitoringTaskExecutor.execute(worker)
                }
            }
        }
    }
}