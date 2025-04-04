package agoraa.app.forms_back.extra_orders.extra_order_stores.service

import agoraa.app.forms_back.extra_orders.extra_order_stores.dto.request.ExtraOrderStoresRequest
import agoraa.app.forms_back.extra_orders.extra_order_stores.dto.response.ExtraOrderStoresResponse
import agoraa.app.forms_back.extra_orders.extra_order_stores.model.ExtraOrderStoresModel
import agoraa.app.forms_back.extra_orders.extra_order_stores.repository.ExtraOrderStoreRepository
import agoraa.app.forms_back.extra_orders.extra_orders.model.ExtraOrderModel
import org.springframework.stereotype.Service

@Service
class ExtraOrderStoreService(
    private val extraOrderStoreRepository: ExtraOrderStoreRepository
) {
    private fun create(
        extraOrder: ExtraOrderModel,
        stores: List<ExtraOrderStoresRequest>
    ) {
        val extraTransfersStores = stores.map { s ->
            ExtraOrderStoresModel(
                extraOrder = extraOrder,
                store = s.store
            )
        }
        extraOrderStoreRepository.saveAll(extraTransfersStores)
    }

    fun findByParentId(
        extraOrderId: Long,
    ): List<ExtraOrderStoresResponse> = extraOrderStoreRepository.findByExtraOrderId(extraOrderId).map { createDto(it) }

    fun createDto(extraOrderStores: ExtraOrderStoresModel): ExtraOrderStoresResponse {
        return ExtraOrderStoresResponse(
            id = extraOrderStores.id,
            store = extraOrderStores.store
        )
    }

    fun editOrCreateOrDelete(
        extraOrder: ExtraOrderModel,
        stores: List<ExtraOrderStoresRequest>
    ) {
        val extraOrderStores = extraOrderStoreRepository.findByExtraOrderId(extraOrder.id)
        val currentStoresSet = extraOrderStores.map { it.store }.toSet()
        val newStoresSet = stores.map { it.store }.toSet()

        val toAdd = stores.filter { it.store !in currentStoresSet }
        create(extraOrder, toAdd)

        val toDelete = extraOrderStores.filter { it.store !in newStoresSet }
        extraOrderStoreRepository.deleteAll(toDelete)
    }
}