package agoraa.app.forms_back.dto.resource_mips

import agoraa.app.forms_back.enum.MipsCategoriesEnum

data class ResourceMipItemsDto(
    val id: Long,
    val category: MipsCategoriesEnum,
    val quantity: Int,
)
