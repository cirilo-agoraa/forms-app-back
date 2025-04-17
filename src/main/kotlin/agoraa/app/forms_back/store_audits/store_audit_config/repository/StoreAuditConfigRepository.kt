package agoraa.app.forms_back.store_audits.store_audit_config.repository

import agoraa.app.forms_back.store_audits.store_audit_config.model.StoreAuditConfigModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoreAuditConfigRepository: JpaRepository<StoreAuditConfigModel, Long> {
}