package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.ProductModel
import agoraa.app.forms_back.model.SupplierModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductRepository : JpaRepository<ProductModel, Long>, PagingAndSortingRepository<ProductModel, Long> {
    fun findByCode(code: String): Optional<ProductModel>

    fun findByNameContainingAndOutOfMix(name: String, outOfMix: Boolean, pageable: Pageable): Page<ProductModel>
    fun findBySupplierNameContainingAndOutOfMix(
        supplierName: String,
        outOfMix: Boolean,
        pageable: Pageable
    ): Page<ProductModel>
    fun findByCodeContainingAndOutOfMix(code: String, outOfMix: Boolean, pageable: Pageable): Page<ProductModel>

    fun findByNameContaining(name: String, pageable: Pageable): Page<ProductModel>
    fun findBySupplierNameContaining(supplierName: String, pageable: Pageable): Page<ProductModel>
    fun findByCodeContaining(code: String, pageable: Pageable): Page<ProductModel>
    fun findByOutOfMix(outOfMix: Boolean, pageable: Pageable): Page<ProductModel>
    fun findBySupplierId(supplierId: Long, pageable: Pageable): Page<ProductModel>
}