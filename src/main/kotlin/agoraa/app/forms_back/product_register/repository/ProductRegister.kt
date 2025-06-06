package agoraa.app.forms_back.products.transfer.repository

import agoraa.app.forms_back.products.transfer.model.ProductRegisterModel
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRegisterRepository : JpaRepository<ProductRegisterModel, Long>