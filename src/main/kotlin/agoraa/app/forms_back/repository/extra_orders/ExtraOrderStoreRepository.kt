package agoraa.app.forms_back.repository.extra_orders

import agoraa.app.forms_back.model.extra_orders.ExtraOrderStoresModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ExtraOrderStoreRepository : JpaRepository<ExtraOrderStoresModel, Long>,
    JpaSpecificationExecutor<ExtraOrderStoresModel> {
}