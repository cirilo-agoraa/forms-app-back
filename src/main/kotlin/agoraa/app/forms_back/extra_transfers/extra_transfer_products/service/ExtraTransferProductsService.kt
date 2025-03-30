package agoraa.app.forms_back.extra_transfers.extra_transfer_products.service

import agoraa.app.forms_back.extra_transfers.extra_transfer_products.dto.request.ExtraTransferProductsRequest
import agoraa.app.forms_back.extra_transfers.extra_transfer_products.dto.response.ExtraTransferProductsResponse
import agoraa.app.forms_back.extra_transfers.extra_transfer_products.model.ExtraTransferProductsModel
import agoraa.app.forms_back.extra_transfers.extra_transfer_products.repository.ExtraTransferProductsRepository
import agoraa.app.forms_back.extra_transfers.extra_transfers.model.ExtraTransferModel
import org.springframework.stereotype.Service

@Service
class ExtraTransferProductsService(private val extraTransferProductsRepository: ExtraTransferProductsRepository) {
    private fun create(
        extraTransfer: ExtraTransferModel,
        products: List<ExtraTransferProductsRequest>
    ) {
        val extraTransfersProducts = products.map { p ->
            ExtraTransferProductsModel(
                extraTransfer = extraTransfer,
                product = p.product,
                quantity = p.quantity,
            )
        }
        extraTransferProductsRepository.saveAll(extraTransfersProducts)
    }

    private fun edit(products: List<ExtraTransferProductsModel>) {
        val extraTransfersProducts = products.map { p ->
            p.copy(
                product = p.product,
                quantity = p.quantity,
            )
        }
        extraTransferProductsRepository.saveAll(extraTransfersProducts)
    }

    fun findByParentId(
        extraTransferId: Long,
    ): List<ExtraTransferProductsModel> = extraTransferProductsRepository.findByExtraTransferId(extraTransferId)

    fun createDto(extraTransferProducts: ExtraTransferProductsModel): ExtraTransferProductsResponse {
        return ExtraTransferProductsResponse(
            id = extraTransferProducts.id,
            product = extraTransferProducts.product,
            quantity = extraTransferProducts.quantity
        )
    }

    fun editOrCreateOrDelete(
        extraTransfer: ExtraTransferModel,
        products: List<ExtraTransferProductsRequest>
    ) {
        val extraTransferProducts = findByParentId(extraTransfer.id)
        val currentProductsSet = extraTransferProducts.map { it.product }.toSet()
        val newProductsSet = products.map { it.product }.toSet()

        val toAdd = products.filter { it.product !in currentProductsSet }
        create(extraTransfer, toAdd)

        val toDelete = extraTransferProducts.filter { it.product !in newProductsSet }
        extraTransferProductsRepository.deleteAll(toDelete)

        val toEdit = extraTransferProducts.filter { it.product in newProductsSet }
        edit(toEdit)
    }
}