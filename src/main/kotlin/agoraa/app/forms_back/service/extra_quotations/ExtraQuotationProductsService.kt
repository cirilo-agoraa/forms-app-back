package agoraa.app.forms_back.service.extra_quotations

import agoraa.app.forms_back.dto.extra_quotations.ExtraQuotationProductsDto
import agoraa.app.forms_back.model.extra_quotations.ExtraQuotationModel
import agoraa.app.forms_back.model.extra_quotations.ExtraQuotationProductsModel
import agoraa.app.forms_back.repository.extra_quotations.ExtraQuotationProductsRepository
import agoraa.app.forms_back.schema.extra_quotations.ExtraQuotationProductsCreateSchema
import agoraa.app.forms_back.schema.extra_quotations.ExtraQuotationProductsEditSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class ExtraQuotationProductsService(private val extraQuotationProductsRepository: ExtraQuotationProductsRepository) {
    private fun editMultiple(
        extraQuotationProducts: List<ExtraQuotationProductsModel>,
        products: List<ExtraQuotationProductsEditSchema>
    ) {
        val editedExtraQuotationProducts = extraQuotationProducts.map { extraQuotationProduct ->
            val updatedEqp = products.find { it.product == extraQuotationProduct.product }
            extraQuotationProduct.copy(
                motive = updatedEqp?.motive ?: extraQuotationProduct.motive,
            )
        }
        extraQuotationProductsRepository.saveAllAndFlush(editedExtraQuotationProducts)
    }

    private fun createCriteria(
        extraQuotation: Long? = null,
    ): Specification<ExtraQuotationProductsModel> {
        return Specification { root: Root<ExtraQuotationProductsModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            extraQuotation?.let {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get<ExtraQuotationModel>("extraQuotation").get<Long>("id"), it
                    )
                )
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun createDto(extraOrderProducts: ExtraQuotationProductsModel): ExtraQuotationProductsDto {
        return ExtraQuotationProductsDto(
            id = extraOrderProducts.id,
            product = extraOrderProducts.product,
            motive = extraOrderProducts.motive,
        )
    }

    fun findByParentId(
        extraQuotationId: Long,
    ): List<ExtraQuotationProductsDto> {
        val spec = createCriteria(extraQuotationId)

        return extraQuotationProductsRepository.findAll(spec).map { createDto(it) }
    }

    fun create(extraQuotation: ExtraQuotationModel, products: List<ExtraQuotationProductsCreateSchema>) {
        val extraQuotationProducts = products.map { p ->
            ExtraQuotationProductsModel(
                extraQuotation = extraQuotation,
                product = p.product,
                motive = p.motive,
            )
        }
        extraQuotationProductsRepository.saveAll(extraQuotationProducts)
    }

    fun edit(extraQuotation: ExtraQuotationModel, products: List<ExtraQuotationProductsEditSchema>) {
        val spec = createCriteria(extraQuotation.id)
        val extraQuotationProducts = extraQuotationProductsRepository.findAll(spec)
        val editExtraQuotationProductsSet = products.map { it.product }.toSet()

        val toAdd = products.filter { it.product !in editExtraQuotationProductsSet }
        val newExtraQuotationProducts = toAdd.map { p ->
            ExtraQuotationProductsModel(
                extraQuotation = extraQuotation,
                product = p.product ?: throw IllegalArgumentException("Product is required"),
                motive = p.motive ?: throw IllegalArgumentException("Motive is required"),
            )
        }
        extraQuotationProductsRepository.saveAll(newExtraQuotationProducts)

        val toDelete = extraQuotationProducts.filter { it.product !in editExtraQuotationProductsSet }
        extraQuotationProductsRepository.deleteAll(toDelete)

        val toEdit = extraQuotationProducts.filter { it.product in editExtraQuotationProductsSet }
        editMultiple(toEdit, products)
    }
}