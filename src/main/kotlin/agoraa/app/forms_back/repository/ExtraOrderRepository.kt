package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.ExtraOrderModel
import agoraa.app.forms_back.model.SupplierModel
import agoraa.app.forms_back.model.UserModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ExtraOrderRepository : JpaRepository<ExtraOrderModel, Long> {

    fun findBySupplierId(supplierId: Long, pageable: Pageable): Page<ExtraOrderModel>
    fun findByUserId(userId: Long, pageable: Pageable): Page<ExtraOrderModel>

    fun findByProcessed(processed: Boolean, pageable: Pageable): Page<ExtraOrderModel>
    fun findByProcessedAndUserId(processed: Boolean, userId: Long, pageable: Pageable): Page<ExtraOrderModel>

    fun findByDateSubmitted(dateSubmitted: LocalDate, pageable: Pageable): Page<ExtraOrderModel>
    fun findByDateSubmittedAndUserId(dateSubmitted: LocalDate, userId: Long, pageable: Pageable): Page<ExtraOrderModel>

    fun findByOrigin(origin: String, pageable: Pageable): Page<ExtraOrderModel>
    fun findByOriginAndUserId(origin: String, userId: Long, pageable: Pageable): Page<ExtraOrderModel>

    fun findByPartialComplete(partialComplete: String, pageable: Pageable): Page<ExtraOrderModel>
    fun findByPartialCompleteAndUserId(partialComplete: String, userId: Long,  pageable: Pageable): Page<ExtraOrderModel>
}