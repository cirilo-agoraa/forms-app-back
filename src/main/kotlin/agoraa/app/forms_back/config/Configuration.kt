package agoraa.app.forms_back.config

import agoraa.app.forms_back.service.UserService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
class Configuration(
    private val dataSource: DataSource,
    private val userService: UserService
) {
    @Bean
    fun encoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(jdbcUserDetailsManager: JdbcUserDetailsManager): AuthenticationProvider =
        DaoAuthenticationProvider()
            .also {
                it.setUserDetailsService(jdbcUserDetailsManager)
                it.setPasswordEncoder(encoder())
            }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Bean
    fun jdbcUserDetailsManager(): JdbcUserDetailsManager {
        val manager = object: JdbcUserDetailsManager() {
            override fun createUserDetails(
                username: String,
                user: UserDetails,
                authorities: MutableList<GrantedAuthority>
            ): CustomUserDetails {
                val userModel = userService.findByUsername(username)
                    .orElseThrow { IllegalArgumentException("User not found") }

                return CustomUserDetails(
                    userModel,
                    user.username,
                    user.password,
                    user.isEnabled,
                    authorities
                )
            }
        }
        manager.setDataSource(dataSource)
        manager.usersByUsernameQuery = "SELECT username, password, enabled FROM users WHERE username = ?"
        manager.setAuthoritiesByUsernameQuery("SELECT u.username, a.authority FROM authorities a JOIN users u ON a.user_id = u.id WHERE u.username = ?")
        return manager
    }
}