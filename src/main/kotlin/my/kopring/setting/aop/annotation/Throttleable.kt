package my.kopring.setting.aop.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Throttleable(
    val key: String,
    val duration: Int = 5000,
    val limitQps: Int = 5,
    val blockDuration: Int = 3*60*1000
)
