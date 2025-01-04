package agoraa.app.forms_back.dto

data class UserDto(
    val id: Long,
    val username: String,
    val enabled: Boolean?,
    val firstAccess: Boolean?,
    val authorities: List<String>?
)
