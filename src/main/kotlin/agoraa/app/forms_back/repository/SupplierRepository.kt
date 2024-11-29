package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.SupplierModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SupplierRepository: JpaRepository<SupplierModel, Long> {
    fun findByName(name: String): Optional<SupplierModel>

    fun findByNameContaining(name: String, pageable: Pageable): Page<SupplierModel>

    fun findByStatus(status: String, pageable: Pageable): Page<SupplierModel>
}