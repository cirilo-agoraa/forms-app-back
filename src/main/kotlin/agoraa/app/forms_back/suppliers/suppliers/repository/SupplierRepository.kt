package agoraa.app.forms_back.suppliers.suppliers.repository

import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SupplierRepository : JpaRepository<SupplierModel, Long>, JpaSpecificationExecutor<SupplierModel> {
    fun findByName(name: String): Optional<SupplierModel>
}