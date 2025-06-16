package agoraa.app.forms_back.product_mix.repository

import agoraa.app.forms_back.product_mix.model.ProductMixModel
import org.springframework.data.jpa.repository.JpaRepository

interface ProductMixRepository : JpaRepository<ProductMixModel, Long>