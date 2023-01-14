package my.kopring.setting.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import my.kopring.setting.component.ActiveProfile
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.http.CacheControl
import org.springframework.util.StringUtils
import org.springframework.web.servlet.View
import org.springframework.web.servlet.ViewResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.view.json.MappingJackson2JsonView
import org.thymeleaf.cache.StandardCacheManager
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring5.view.ThymeleafViewResolver
import java.util.concurrent.TimeUnit

@Configuration
@EnableWebMvc
class WebConfig(
    val applicationContext: ApplicationContext,
    val activeProfile: ActiveProfile
) : WebMvcConfigurer {
    @Bean
    fun getObjectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        return objectMapper
    }


    @Bean
    fun templateEngine(): SpringTemplateEngine {
        val templateEngine = SpringTemplateEngine()
        templateEngine.setTemplateResolver(templateResolver())

        val cacheManager = StandardCacheManager()
        cacheManager.expressionCacheInitialSize = 2000
        cacheManager.expressionCacheMaxSize = 10000
        cacheManager.templateCacheInitialSize = 160
        cacheManager.templateCacheMaxSize = 1000

        templateEngine.enableSpringELCompiler = true
        templateEngine.cacheManager = cacheManager

        return templateEngine
    }

    @Bean
    fun templateResolver(): SpringResourceTemplateResolver {
        val templateResolver = SpringResourceTemplateResolver()
        templateResolver.setApplicationContext(this.applicationContext)
        templateResolver.prefix = "classpath:/templates/"
        templateResolver.suffix = ".html"
        templateResolver.isCacheable = activeProfile.isProd()
        return templateResolver
    }

    @Bean
    fun viewResolver(): ViewResolver {
        val viewResolver = ThymeleafViewResolver()
        viewResolver.templateEngine = templateEngine()
        return viewResolver
    }

    @Bean("jsonView")
    fun jsonView(): View {
        return MappingJackson2JsonView()
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        var cacheControl: CacheControl

        if(activeProfile.isLocal()) {
            cacheControl = CacheControl.noCache()
        } else {
            cacheControl = CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic()
        }

        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/").setCacheControl(cacheControl)
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/").setCacheControl(cacheControl)
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/").setCacheControl(cacheControl)
    }

//    @Bean
//    fun localeResolver(): LocaleResolver {
//        val localeResolver = CookieLocaleResolver()
//        localeResolver.cookieName = "lang"
//        return localeResolver
//    }
//
//    @Bean
//    fun localeInterceptor(): LocaleChangeInterceptor {
//        val localeInterceptor = LocaleChangeInterceptor()
//        localeInterceptor.paramName = "lang"
//        return localeInterceptor
//    }
}