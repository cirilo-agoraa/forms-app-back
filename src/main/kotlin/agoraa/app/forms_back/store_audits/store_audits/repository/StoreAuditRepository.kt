package agoraa.app.forms_back.store_audits.store_audits.repository

import agoraa.app.forms_back.store_audits.store_audits.model.StoreAuditModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface StoreAuditRepository : JpaRepository<StoreAuditModel, Long>, JpaSpecificationExecutor<StoreAuditModel> {
}