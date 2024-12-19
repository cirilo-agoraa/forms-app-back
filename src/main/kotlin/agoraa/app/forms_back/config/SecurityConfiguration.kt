package agoraa.app.forms_back.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider
) {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): DefaultSecurityFilterChain =
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/auth", "/api/auth/refresh").permitAll()
                    .requestMatchers("swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                    // users endpoints
                    .requestMatchers("/api/users").hasRole("ADMIN")

                    // suppliers endpoints
                    .requestMatchers("/api/suppliers/create-multiple").hasRole("ADMIN")
                    .requestMatchers("/api/suppliers/edit-or-create-multiple").hasRole("ADMIN")

                    // products endpoints
                    .requestMatchers("/api/products/create-multiple").hasRole("ADMIN")
                    .requestMatchers("/api/products/edit-or-create-multiple").hasRole("ADMIN")

                    .anyRequest().fullyAuthenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
}