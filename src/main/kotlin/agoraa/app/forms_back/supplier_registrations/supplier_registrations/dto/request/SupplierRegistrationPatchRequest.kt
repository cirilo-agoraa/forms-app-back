package agoraa.app.forms_back.supplier_registrations.supplier_registrations.dto.request

data class SupplierRegistrationPatchRequest(
    val created: Boolean? = null,
    val accepted: Boolean? = null,
    val recused: Boolean? = null,
    val recusedMotive: String? = null,
)
