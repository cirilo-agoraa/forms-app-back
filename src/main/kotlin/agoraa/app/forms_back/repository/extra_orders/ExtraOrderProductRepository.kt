package agoraa.app.forms_back.repository.extra_orders

import agoraa.app.forms_back.model.extra_orders.ExtraOrderProductsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ExtraOrderProductRepository : JpaRepository<ExtraOrderProductsModel, Long>, JpaSpecificationExecutor<ExtraOrderProductsModel> {
}