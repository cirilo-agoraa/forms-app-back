package agoraa.app.forms_back.service.extra_orders

import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.extra_orders.ExtraOrderModel
import agoraa.app.forms_back.model.extra_orders.ExtraOrderProductModel
import agoraa.app.forms_back.repository.ExtraOrderProductRepository
import agoraa.app.forms_back.schema.extra_order_product.ExtraOrderProductCreateSchema
import agoraa.app.forms_back.service.ProductService
import org.springframework.stereotype.Service

@Service
class ExtraOrderProductService(
    private val productService: ProductService,
    private val extraOrderProductRepository: ExtraOrderProductRepository,
) {

    fun delete(extraOrderProduct: ExtraOrderProductModel) {
        val foundExtraOrderProduct = extraOrderProductRepository.findById(extraOrderProduct.id)
            .map { extraOrderProductRepository.delete(it) }
            .orElseThrow { ResourceNotFoundException("Extra Order Store not found.") }
    }

    fun deleteAll(extraOrderProducts: List<ExtraOrderProductModel>) {
        extraOrderProducts.forEach { delete(it) }
    }

    fun create(
        extraOrder: ExtraOrderModel,
        productsInfo: List<ExtraOrderProductCreateSchema>
    ): List<ExtraOrderProductModel> {
        val extraOrderProducts = productsInfo.map { p ->
            val product = productService.findById(p.productId)
            ExtraOrderProductModel(
                product = product,
                extraOrder = extraOrder,
                price = p.price,
                quantity = p.quantity
            )
        }
        return extraOrderProducts
    }

    fun edit(
        extraOrder: ExtraOrderModel,
        products: List<ExtraOrderProductCreateSchema>
    ): MutableList<ExtraOrderProductModel> {
        val currentProductsSet = extraOrder.products.map { it.product.id }.toSet()
        val newProductsSet = products.map { it.productId }.toSet()

        val productsToRemove = extraOrder.products.filter { it.product.id !in newProductsSet }
        extraOrder.products.removeAll(productsToRemove)
        deleteAll(productsToRemove)

        val productsToAdd = products.filter { it.productId !in currentProductsSet }
        val newProducts = productsToAdd.map { productInfo ->
            val product = productService.findById(productInfo.productId)

            ExtraOrderProductModel(
                product = product,
                extraOrder = extraOrder,
                price = productInfo.price,
                quantity = productInfo.quantity
            )
        }
        extraOrder.products.addAll(newProducts)

        val productsToUpdate = extraOrder.products.filter { it.product.id in newProductsSet }
        productsToUpdate.forEach { extraOrderProduct ->
            val productInfo = products.find { it.productId == extraOrderProduct.product.id }!!
            extraOrderProduct.price = productInfo.price
            extraOrderProduct.quantity = productInfo.quantity
        }

        return extraOrder.products
    }
}