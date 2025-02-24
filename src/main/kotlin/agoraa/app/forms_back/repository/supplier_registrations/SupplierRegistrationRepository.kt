package agoraa.app.forms_back.repository.supplier_registrations

import agoraa.app.forms_back.model.supplier_registrations.SupplierRegistrationModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface SupplierRegistrationRepository : JpaRepository<SupplierRegistrationModel, Long>,
    JpaSpecificationExecutor<SupplierRegistrationModel>