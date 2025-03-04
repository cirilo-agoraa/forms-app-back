package agoraa.app.forms_back.service.extra_transfers

import agoraa.app.forms_back.dto.extra_transfers.ExtraTransferProductsDto
import agoraa.app.forms_back.model.extra_quotations.ExtraQuotationModel
import agoraa.app.forms_back.model.extra_transfers.ExtraTransferModel
import agoraa.app.forms_back.model.extra_transfers.ExtraTransferProductsModel
import agoraa.app.forms_back.repository.extra_transfers.ExtraTransferProductsRepository
import agoraa.app.forms_back.schema.extra_transfers.ExtraTransferProductsCreateSchema
import agoraa.app.forms_back.schema.extra_transfers.ExtraTransferProductsEditSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class ExtraTransferProductsService(private val extraTransferProductsRepository: ExtraTransferProductsRepository) {
    private fun editMultiple(
        extraTransferProducts: List<ExtraTransferProductsModel>,
        products: List<ExtraTransferProductsEditSchema>
    ) {
        val editedExtraTransferProducts = extraTransferProducts.map { extraTransferProduct ->
            val updatedEqp = products.find { it.product == extraTransferProduct.product }
            extraTransferProduct.copy(
                quantity = updatedEqp?.quantity ?: extraTransferProduct.quantity,
            )
        }
        extraTransferProductsRepository.saveAllAndFlush(editedExtraTransferProducts)
    }

    private fun createCriteria(
        extraTransferId: Long? = null,
    ): Specification<ExtraTransferProductsModel> {
        return Specification { root: Root<ExtraTransferProductsModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            extraTransferId?.let {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get<ExtraQuotationModel>("extraTransfer").get<Long>("id"), it
                    )
                )
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun createDto(extraTransferProducts: ExtraTransferProductsModel): ExtraTransferProductsDto {
        return ExtraTransferProductsDto(
            id = extraTransferProducts.id,
            product = extraTransferProducts.product,
            quantity = extraTransferProducts.quantity,
        )
    }

    fun findByParentId(
        extraTransferId: Long,
    ): List<ExtraTransferProductsDto> {
        val spec = createCriteria(extraTransferId)

        return extraTransferProductsRepository.findAll(spec).map { createDto(it) }
    }

    fun create(extraTransfer: ExtraTransferModel, products: List<ExtraTransferProductsCreateSchema>) {
        val extraTransferProducts = products.map { p ->
            ExtraTransferProductsModel(
                extraTransfer = extraTransfer,
                product = p.product,
                quantity = p.quantity,
            )
        }
        extraTransferProductsRepository.saveAll(extraTransferProducts)
    }

    fun edit(extraTransfer: ExtraTransferModel, products: List<ExtraTransferProductsEditSchema>) {
        val spec = createCriteria(extraTransfer.id)
        val extraTransferProducts = extraTransferProductsRepository.findAll(spec)
        val editExtraTransferProductsSet = products.map { it.product }.toSet()

        val toAdd = products.filter { it.product !in editExtraTransferProductsSet }
        val newExtraTransferProducts = toAdd.map { p ->
            ExtraTransferProductsModel(
                extraTransfer = extraTransfer,
                product = p.product ?: throw IllegalArgumentException("Product is required"),
                quantity = p.quantity ?: throw IllegalArgumentException("Quantity is required"),
            )
        }
        extraTransferProductsRepository.saveAll(newExtraTransferProducts)

        val toDelete = extraTransferProducts.filter { it.product !in editExtraTransferProductsSet }
        extraTransferProductsRepository.deleteAll(toDelete)

        val toEdit = extraTransferProducts.filter { it.product in editExtraTransferProductsSet }
        editMultiple(toEdit, products)
    }
}