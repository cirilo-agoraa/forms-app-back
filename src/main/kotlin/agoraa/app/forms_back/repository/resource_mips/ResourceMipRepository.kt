package agoraa.app.forms_back.repository.resource_mips

import agoraa.app.forms_back.model.resource_mip.ResourceMipModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ResourceMipRepository: JpaRepository<ResourceMipModel, Long>, JpaSpecificationExecutor<ResourceMipModel> {
}