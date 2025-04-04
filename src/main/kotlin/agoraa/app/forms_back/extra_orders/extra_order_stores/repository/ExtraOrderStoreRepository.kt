package agoraa.app.forms_back.extra_orders.extra_order_stores.repository

import agoraa.app.forms_back.extra_orders.extra_order_stores.model.ExtraOrderStoresModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ExtraOrderStoreRepository : JpaRepository<ExtraOrderStoresModel, Long> {
    fun findByExtraOrderId(extraOrderId: Long): List<ExtraOrderStoresModel>
}