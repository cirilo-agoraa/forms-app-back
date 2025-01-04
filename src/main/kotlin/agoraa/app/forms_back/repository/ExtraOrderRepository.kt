package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.ExtraOrderModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ExtraOrderRepository : JpaRepository<ExtraOrderModel, Long>, JpaSpecificationExecutor<ExtraOrderModel> {
}