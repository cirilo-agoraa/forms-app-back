package agoraa.app.forms_back.passive_quotations.passive_quotation_products.service

import agoraa.app.forms_back.passive_quotations.passive_quotation_products.dto.request.PassiveQuotationProductsRequest
import agoraa.app.forms_back.passive_quotations.passive_quotation_products.dto.response.PassiveQuotationProductsResponse
import agoraa.app.forms_back.passive_quotations.passive_quotation_products.model.PassiveQuotationProductsModel
import agoraa.app.forms_back.passive_quotations.passive_quotation_products.repository.PassiveQuotationProductsRepository
import agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel
import org.springframework.stereotype.Service

@Service
class PassiveQuotationProductsService(private val passiveQuotationProductsRepository: PassiveQuotationProductsRepository) {
    private fun create(
        passiveQuotation: PassiveQuotationModel,
        products: List<PassiveQuotationProductsRequest>
    ) {
        val passiveQuotationProducts = products.map { p ->
            PassiveQuotationProductsModel(
                passiveQuotation = passiveQuotation,
                product = p.product,
                quantity = p.quantity,
                price = p.price,
                stockPlusOpenOrder = p.stockPlusOpenOrder,
            )
        }
        passiveQuotationProductsRepository.saveAll(passiveQuotationProducts)
    }

    private fun edit(products: List<PassiveQuotationProductsModel>, request: List<PassiveQuotationProductsRequest>) {
        val productsMap = request.associateBy { it.product.code }
        val passiveQuotationProducts = products.map { p ->
            val requestProduct = productsMap[p.product.code]!!
            p.copy(
                product = requestProduct.product,
                quantity = requestProduct.quantity,
                price = requestProduct.price,
                stockPlusOpenOrder = requestProduct.stockPlusOpenOrder,
            )
        }
        passiveQuotationProductsRepository.saveAll(passiveQuotationProducts)
    }

    fun findByParentId(
        passiveQuotationId: Long,
    ): List<PassiveQuotationProductsModel> = passiveQuotationProductsRepository.findByPassiveQuotationId(passiveQuotationId)

    fun createDto(
        passiveQuotationProduct: PassiveQuotationProductsModel,
    ): PassiveQuotationProductsResponse {
        return PassiveQuotationProductsResponse(
            id = passiveQuotationProduct.id,
            price = passiveQuotationProduct.price,
            quantity = passiveQuotationProduct.quantity,
            stockPlusOpenOrder = passiveQuotationProduct.stockPlusOpenOrder,
            product = passiveQuotationProduct.product
        )
    }

    fun editOrCreateOrDelete(
        passiveQuotation: PassiveQuotationModel,
        products: List<PassiveQuotationProductsRequest>
    ) {
        val passiveQuotationProducts = findByParentId(passiveQuotation.id)
        val currentProductsSet = passiveQuotationProducts.map { it.product }.toSet()
        val newProductsSet = products.map { it.product }.toSet()

        val toAdd = products.filter { it.product !in currentProductsSet }
        create(passiveQuotation, toAdd)

        val toDelete = passiveQuotationProducts.filter { it.product !in newProductsSet }
        passiveQuotationProductsRepository.deleteAll(toDelete)

        val toEdit = passiveQuotationProducts.filter { it.product in newProductsSet }
        edit(toEdit, products)
    }
}