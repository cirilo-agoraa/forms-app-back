package agoraa.app.forms_back.resource_mips.resource_mips.repository

import agoraa.app.forms_back.resource_mips.resource_mips.model.ResourceMipModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ResourceMipRepository: JpaRepository<ResourceMipModel, Long>, JpaSpecificationExecutor<ResourceMipModel> {
}