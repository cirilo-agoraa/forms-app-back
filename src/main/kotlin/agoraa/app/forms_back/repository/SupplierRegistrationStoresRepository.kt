package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.supplier_registrations.SupplierRegistrationStoresModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface SupplierRegistrationStoresRepository : JpaRepository<SupplierRegistrationStoresModel, Long>,
    JpaSpecificationExecutor<SupplierRegistrationStoresModel>