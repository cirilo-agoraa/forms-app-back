package agoraa.app.forms_back.supplier_registrations.supplier_registratio_weekly_quotations.service

import agoraa.app.forms_back.supplier_registrations.supplier_registratio_weekly_quotations.dto.response.SupplierRegistrationWeeklyQuotationsDto
import agoraa.app.forms_back.supplier_registrations.supplier_registrations.model.SupplierRegistrationModel
import agoraa.app.forms_back.supplier_registrations.supplier_registratio_weekly_quotations.model.SupplierRegistrationWeeklyQuotationsModel
import agoraa.app.forms_back.supplier_registrations.supplier_registratio_weekly_quotations.repository.SupplierRegistrationWeeklyQuotationsRepository
import agoraa.app.forms_back.supplier_registrations.supplier_registratio_weekly_quotations.dto.request.SupplierRegistrationWeeklyQuotationsSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class SupplierRegistrationWeeklyQuotationService(
    private val supplierRegistrationWeeklyQuotationsRepository: SupplierRegistrationWeeklyQuotationsRepository
) {

    private fun createCriteria(
        supplierRegistration: Long? = null,
    ): Specification<SupplierRegistrationWeeklyQuotationsModel> {
        return Specification { root: Root<SupplierRegistrationWeeklyQuotationsModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
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

    fun createDto(supplierRegistrationWeeklyQuotations: SupplierRegistrationWeeklyQuotationsModel): SupplierRegistrationWeeklyQuotationsDto {
        return SupplierRegistrationWeeklyQuotationsDto(
            id = supplierRegistrationWeeklyQuotations.id,
            weeklyQuotation = supplierRegistrationWeeklyQuotations.weeklyQuotation,
        )
    }

    fun findByParentId(
        supplierRegistrationId: Long,
    ): List<SupplierRegistrationWeeklyQuotationsDto> {
        val spec = createCriteria(supplierRegistrationId)

        return supplierRegistrationWeeklyQuotationsRepository.findAll(spec).map { createDto(it) }
    }

    fun create(
        supplierRegistration: SupplierRegistrationModel,
        weeklyQuotations: List<SupplierRegistrationWeeklyQuotationsSchema>
    ) {
        val supplierRegistrationWeeklyQuotations = weeklyQuotations.map { p ->
            SupplierRegistrationWeeklyQuotationsModel(
                supplierRegistration = supplierRegistration,
                weeklyQuotation = p.weeklyQuotation,
            )
        }
        supplierRegistrationWeeklyQuotationsRepository.saveAll(supplierRegistrationWeeklyQuotations)
    }

    fun edit(
        supplierRegistration: SupplierRegistrationModel,
        weeklyQuotations: List<SupplierRegistrationWeeklyQuotationsSchema>
    ) {
        val spec = createCriteria(supplierRegistration.id)
        val supplierRegistrationWeeklyQuotations = supplierRegistrationWeeklyQuotationsRepository.findAll(spec)
        val currentSpWkSet = supplierRegistrationWeeklyQuotations.map { it.weeklyQuotation }.toSet()
        val newSpWkSet = weeklyQuotations.map { it.weeklyQuotation }.toSet()

        val toAdd = weeklyQuotations.filter { it.weeklyQuotation !in currentSpWkSet }
        create(supplierRegistration, toAdd)

        val toDelete = supplierRegistrationWeeklyQuotations.filter { it.weeklyQuotation !in newSpWkSet }
        supplierRegistrationWeeklyQuotationsRepository.deleteAll(toDelete)
    }
}