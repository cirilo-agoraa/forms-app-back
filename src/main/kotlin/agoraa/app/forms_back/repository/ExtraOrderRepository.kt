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

    @EntityGraph(value = "graph.ExtraOrderModel.all", type = EntityGraph.EntityGraphType.LOAD)
    override fun <S : ExtraOrderModel?> save(entity: S & Any): S & Any

    @EntityGraph(value = "graph.ExtraOrderModel.all", type = EntityGraph.EntityGraphType.LOAD)
    override fun findById(id: Long): Optional<ExtraOrderModel>

    @EntityGraph(value = "graph.ExtraOrderModel.all", type = EntityGraph.EntityGraphType.LOAD)
    override fun findAll(pageable: Pageable): Page<ExtraOrderModel>

    @EntityGraph(value = "graph.ExtraOrderModel.all", type = EntityGraph.EntityGraphType.LOAD)
    override fun findAll(): MutableList<ExtraOrderModel>

    @EntityGraph(value = "graph.ExtraOrderModel.all", type = EntityGraph.EntityGraphType.LOAD)
    override fun findAll(spec: Specification<ExtraOrderModel>?): MutableList<ExtraOrderModel>

    @EntityGraph(value = "graph.ExtraOrderModel.all", type = EntityGraph.EntityGraphType.LOAD)
    override fun findAll(spec: Specification<ExtraOrderModel>?, pageable: Pageable): Page<ExtraOrderModel>
}