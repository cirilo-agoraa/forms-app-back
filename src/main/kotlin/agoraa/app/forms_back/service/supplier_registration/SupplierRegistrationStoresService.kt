package agoraa.app.forms_back.service.supplier_registration

import agoraa.app.forms_back.dto.supplier_registration.SupplierRegistrationStoresDto
import agoraa.app.forms_back.model.supplier_registrations.SupplierRegistrationModel
import agoraa.app.forms_back.model.supplier_registrations.SupplierRegistrationStoresModel
import agoraa.app.forms_back.repository.SupplierRegistrationStoresRepository
import agoraa.app.forms_back.schema.supplier_registration.SupplierRegistrationStoresCreateSchema
import agoraa.app.forms_back.schema.supplier_registration.SupplierRegistrationStoresEditSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class SupplierRegistrationStoresService(
    private val supplierRegistrationStoresRepository: SupplierRegistrationStoresRepository
) {

    private fun editMultiple(
        supplierRegistrationStores: List<SupplierRegistrationStoresModel>,
        stores: List<SupplierRegistrationStoresEditSchema>
    ) {
        val editedSupplierRegistrationStores = supplierRegistrationStores.map { srs ->
            val updatedSps = stores.find { it.store == srs.store }
            srs.copy(
                deliveryTime = updatedSps?.deliveryTime ?: srs.deliveryTime,
                orderBestDay = updatedSps?.orderBestDay ?: srs.orderBestDay,
                routine = updatedSps?.routine ?: srs.routine,
                motive = updatedSps?.motive ?: srs.motive,
                sellerName = updatedSps?.sellerName ?: srs.sellerName,
                sellerPhone = updatedSps?.sellerPhone ?: srs.sellerPhone
            )
        }
        supplierRegistrationStoresRepository.saveAllAndFlush(editedSupplierRegistrationStores)
    }

    private fun createCriteria(
        supplierRegistration: Long? = null,
    ): Specification<SupplierRegistrationStoresModel> {
        return Specification { root: Root<SupplierRegistrationStoresModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            supplierRegistration?.let {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get<SupplierRegistrationModel>("supplierRegistration").get<Long>("id"), it
                    )
                )
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun createDto(supplierRegistrationStores: SupplierRegistrationStoresModel): SupplierRegistrationStoresDto {
        return SupplierRegistrationStoresDto(
            id = supplierRegistrationStores.id,
            store = supplierRegistrationStores.store,
            deliveryTime = supplierRegistrationStores.deliveryTime,
            orderBestDay = supplierRegistrationStores.orderBestDay,
            routine = supplierRegistrationStores.routine,
            motive = supplierRegistrationStores.motive,
            sellerName = supplierRegistrationStores.sellerName,
            sellerPhone = supplierRegistrationStores.sellerPhone,
        )
    }

    fun findByParentId(
        supplierRegistrationId: Long,
    ): List<SupplierRegistrationStoresDto> {
        val spec = createCriteria(supplierRegistrationId)

        return supplierRegistrationStoresRepository.findAll(spec).map { createDto(it) }
    }

    fun create(supplierRegistration: SupplierRegistrationModel, stores: List<SupplierRegistrationStoresCreateSchema>) {
        val supplierRegistrationStores = stores.map { p ->
            SupplierRegistrationStoresModel(
                supplierRegistration = supplierRegistration,
                store = p.store,
                deliveryTime = p.deliveryTime,
                orderBestDay = p.orderBestDay,
                routine = p.routine,
                motive = p.motive,
                sellerName = p.sellerName,
                sellerPhone = p.sellerPhone
            )
        }
        supplierRegistrationStoresRepository.saveAll(supplierRegistrationStores)
    }

    fun edit(supplierRegistration: SupplierRegistrationModel, stores: List<SupplierRegistrationStoresEditSchema>) {
        val spec = createCriteria(supplierRegistration.id)
        val supplierRegistrationStores = supplierRegistrationStoresRepository.findAll(spec)
        val currentSpsSet = supplierRegistrationStores.map { it.store }.toSet()
        val newSpsSet = stores.map { it.store }.toSet()

        val toAdd = stores.filter { it.store !in currentSpsSet }
        val newSupplierRegistrationStores = toAdd.map { p ->
            SupplierRegistrationStoresModel(
                supplierRegistration = supplierRegistration,
                store = p.store,
                deliveryTime = p.deliveryTime ?: throw IllegalArgumentException("Delivery time is required"),
                orderBestDay = p.orderBestDay,
                routine = p.routine,
                motive = p.motive,
                sellerName = p.sellerName,
                sellerPhone = p.sellerPhone
            )
        }
        supplierRegistrationStoresRepository.saveAll(newSupplierRegistrationStores)

        val toDelete = supplierRegistrationStores.filter { it.store !in newSpsSet }
        supplierRegistrationStoresRepository.deleteAll(toDelete)

        val toEdit = supplierRegistrationStores.filter { it.store in newSpsSet }
        editMultiple(toEdit, stores)
    }
}