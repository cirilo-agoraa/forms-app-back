package agoraa.app.forms_back.suppliers.supplier_stores.repository

import agoraa.app.forms_back.suppliers.supplier_stores.model.SupplierStoresModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface SupplierStoresRepository : JpaRepository<SupplierStoresModel, Long>,
    JpaSpecificationExecutor<SupplierStoresModel> {
}