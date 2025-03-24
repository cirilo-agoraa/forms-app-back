package agoraa.app.forms_back.passive_quotations.passive_quotation_products.service

import agoraa.app.forms_back.passive_quotations.passive_quotation_products.dto.request.PassiveQuotationProductsRequest
import agoraa.app.forms_back.passive_quotations.passive_quotation_products.model.PassiveQuotationProductsModel
import agoraa.app.forms_back.passive_quotations.passive_quotation_products.repository.PassiveQuotationProductsRepository
import org.springframework.stereotype.Service

@Service
class PassiveQuotationProductsService(private val passiveQuotationProductsRepository: PassiveQuotationProductsRepository) {
    private fun create(
        passiveQuotation: agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel,
        products: List<PassiveQuotationProductsRequest>
    ) {
        val passiveQuotationProducts = products.map { p ->
            PassiveQuotationProductsModel(
                passiveQuotation = passiveQuotation,
                product = p.product,
                quantity = p.quantity,
                price = p.price,
                stockPlusOpenOrder = p.stockPlusOpenOrder,
                total = p.total,
            )
        }
        passiveQuotationProductsRepository.saveAll(passiveQuotationProducts)
    }

    private fun edit(products: List<PassiveQuotationProductsModel>) {
        val passiveQuotationProducts = products.map { p ->
            p.copy(
                product = p.product,
                quantity = p.quantity,
                price = p.price,
                stockPlusOpenOrder = p.stockPlusOpenOrder,
                total = p.total,
            )
        }
        passiveQuotationProductsRepository.saveAll(passiveQuotationProducts)
    }

    fun findByParentId(
        passiveQuotationId: Long,
    ): List<PassiveQuotationProductsModel> {
        return passiveQuotationProductsRepository.findByPassiveQuotationId(passiveQuotationId)
    }

    fun editOrCreateOrDelete(
        passiveQuotation: agoraa.app.forms_back.passive_quotations.passive_quotations.model.PassiveQuotationModel,
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
        edit(toEdit)
    }
}