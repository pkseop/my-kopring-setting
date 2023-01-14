package my.kopring.setting.config

import my.kopring.setting.filter.JwtRequestFilter
import my.kopring.setting.jwt.JwtAuthenticationEntryPoint
import my.kopring.setting.jwt.JwtTokenHandler
import my.kopring.setting.jwt.JwtUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.CorsUtils
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenHandler: JwtTokenHandler,
    private val jwtUserDetailsService: JwtUserDetailsService
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.addAllowedOriginPattern("*")
        configuration.addAllowedMethod("*")
        configuration.addAllowedHeader("*")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager;
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityAysnConfig(){
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL)
    }

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .formLogin().disable()
            .httpBasic().disable()
            .csrf().disable()
            .addFilterBefore(JwtRequestFilter(jwtTokenHandler, jwtUserDetailsService), UsernamePasswordAuthenticationFilter::class.java)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().cors()
            .and().authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .antMatchers("/monitor/**").permitAll()
            .antMatchers("/lang/**").permitAll()
            .antMatchers("/account/**").permitAll()
            .antMatchers("/v1/auth/login").permitAll()
            .antMatchers("/v1/auth/refresh/token").permitAll()
            .antMatchers("/docs/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .exceptionHandling().authenticationEntryPoint(JwtAuthenticationEntryPoint())
            .and()
            .build()
    }
}