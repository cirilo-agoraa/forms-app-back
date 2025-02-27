package agoraa.app.forms_back.schema.extra_order

import agoraa.app.forms_back.model.suppliers.SupplierModel

data class ExtraOrderEditSchema(
    val supplier: SupplierModel? = null,
    val partialComplete: String? = null,
    val processed: Boolean? = null,
    val stores: List<ExtraOrderStoresCreateSchema>? = null,
    val products: List<ExtraOrderProductEditSchema>? = null,
    val origin: String? = null,
    )
