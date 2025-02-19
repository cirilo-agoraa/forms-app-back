package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.resources.ResourceProductsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ResourceProductsRepository : JpaRepository<ResourceProductsModel, Long>,
    JpaSpecificationExecutor<ResourceProductsModel>