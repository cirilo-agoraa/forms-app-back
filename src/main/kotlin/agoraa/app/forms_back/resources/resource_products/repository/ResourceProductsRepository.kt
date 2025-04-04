package agoraa.app.forms_back.resources.resource_products.repository

import agoraa.app.forms_back.resources.resource_products.model.ResourceProductsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ResourceProductsRepository : JpaRepository<ResourceProductsModel, Long> {
    fun findByResourceId(resourceId: Long): List<ResourceProductsModel>
}