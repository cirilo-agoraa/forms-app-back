package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.ExtraOrderStoreModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExtraOrderStoreRepository: JpaRepository<ExtraOrderStoreModel, Long> {
    fun findByExtraOrderId(extraOrderId: Long): List<ExtraOrderStoreModel>
}