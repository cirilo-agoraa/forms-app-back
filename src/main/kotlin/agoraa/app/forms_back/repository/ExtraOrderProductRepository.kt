package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.extra_orders.ExtraOrderProductModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExtraOrderProductRepository : JpaRepository<ExtraOrderProductModel, Long>