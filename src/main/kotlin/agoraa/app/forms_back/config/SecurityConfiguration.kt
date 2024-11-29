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
                    .requestMatchers("/api/users/{id}").hasRole("ADMIN")
                    .requestMatchers("/api/users/{id}/edit").hasRole("ADMIN")

                    //.requestMatchers("/api/users/edit-current").hasAnyRole("ADMIN", "FISCAL", "COMPRADOR")

                    // suppliers endpoints
                    .requestMatchers(HttpMethod.POST, "/api/suppliers").hasRole("ADMIN")
                    .requestMatchers("/api/suppliers/{id}").hasRole("ADMIN")
                    .requestMatchers("/api/suppliers/{id}/edit").hasRole("ADMIN")
                    .requestMatchers("/api/suppliers/create-multiple").hasRole("ADMIN")
                    .requestMatchers("/api/suppliers/edit-multiple").hasRole("ADMIN")

                    //.requestMatchers(HttpMethod.GET, "/api/suppliers").hasAnyRole("ADMIN", "FISCAL", "COMPRADOR")

                    // products endpoints
                    .requestMatchers(HttpMethod.POST,"/api/products").hasRole("ADMIN")
                    .requestMatchers("/api/products/create-multiple").hasRole("ADMIN")

                    //.requestMatchers(HttpMethod.GET, "/api/products").hasAnyRole("ADMIN", "FISCAL", "COMPRADOR")
                    //.requestMatchers("/api/products/{id}").hasAnyRole("ADMIN", "FISCAL", "COMPRADOR")

                    // extra-orders endpoints
                    .requestMatchers("api/extra-orders/{id}/edit").hasRole("ADMIN")

                    //.requestMatchers("/api/extra-orders").hasAnyRole("ADMIN", "FISCAL", "COMPRADOR")
                    //.requestMatchers("/api/extra-orders/{id}").hasAnyRole("ADMIN", "FISCAL", "COMPRADOR")

                    // extra-order-products endpoints
                    .requestMatchers("/api/extra-order-products/{id}").hasRole("ADMIN")
                    .requestMatchers("/api/extra-order-products/{id}/edit").hasRole("ADMIN")
                    .requestMatchers("/api/extra-order-products/{id}/delete").hasRole("ADMIN")

                    //.requestMatchers("/api/extra-order-products").hasAnyRole("ADMIN", "FISCAL", "COMPRADOR")

                    .anyRequest().fullyAuthenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
}