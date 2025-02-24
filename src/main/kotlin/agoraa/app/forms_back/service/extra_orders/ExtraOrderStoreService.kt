package agoraa.app.forms_back.service.extra_orders

import agoraa.app.forms_back.dto.extra_order.ExtraOrderStoresDto
import agoraa.app.forms_back.model.extra_orders.ExtraOrderModel
import agoraa.app.forms_back.model.extra_orders.ExtraOrderStoresModel
import agoraa.app.forms_back.repository.extra_orders.ExtraOrderStoreRepository
import agoraa.app.forms_back.schema.extra_order.ExtraOrderStoresCreateSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class ExtraOrderStoreService(
    private val extraOrderStoreRepository: ExtraOrderStoreRepository
) {

    private fun createCriteria(
        extraOrder: Long? = null,
    ): Specification<ExtraOrderStoresModel> {
        return Specification { root: Root<ExtraOrderStoresModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
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

    fun createDto(supplierRegistrationStores: ExtraOrderStoresModel): ExtraOrderStoresDto {
        return ExtraOrderStoresDto(
            id = supplierRegistrationStores.id,
            store = supplierRegistrationStores.store,
        )
    }

    fun findByParentId(
        supplierRegistrationId: Long,
    ): List<ExtraOrderStoresDto> {
        val spec = createCriteria(supplierRegistrationId)

        return extraOrderStoreRepository.findAll(spec).map { createDto(it) }
    }

    fun create(extraOrder: ExtraOrderModel, stores: List<ExtraOrderStoresCreateSchema>) {
        val extraOrderStores = stores.map { p ->
            ExtraOrderStoresModel(
                extraOrder = extraOrder,
                store = p.store,
            )
        }
        extraOrderStoreRepository.saveAll(extraOrderStores)
    }

    fun edit(
        extraOrder: ExtraOrderModel,
        stores: List<ExtraOrderStoresCreateSchema>
    ) {
        val spec = createCriteria(extraOrder.id)
        val extraOrderStores = extraOrderStoreRepository.findAll(spec)
        val currentExtraOrderStoresSet = extraOrderStores.map { it.store }.toSet()
        val newExtraOrderStoresSet = stores.map { it.store }.toSet()

        val toAdd = stores.filter { it.store !in currentExtraOrderStoresSet }
        create(extraOrder, toAdd)

        val toDelete = extraOrderStores.filter { it.store !in newExtraOrderStoresSet }
        extraOrderStoreRepository.deleteAll(toDelete)
    }

}