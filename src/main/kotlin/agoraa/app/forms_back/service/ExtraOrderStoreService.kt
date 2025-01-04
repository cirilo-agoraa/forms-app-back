package agoraa.app.forms_back.service

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.ExtraOrderModel
import agoraa.app.forms_back.model.ExtraOrderStoreModel
import agoraa.app.forms_back.repository.ExtraOrderStoreRepository
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class ExtraOrderStoreService(
    private val extraOrderStoreRepository: ExtraOrderStoreRepository
) {

    private fun createCriteria(
        extraOrder: Long,
        store: String
    ): Specification<ExtraOrderStoreModel> {
        return Specification { root: Root<ExtraOrderStoreModel>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            if (extraOrder != 0L) {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get<ExtraOrderModel>("extraOrder").get<Long>("id"),
                        extraOrder
                    )
                )
            }

            if (store.isNotEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get<StoresEnum>("store"), store))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun findAll(
        pagination: Boolean,
        extraOrder: Long,
        store: String,
        page: Int,
        size: Int,
        sort: String,
        direction: String
    ): Any {
        val spec = createCriteria(extraOrder, store)

        return if (pagination) {
            val sortDirection =
                if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
            val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))

            extraOrderStoreRepository.findAll(spec, pageable)
        } else {
            extraOrderStoreRepository.findAll(spec)
        }
    }

    fun findById(id: Long): ExtraOrderStoreModel {
        return extraOrderStoreRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Extra Order Store not found.") }
    }

    fun findByExtraOrderId(extraOrderId: Long): List<ExtraOrderStoreModel> {
        return extraOrderStoreRepository.findByExtraOrderId(extraOrderId)
    }

    fun create(extraOrder: ExtraOrderModel, stores: List<String>): List<ExtraOrderStoreModel> {
        val storesComplete = stores.map { store ->
            ExtraOrderStoreModel(
                extraOrder = extraOrder,
                store = StoresEnum.valueOf(store)
            )
        }
        return storesComplete
    }

    fun delete(extraOrderStore: ExtraOrderStoreModel) {
        val foundExtraOrderStore = extraOrderStoreRepository.findById(extraOrderStore.id)
            .map { extraOrderStoreRepository.delete(it) }
            .orElseThrow { ResourceNotFoundException("Extra Order Store not found.") }
    }

    fun deleteAll(extraOrderStores: List<ExtraOrderStoreModel>) {
        extraOrderStores.forEach { delete(it) }
    }

    fun edit(extraOrder: ExtraOrderModel, stores: List<String>): MutableList<ExtraOrderStoreModel> {
        val currentStoresSet = extraOrder.stores.map { it.store }.toSet()
        val newStoresToSet = stores.map { StoresEnum.valueOf(it) }.toSet()

        val storesToRemove = extraOrder.stores.filter { it.store !in newStoresToSet }
        extraOrder.stores.removeAll(storesToRemove)
        deleteAll(storesToRemove)

        val storesToAdd = newStoresToSet.minus(currentStoresSet)
        val newStores = storesToAdd.map { store ->
            ExtraOrderStoreModel(
                extraOrder = extraOrder,
                store = store
            )
        }
        extraOrder.stores.addAll(newStores)

        return extraOrder.stores
    }

}