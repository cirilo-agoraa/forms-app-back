package agoraa.app.forms_back.repository.extra_quotations

import agoraa.app.forms_back.model.extra_quotations.ExtraQuotationProductsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ExtraQuotationProductsRepository : JpaRepository<ExtraQuotationProductsModel, Long>,
    JpaSpecificationExecutor<ExtraQuotationProductsModel> {
}