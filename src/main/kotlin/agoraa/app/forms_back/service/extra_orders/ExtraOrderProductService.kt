package agoraa.app.forms_back.service.extra_orders

import agoraa.app.forms_back.dto.extra_order.ExtraOrderProductsDto
import agoraa.app.forms_back.model.extra_orders.ExtraOrderModel
import agoraa.app.forms_back.model.extra_orders.ExtraOrderProductsModel
import agoraa.app.forms_back.repository.extra_orders.ExtraOrderProductRepository
import agoraa.app.forms_back.schema.extra_order.ExtraOrderProductCreateSchema
import agoraa.app.forms_back.schema.extra_order.ExtraOrderProductEditSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class ExtraOrderProductService(
    private val extraOrderProductRepository: ExtraOrderProductRepository,
) {
    private fun editMultiple(
        extraOrderProducts: List<ExtraOrderProductsModel>,
        products: List<ExtraOrderProductEditSchema>
    ) {
        val editedExtraOrderProducts = extraOrderProducts.map { extraOrderProducts ->
            val updatedSps = products.find { it.product == extraOrderProducts.product }
            extraOrderProducts.copy(
                quantity = updatedSps?.quantity ?: extraOrderProducts.quantity,
                price = updatedSps?.price ?: extraOrderProducts.price,
            )
        }
        extraOrderProductRepository.saveAllAndFlush(editedExtraOrderProducts)
    }

    private fun createCriteria(
        extraOrder: Long? = null,
    ): Specification<ExtraOrderProductsModel> {
        return Specification { root: Root<ExtraOrderProductsModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            extraOrder?.let {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get<ExtraOrderModel>("extraOrder").get<Long>("id"), it
                    )
                )
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun createDto(extraOrderProducts: ExtraOrderProductsModel): ExtraOrderProductsDto {
        return ExtraOrderProductsDto(
            id = extraOrderProducts.id,
            product = extraOrderProducts.product,
            quantity = extraOrderProducts.quantity,
            price = extraOrderProducts.price,
        )
    }

    fun findByParentId(
        extraOrderId: Long,
    ): List<ExtraOrderProductsDto> {
        val spec = createCriteria(extraOrderId)

        return extraOrderProductRepository.findAll(spec).map { createDto(it) }
    }

    fun create(extraOrder: ExtraOrderModel, products: List<ExtraOrderProductCreateSchema>) {
        val extraOrderStores = products.map { p ->
            ExtraOrderProductsModel(
                extraOrder = extraOrder,
                product = p.product,
                quantity = p.quantity,
                price = p.price,
            )
        }
        extraOrderProductRepository.saveAll(extraOrderStores)
    }

    fun edit(extraOrder: ExtraOrderModel, products: List<ExtraOrderProductEditSchema>) {
        val spec = createCriteria(extraOrder.id)
        val extraOrderProducts = extraOrderProductRepository.findAll(spec)
        val editExtraOrderProductsSet = products.map { it.product }.toSet()

        val toAdd = products.filter { it.product !in editExtraOrderProductsSet }
        val newExtraOrderProducts = toAdd.map { p ->
            ExtraOrderProductsModel(
                extraOrder = extraOrder,
                product = p.product ?: throw IllegalArgumentException("Product is required"),
                quantity = p.quantity ?: throw IllegalArgumentException("Quantity is required"),
                price = p.price ?: throw IllegalArgumentException("Price is required"),
            )
        }
        extraOrderProductRepository.saveAll(newExtraOrderProducts)

        val toDelete = extraOrderProducts.filter { it.product !in editExtraOrderProductsSet }
        extraOrderProductRepository.deleteAll(toDelete)

        val toEdit = extraOrderProducts.filter { it.product in editExtraOrderProductsSet }
        editMultiple(toEdit, products)
    }
}