package agoraa.app.forms_back.repository

import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.model.products.ProductModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductRepository : JpaRepository<ProductModel, Long>, JpaSpecificationExecutor<ProductModel> {
    fun findByCodeAndStore(code: String, store: StoresEnum): Optional<ProductModel>
}