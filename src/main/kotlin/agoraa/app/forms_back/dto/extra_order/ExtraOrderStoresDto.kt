package agoraa.app.forms_back.dto.extra_order

import agoraa.app.forms_back.shared.enums.StoresEnum

data class ExtraOrderStoresDto(
    val id: Long,
    val store: agoraa.app.forms_back.shared.enums.StoresEnum
)