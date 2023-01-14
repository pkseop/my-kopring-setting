package my.kopring.setting.config

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig(
    @Value("\${rabbitmq.direct.exchange.name}") private val directExchangeName: String,
    @Value("\${rabbitmq.queue.name.test}") private val cloudQueueName: String,
    @Value("\${rabbitmq.routing.key.test}") private val cloudRoutingKey: String,
) {

    @Bean
    fun cloudQueue(): Queue {
        val queue = Queue(cloudQueueName)
        queue.addArgument("x-queue-type", "quorum")
        return queue
    }

    @Bean
    fun directExchange(): DirectExchange {
        return DirectExchange(directExchangeName)
    }

    @Bean
    fun cloudBinding(): Binding {
        return BindingBuilder
            .bind(cloudQueue())
            .to(directExchange())
            .with(cloudRoutingKey)
    }

    @Bean
    fun converter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = converter()
        return rabbitTemplate
    }
}