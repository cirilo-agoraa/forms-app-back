package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.ExtraOrderProductModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ExtraOrderProductRepository : JpaRepository<ExtraOrderProductModel, Long>,
    PagingAndSortingRepository<ExtraOrderProductModel, Long> {

        fun findByExtraOrderId(extraOrderId: Long, pageable: Pageable): Page<ExtraOrderProductModel>
}