package agoraa.app.forms_back.passive_quotations.service

import agoraa.app.forms_back.passive_quotations.model.PassiveQuotationModel
import agoraa.app.forms_back.passive_quotations.model.PassiveQuotationProductsModel
import agoraa.app.forms_back.passive_quotations.repository.PassiveQuotationProductsRepository
import agoraa.app.forms_back.passive_quotations.dto.request.PassiveQuotationProductsRequest
import org.springframework.stereotype.Service

@Service
class PassiveQuotationProductsService(private val passiveQuotationProductsRepository: PassiveQuotationProductsRepository) {
    private fun create(passiveQuotation: PassiveQuotationModel, products: List<PassiveQuotationProductsRequest>) {
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

    fun editOrCreate(passiveQuotation: PassiveQuotationModel, products: List<PassiveQuotationProductsRequest>) {
        val passiveQuotationProducts = findByParentId(passiveQuotation.id)
        val productsSet = products.map { it.product }.toSet()

        val toAdd = products.filter { it.product !in productsSet }
        create(passiveQuotation, toAdd)

        val toDelete = passiveQuotationProducts.filter { it.product !in productsSet }
        passiveQuotationProductsRepository.deleteAll(toDelete)

        val toEdit = passiveQuotationProducts.filter { it.product in productsSet }
        edit(toEdit)
    }
}