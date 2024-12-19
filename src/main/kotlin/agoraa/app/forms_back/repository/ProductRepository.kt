package agoraa.app.forms_back.repository

import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.model.ProductModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductRepository : JpaRepository<ProductModel, Long>, JpaSpecificationExecutor<ProductModel> {

    @EntityGraph(value = "ProductModel.supplier", type = EntityGraph.EntityGraphType.LOAD)
    override fun findById(id: Long): Optional<ProductModel>

    @EntityGraph(value = "ProductModel.supplier", type = EntityGraph.EntityGraphType.LOAD)
    override fun findAll(): MutableList<ProductModel>

    @EntityGraph(value = "ProductModel.supplier", type = EntityGraph.EntityGraphType.LOAD)
    override fun findAll(pageable: Pageable): Page<ProductModel>

    @EntityGraph(value = "ProductModel.supplier", type = EntityGraph.EntityGraphType.LOAD)
    override fun findAll(spec: Specification<ProductModel>?): MutableList<ProductModel>

    @EntityGraph(value = "ProductModel.supplier", type = EntityGraph.EntityGraphType.LOAD)
    override fun findAll(spec: Specification<ProductModel>?, pageable: Pageable): Page<ProductModel>

    @EntityGraph(value = "ProductModel.supplier", type = EntityGraph.EntityGraphType.LOAD)
    fun findByCodeAndStore(code: String, store: StoresEnum): Optional<ProductModel>

}