package agoraa.app.forms_back.dto.supplier_registration

import agoraa.app.forms_back.dto.supplier_registration_stores.SupplierRegistrationStoresDto
import agoraa.app.forms_back.dto.user.UserDto
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SupplierRegistrationDto(
    val id: Long,
    val user: UserDto,
    val accepted: Boolean,
    val createdAt: LocalDateTime,
    var stores: List<SupplierRegistrationStoresDto>? = null
)
