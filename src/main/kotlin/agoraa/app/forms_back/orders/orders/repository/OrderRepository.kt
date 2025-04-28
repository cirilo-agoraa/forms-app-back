package agoraa.app.forms_back.orders.orders.repository

import agoraa.app.forms_back.orders.orders.model.OrderModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: JpaRepository<OrderModel, Long>, JpaSpecificationExecutor<OrderModel> {
}