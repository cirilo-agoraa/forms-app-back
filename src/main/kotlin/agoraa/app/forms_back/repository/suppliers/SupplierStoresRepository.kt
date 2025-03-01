package agoraa.app.forms_back.repository.suppliers

import agoraa.app.forms_back.model.suppliers.SupplierStoresModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface SupplierStoresRepository : JpaRepository<SupplierStoresModel, Long>,
    JpaSpecificationExecutor<SupplierStoresModel> {
}