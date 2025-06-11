package agoraa.app.forms_back.products.products.repository

import agoraa.app.forms_back.products.products.model.ProductModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductRepository : JpaRepository<ProductModel, Long>, JpaSpecificationExecutor<ProductModel> {
    fun findByCodeAndStore(code: String, store: agoraa.app.forms_back.shared.enums.StoresEnum): Optional<ProductModel>
    fun findByCode(code: String): Optional<ProductModel>
    fun findByNameAndStore(name: String, store: agoraa.app.forms_back.shared.enums.StoresEnum): Optional<ProductModel>
    fun findByName(name: String): Optional<ProductModel>
    
}