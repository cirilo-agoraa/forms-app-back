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

                    // suppliers endpoints
                    .requestMatchers(HttpMethod.POST, "/api/suppliers").hasRole("ADMIN")
                    .requestMatchers("/api/suppliers/{id}/edit").hasRole("ADMIN")

                    // products endpoints
                    .requestMatchers("/api/products/create-multiple").hasRole("ADMIN")
                    .requestMatchers("/api/products/edit-or-create-multiple").hasRole("ADMIN")

                    // resources endpoints
                    .requestMatchers(HttpMethod.GET, "/api/resources").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/resources").hasAnyRole("ADMIN", "LOJA")
                    .requestMatchers("/api/resources/current-user").hasAnyRole("ADMIN", "LOJA")
                    .requestMatchers("/api/resources/{id}").hasAnyRole("ADMIN", "LOJA")
                    .requestMatchers("/api/resources/{id}/edit").hasAnyRole("ADMIN", "LOJA")

                    // extra orders endpoints
                    .requestMatchers(HttpMethod.GET, "/api/extra-orders").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/extra-orders").hasAnyRole("ADMIN", "COMPRADOR", "FISCAL")
                    .requestMatchers("/api/extra-orders/current-user").hasAnyRole("ADMIN", "COMPRADOR", "FISCAL")
                    .requestMatchers("/api/extra-orders/{id}").hasAnyRole("ADMIN", "COMPRADOR", "FISCAL")
                    .requestMatchers("/api/extra-orders/{id}/edit").hasAnyRole("ADMIN", "COMPRADOR", "FISCAL")

                    // suppler registration endpoints
                    .requestMatchers(HttpMethod.GET, "/api/supplier-registrations").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/supplier-registrations").hasAnyRole("ADMIN", "COMPRADOR")
                    .requestMatchers("/api/supplier-registrations/current-user").hasAnyRole("ADMIN", "COMPRADOR")
                    .requestMatchers("/api/supplier-registrations/{id}").hasAnyRole("ADMIN", "COMPRADOR")
                    .requestMatchers("/api/supplier-registrations/{id}/edit").hasAnyRole("ADMIN", "COMPRADOR")

                    // extra quotation endpoints
                    .requestMatchers(HttpMethod.GET, "/api/extra-quotations").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/extra-quotations").hasAnyRole("ADMIN", "COMPRADOR")
                    .requestMatchers("/api/extra-quotations/current-user").hasAnyRole("ADMIN", "COMPRADOR")
                    .requestMatchers("/api/extra-quotations/{id}").hasAnyRole("ADMIN", "COMPRADOR")
                    .requestMatchers("/api/extra-quotations/{id}/edit").hasAnyRole("ADMIN", "COMPRADOR")

                    // extra transfers endpoints
                    .requestMatchers(HttpMethod.GET, "/api/extra-transfers").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/extra-transfers").hasAnyRole("ADMIN", "COMPRADOR")
                    .requestMatchers("/api/extra-transfers/current-user").hasAnyRole("ADMIN", "COMPRADOR")
                    .requestMatchers("/api/extra-transfers/{id}").hasAnyRole("ADMIN", "COMPRADOR")
                    .requestMatchers("/api/extra-transfers/{id}/edit").hasAnyRole("ADMIN", "COMPRADOR")

                    // resource mip endpoints
                    .requestMatchers(HttpMethod.GET, "/api/extra-quotations").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/extra-quotations").hasAnyRole("ADMIN", "LOJA")
                    .requestMatchers("/api/extra-quotations/current-user").hasAnyRole("ADMIN", "LOJA")
                    .requestMatchers("/api/extra-quotations/{id}").hasAnyRole("ADMIN", "LOJA")
                    .requestMatchers("/api/extra-quotations/{id}/edit").hasAnyRole("ADMIN", "LOJA")

                    // passive quotations endpoints
                    .requestMatchers(HttpMethod.GET, "/api/passive-quotations").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/passive-quotations").hasAnyRole("ADMIN", "COMPRADOR")
                    .requestMatchers("/api/passive-quotations/current-user").hasAnyRole("ADMIN", "COMPRADOR")
                    .requestMatchers("/api/passive-quotations/{id}").hasAnyRole("ADMIN", "COMPRADOR")
                    .requestMatchers("/api/passive-quotations/{id}/edit").hasAnyRole("ADMIN", "COMPRADOR")

                    .anyRequest().fullyAuthenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
}