package agoraa.app.forms_back.store_audits.store_audit_products.repository

import agoraa.app.forms_back.store_audits.store_audit_products.model.StoreAuditProductsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoreAuditProductsRepository: JpaRepository<StoreAuditProductsModel, Long> {
    fun findByStoreAuditId(storeAuditId: Long): List<StoreAuditProductsModel>
}