package agoraa.app.forms_back.extra_orders.extra_order_products.service

import agoraa.app.forms_back.extra_orders.extra_order_products.dto.request.ExtraOrderProductRequest
import agoraa.app.forms_back.extra_orders.extra_order_products.dto.response.ExtraOrderProductsResponse
import agoraa.app.forms_back.extra_orders.extra_order_products.model.ExtraOrderProductsModel
import agoraa.app.forms_back.extra_orders.extra_order_products.repository.ExtraOrderProductRepository
import agoraa.app.forms_back.extra_orders.extra_orders.model.ExtraOrderModel
import org.springframework.stereotype.Service

@Service
class ExtraOrderProductService(
    private val extraOrderProductRepository: ExtraOrderProductRepository,
) {
    private fun create(
        extraOrder: ExtraOrderModel,
        products: List<ExtraOrderProductRequest>
    ) {
        val extraTransfersProducts = products.map { p ->
            ExtraOrderProductsModel(
                extraOrder = extraOrder,
                product = p.product,
                quantity = p.quantity,
                price = p.price
            )
        }
        extraOrderProductRepository.saveAll(extraTransfersProducts)
    }

    private fun edit(products: List<ExtraOrderProductsModel>) {
        val extraOrderProducts = products.map { p ->
            p.copy(
                product = p.product,
                quantity = p.quantity,
                price = p.price
            )
        }
        extraOrderProductRepository.saveAll(extraOrderProducts)
    }

    fun findByParentId(
        extraOrderId: Long,
    ): List<ExtraOrderProductsResponse> = extraOrderProductRepository.findByExtraOrderId(extraOrderId).map { createDto(it) }

    fun createDto(extraOrderProducts: ExtraOrderProductsModel): ExtraOrderProductsResponse {
        return ExtraOrderProductsResponse(
            id = extraOrderProducts.id,
            product = extraOrderProducts.product,
            quantity = extraOrderProducts.quantity,
            price = extraOrderProducts.price
        )
    }

    fun editOrCreateOrDelete(
        extraOrder: ExtraOrderModel,
        products: List<ExtraOrderProductRequest>
    ) {
        val extraOrderProducts = extraOrderProductRepository.findByExtraOrderId(extraOrder.id)
        val currentProductsSet = extraOrderProducts.map { it.product }.toSet()
        val newProductsSet = products.map { it.product }.toSet()

        val toAdd = products.filter { it.product !in currentProductsSet }
        create(extraOrder, toAdd)

        val toDelete = extraOrderProducts.filter { it.product !in newProductsSet }
        extraOrderProductRepository.deleteAll(toDelete)

        val toEdit = extraOrderProducts.filter { it.product in newProductsSet }
        edit(toEdit)
    }
}