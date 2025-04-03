package agoraa.app.forms_back.passive_quotations.passive_quotations.dto.request

import agoraa.app.forms_back.shared.enums.WppGroupsEnum

data class PassiveQuotationPrintRequest(
    val wppGroup: agoraa.app.forms_back.shared.enums.WppGroupsEnum,
    val fileName: String
)
