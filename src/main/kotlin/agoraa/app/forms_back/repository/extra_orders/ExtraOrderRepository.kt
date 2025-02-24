package agoraa.app.forms_back.repository.extra_orders

import agoraa.app.forms_back.model.extra_orders.ExtraOrderModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ExtraOrderRepository : JpaRepository<ExtraOrderModel, Long>, JpaSpecificationExecutor<ExtraOrderModel>