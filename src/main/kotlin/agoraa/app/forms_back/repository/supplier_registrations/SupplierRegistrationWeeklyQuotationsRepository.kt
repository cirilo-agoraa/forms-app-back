package agoraa.app.forms_back.repository.supplier_registrations

import agoraa.app.forms_back.model.supplier_registrations.SupplierRegistrationWeeklyQuotationsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface SupplierRegistrationWeeklyQuotationsRepository :
    JpaRepository<SupplierRegistrationWeeklyQuotationsModel, Long>,
    JpaSpecificationExecutor<SupplierRegistrationWeeklyQuotationsModel>