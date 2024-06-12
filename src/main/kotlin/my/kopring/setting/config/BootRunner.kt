package my.kopring.setting.config

import mu.KotlinLogging
import my.kopring.setting.component.ActiveProfile
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
class BootRunner(
    val activeProfile: ActiveProfile
) : CommandLineRunner, ApplicationListener<ContextClosedEvent> {
    override fun run(vararg args: String?) {
        log.info("===================================================================================================");
        log.info("Start my-kopring-setting ({}) : {}", activeProfile.get(), LocalDateTime.now());
        log.info("===================================================================================================");
    }

    override fun onApplicationEvent(event: ContextClosedEvent) {
        log.info("===================================================================================================");
        log.info("Stop my-kopring-setting ({}) : {}", activeProfile.get(), LocalDateTime.now());
        log.info("===================================================================================================");
    }
}