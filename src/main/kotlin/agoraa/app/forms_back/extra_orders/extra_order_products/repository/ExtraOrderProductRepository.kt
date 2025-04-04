package agoraa.app.forms_back.extra_orders.extra_order_products.repository

import agoraa.app.forms_back.extra_orders.extra_order_products.model.ExtraOrderProductsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExtraOrderProductRepository : JpaRepository<ExtraOrderProductsModel, Long> {
    fun findByExtraOrderId(extraOrderId: Long): List<ExtraOrderProductsModel>
}