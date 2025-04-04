package agoraa.app.forms_back.extra_orders.extra_orders.repository

import agoraa.app.forms_back.extra_orders.extra_orders.model.ExtraOrderModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ExtraOrderRepository : JpaRepository<ExtraOrderModel, Long>, JpaSpecificationExecutor<ExtraOrderModel>