package agoraa.app.forms_back.store_audits.store_audit_config.dto.request

import agoraa.app.forms_back.shared.enums.ProductGroupsEnum
import agoraa.app.forms_back.shared.enums.ProductSectorsEnum

data class StoreAuditConfigRequest(
    val daysToNotRepeatProducts: Long,
    val excludeSectors: List<ProductSectorsEnum>,
    val excludeGroups: List<ProductGroupsEnum>,
    val dailyProductsLimit: Int,
    val formDurationDays: Long,
)
