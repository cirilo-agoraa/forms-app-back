package agoraa.app.forms_back.store_audits.store_audit_config.service

import agoraa.app.forms_back.shared.exception.ResourceNotFoundException
import agoraa.app.forms_back.store_audits.store_audit_config.dto.request.StoreAuditConfigRequest
import agoraa.app.forms_back.store_audits.store_audit_config.dto.response.StoreAuditConfigResponse
import agoraa.app.forms_back.store_audits.store_audit_config.model.StoreAuditConfigGroupsModel
import agoraa.app.forms_back.store_audits.store_audit_config.model.StoreAuditConfigModel
import agoraa.app.forms_back.store_audits.store_audit_config.model.StoreAuditConfigSectorsModel
import agoraa.app.forms_back.store_audits.store_audit_config.repository.StoreAuditConfigRepository
import org.springframework.stereotype.Service

@Service
class StoreAuditConfigService(private val storeAuditConfigRepository: StoreAuditConfigRepository) {

    private fun findConfig(): StoreAuditConfigModel {
        return storeAuditConfigRepository.findById(1L)
            .orElseThrow { ResourceNotFoundException("StoreAuditConfigModel not found") }
    }

    fun editConfig(request: StoreAuditConfigRequest) {
        val storeAuditConfigModel = findConfig()

        val editedStoreAuditConfigModel = storeAuditConfigModel.copy(
            daysToNotRepeatProducts = request.daysToNotRepeatProducts,
            dailyProductsLimit = request.dailyProductsLimit,
            formDurationDays = request.formDurationDays
        )

        editedStoreAuditConfigModel.excludeSectors = request.excludeSectors.map {
            StoreAuditConfigSectorsModel(
                storeAuditConfig = storeAuditConfigModel,
                sector = it
            )
        }.toMutableList()

        editedStoreAuditConfigModel.excludeGroups = request.excludeGroups.map {
            StoreAuditConfigGroupsModel(
                storeAuditConfig = storeAuditConfigModel,
                groupName = it
            )
        }.toMutableList()

        storeAuditConfigRepository.save(editedStoreAuditConfigModel)
    }

    fun returnConfig(): StoreAuditConfigResponse {
        val storeAuditConfigModel = findConfig()

        return StoreAuditConfigResponse(
            id = storeAuditConfigModel.id,
            excludeGroups = storeAuditConfigModel.excludeGroups.map { it.groupName },
            excludeSectors = storeAuditConfigModel.excludeSectors.map { it.sector },
            daysToNotRepeatProducts = storeAuditConfigModel.daysToNotRepeatProducts,
            dailyProductsLimit = storeAuditConfigModel.dailyProductsLimit,
            formDurationDays = storeAuditConfigModel.formDurationDays
        )
    }
}