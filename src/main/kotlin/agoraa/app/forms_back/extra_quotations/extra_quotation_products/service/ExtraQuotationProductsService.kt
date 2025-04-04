package agoraa.app.forms_back.extra_quotations.extra_quotation_products.service

import agoraa.app.forms_back.extra_quotations.extra_quotation_products.dto.request.ExtraQuotationProductsRequest
import agoraa.app.forms_back.extra_quotations.extra_quotation_products.dto.response.ExtraQuotationProductsResponse
import agoraa.app.forms_back.extra_quotations.extra_quotation_products.model.ExtraQuotationProductsModel
import agoraa.app.forms_back.extra_quotations.extra_quotation_products.repository.ExtraQuotationProductsRepository
import agoraa.app.forms_back.extra_quotations.extra_quotations.model.ExtraQuotationModel
import org.springframework.stereotype.Service

@Service
class ExtraQuotationProductsService(private val extraQuotationProductsRepository: ExtraQuotationProductsRepository) {
    private fun create(
        extraQuotation: ExtraQuotationModel,
        products: List<ExtraQuotationProductsRequest>
    ) {
        val extraTransfersProducts = products.map { p ->
            ExtraQuotationProductsModel(
                extraQuotation = extraQuotation,
                product = p.product,
                motive = p.motive
            )
        }
        extraQuotationProductsRepository.saveAll(extraTransfersProducts)
    }

    private fun edit(products: List<ExtraQuotationProductsModel>, request: List<ExtraQuotationProductsRequest>) {
        val productsMap = request.associateBy { it.product.code }
        val extraQuotationProducts = products.map { p ->
            val requestProduct = productsMap[p.product.code]!!
            p.copy(
                product = requestProduct.product,
                motive = requestProduct.motive
            )
        }
        extraQuotationProductsRepository.saveAll(extraQuotationProducts)
    }

    fun findByParentId(
        extraQuotationId: Long,
    ): List<ExtraQuotationProductsResponse> =
        extraQuotationProductsRepository.findByExtraQuotationId(extraQuotationId).map { createDto(it) }

    fun createDto(extraQuotationProducts: ExtraQuotationProductsModel): ExtraQuotationProductsResponse {
        return ExtraQuotationProductsResponse(
            id = extraQuotationProducts.id,
            product = extraQuotationProducts.product,
            motive = extraQuotationProducts.motive
        )
    }

    fun editOrCreateOrDelete(
        extraQuotation: ExtraQuotationModel,
        products: List<ExtraQuotationProductsRequest>
    ) {
        val extraQuotationProducts = extraQuotationProductsRepository.findByExtraQuotationId(extraQuotation.id)
        val currentProductsSet = extraQuotationProducts.map { it.product }.toSet()
        val newProductsSet = products.map { it.product }.toSet()

        val toAdd = products.filter { it.product !in currentProductsSet }
        create(extraQuotation, toAdd)

        val toDelete = extraQuotationProducts.filter { it.product !in newProductsSet }
        extraQuotationProductsRepository.deleteAll(toDelete)

        val toEdit = extraQuotationProducts.filter { it.product in newProductsSet }
        edit(toEdit, products)
    }
}