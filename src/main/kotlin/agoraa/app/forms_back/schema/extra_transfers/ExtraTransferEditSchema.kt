package agoraa.app.forms_back.schema.extra_transfers

import agoraa.app.forms_back.enums.StoresEnum

data class ExtraTransferEditSchema(
    val processed: Boolean? = null,
    val originStore: StoresEnum? = null,
    val destinyStore: StoresEnum? = null,
    val products: List<ExtraTransferProductsEditSchema>? = null,
)
