package my.kopring.setting.config

import mu.KotlinLogging
import my.kopring.setting.component.ActiveProfile
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor

private val log = KotlinLogging.logger {}

@Configuration
@EnableAsync
class SpringAsyncConfig(
    private val activeProfile: ActiveProfile
) {
    private fun threadPoolTaskExecutor(
        name: String,
        capacity: Int,
        rejectedExecutionHandler: RejectedExecutionHandler
    ): ThreadPoolTaskExecutor {
        val corePoolSize = if (activeProfile.isProd()) 4 else 1
        val taskExecutor = ThreadPoolTaskExecutor()
        taskExecutor.corePoolSize = corePoolSize // 최소 동시 처리 쓰래드 개수
        taskExecutor.maxPoolSize = 10 // 최대 동시 처리 쓰래드 개수, QueueCapacity에 도달하면 동시 처리 쓰래드 개수가 늘어남
        taskExecutor.queueCapacity = capacity
        taskExecutor.setThreadNamePrefix("$name-")
        taskExecutor.setRejectedExecutionHandler(rejectedExecutionHandler)
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true)
        taskExecutor.setAwaitTerminationSeconds(60) // shutdown 최대 60초 대기
        taskExecutor.initialize()
        return taskExecutor
    }

    private fun threadPoolTaskExecutor(name: String): ThreadPoolTaskExecutor {
        return threadPoolTaskExecutor(name, 200, ThreadPoolExecutor.AbortPolicy())
    }

    @Bean(name = ["threadPoolTaskExecutor"])
    fun threadPoolTaskExecutor(): Executor {
        return threadPoolTaskExecutor("threadPoolTaskExecutor")
    }


}