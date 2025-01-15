package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.ResourceModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ResourceRepository: JpaRepository<ResourceModel, Long>, JpaSpecificationExecutor<ResourceModel> {
}