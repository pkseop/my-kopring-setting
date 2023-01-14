package my.kopring.setting.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@EnableScheduling
@Configuration
class ScheduleConfig {
    @Bean(name = ["commonTaskScheduler"])
    fun commonTaskScheduler(): TaskScheduler {
        return taskScheduler(5)
    }

    private fun taskScheduler(poolSize: Int): ThreadPoolTaskScheduler {
        val taskScheduler = ThreadPoolTaskScheduler()
        taskScheduler.poolSize = poolSize
        return taskScheduler
    }
}