package my.kopring.setting.config

import biz.gripcloud.api.cache.kryo.KryoRedisSerializer
import my.kopring.setting.cache.CacheName
import my.kopring.setting.cache.Jackson2JsonRedisWarnSerializer
import my.kopring.setting.component.ActiveProfile
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration


@Configuration
@EnableCaching
class RedisConfig(
    @Value("\${spring.redis.host}") private val host: String,
    @Value("\${spring.redis.port}") private val port: Int,
    @Value("\${spring.redis.cache.ttl}") private val ttl: Int,
    @Value("\${spring.redis.cluster.nodes}")
    private val clusterNodes: List<String>,

    private val activeProfile: ActiveProfile
) {
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return if(activeProfile.isProd()) {
            val clusterConfig = RedisClusterConfiguration(clusterNodes)
            LettuceConnectionFactory(clusterConfig)
        } else {
            val standaloneConfig = RedisStandaloneConfiguration(host, port);
            LettuceConnectionFactory(standaloneConfig)
        }
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.setConnectionFactory(redisConnectionFactory())
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = Jackson2JsonRedisWarnSerializer()
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = Jackson2JsonRedisWarnSerializer()
        return redisTemplate
    }

    @Bean
    @Qualifier("cacheManager")
    fun cacheManager(): RedisCacheManager {
        val cacheConfigurations: MutableMap<String, RedisCacheConfiguration> =
            mutableMapOf<String, RedisCacheConfiguration>();
        for ((key, value) in CacheName.TTL_MAP.entries) {
            cacheConfigurations[key] = cacheConfiguration(value);
        }

        return RedisCacheManager.builder(redisConnectionFactory()) //
            .cacheDefaults(cacheConfiguration(ttl)) //
            .transactionAware() //
            .withInitialCacheConfigurations(cacheConfigurations) //
            .build();
    }

    private fun cacheConfiguration(ttl: Int): RedisCacheConfiguration {
        var cacheConfig = RedisCacheConfiguration.defaultCacheConfig() //
            .entryTtl(Duration.ofSeconds(ttl.toLong())) //

        cacheConfig = cacheConfig.serializeKeysWith(SerializationPair.fromSerializer(StringRedisSerializer())) //
        cacheConfig = cacheConfig.serializeValuesWith(SerializationPair.fromSerializer(KryoRedisSerializer()))
        return cacheConfig
    }
}