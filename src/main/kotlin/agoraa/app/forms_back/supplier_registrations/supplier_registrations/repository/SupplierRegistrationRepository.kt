package agoraa.app.forms_back.supplier_registrations.supplier_registrations.repository

import agoraa.app.forms_back.supplier_registrations.supplier_registrations.model.SupplierRegistrationModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface SupplierRegistrationRepository : JpaRepository<SupplierRegistrationModel, Long>,
    JpaSpecificationExecutor<SupplierRegistrationModel>