package agoraa.app.forms_back.config

import agoraa.app.forms_back.model.UserModel
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class CustomUserDetails(
    private val userModel: UserModel,
    username: String,
    password: String,
    enabled: Boolean,
    authorities: Collection<GrantedAuthority>
) : User(username, password, enabled, true, true, true, authorities) {
    fun getUserModel(): UserModel = userModel
}