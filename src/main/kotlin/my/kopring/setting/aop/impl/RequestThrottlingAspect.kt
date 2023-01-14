package my.kopring.setting.aop.impl

import my.kopring.setting.aop.annotation.ThrottleKey
import my.kopring.setting.aop.annotation.ThrottleValue
import my.kopring.setting.aop.annotation.Throttleable
import mu.KotlinLogging
import my.kopring.setting.exception.BadRequestException
import my.kopring.setting.repository.redis.RequestThrottlingRepository
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Aspect
@Component
class RequestThrottlingAspect(
    private val requestThrottlingRepository: RequestThrottlingRepository
) {
    @Before("@annotation(my.kopring.setting.aop.annotation.Throttleable) * and args(param..)")
    fun checkThrottleLimits(joinPoint: JoinPoint) {
        val parameterValues = joinPoint.args

        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val methodAnnotations = method.annotations
        var throttleable: Throttleable? = null

        for(annotation in methodAnnotations) {
            if(annotation is Throttleable) {
                throttleable = annotation
                break
            }
        }
        if(throttleable == null) {
            log.error("Invalid annotation")
            return
        }

        val parameterAnnotations = method.parameterAnnotations
        var value = 1L // 기본적으로 증가되는 값은 1로 지정한다.
        val keyBuilder = StringBuilder(throttleable.key)

        for((i, paramAnnotations) in parameterAnnotations.withIndex()) {
            for(annotation in paramAnnotations) {
                if(annotation is ThrottleValue) {
                    if(parameterValues[i] is Number) {
                        value = (parameterValues[i] as Number).toLong()
                    } else {
                        throw Exception("Invalid throttling param")
                    }
                } else if(annotation is ThrottleKey) {
                    if(parameterValues[i] != null) {
                        keyBuilder.append("_").append(parameterValues[i]);
                    }
                }
            }
        }

        val key = keyBuilder.toString()

        if(requestThrottlingRepository.isBlocked(key)) {
            log.error { "지속적인 요청: ${key}_${value}" }
            throw BadRequestException("Request throttled")
        }

        val appendedCount = requestThrottlingRepository.append(key, value, throttleable.duration.toLong()) ?: 0L
        if(appendedCount > throttleable.limitQps) {
            requestThrottlingRepository.block(key, throttleable.blockDuration.toLong())
            throw BadRequestException("Request throttled")
        }
    }
}