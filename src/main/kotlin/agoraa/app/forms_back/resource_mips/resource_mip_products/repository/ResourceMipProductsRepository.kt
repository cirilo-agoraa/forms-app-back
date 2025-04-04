package agoraa.app.forms_back.resource_mips.resource_mip_products.repository

import agoraa.app.forms_back.resource_mips.resource_mip_products.model.ResourceMipProductsModel
import org.springframework.data.jpa.repository.JpaRepository

interface ResourceMipProductsRepository: JpaRepository<ResourceMipProductsModel, Long> {
    fun findByResourceMipId(resourceMipId: Long): List<ResourceMipProductsModel>
}