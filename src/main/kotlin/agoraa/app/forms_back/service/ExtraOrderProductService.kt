package agoraa.app.forms_back.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.ExtraOrderModel
import agoraa.app.forms_back.model.ExtraOrderProductModel
import agoraa.app.forms_back.repository.ExtraOrderProductRepository
import agoraa.app.forms_back.schema.extra_order_product.ExtraOrderProductCreateSchema
import agoraa.app.forms_back.schema.extra_order_product.ExtraOrderProductEditSchema
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy

@Service
class ExtraOrderProductService(
    private val productService: ProductService,
    private val extraOrderProductRepository: ExtraOrderProductRepository,
    @Lazy private val extraOrderService: ExtraOrderService
) {

    fun findAll(
        customUserDetails: CustomUserDetails,
        extraOrderId: Long,
        page: Int,
        size: Int,
        sort: String,
        direction: String
    ): Page<ExtraOrderProductModel> {
        val sortDirection = if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))

        return when {
            extraOrderId != 0L -> {
                val extraOrder = extraOrderService.findById(customUserDetails, extraOrderId)
                extraOrderProductRepository.findByExtraOrderId(extraOrder.id, pageable)
            }

            else -> {
                extraOrderProductRepository.findAll(pageable)
            }
        }
    }

    fun findById(id: Long): ExtraOrderProductModel {
        return extraOrderProductRepository.findById(id)
            .orElseThrow { throw ResourceNotFoundException("Extra Order Product not found") }
    }

    fun create(
        extraOrder: ExtraOrderModel,
        productsInfo: List<ExtraOrderProductCreateSchema>
    ): List<ExtraOrderProductModel> {
        val extraOrderProducts = productsInfo.map { p ->
            val product = productService.findByCode(p.code)
            ExtraOrderProductModel(
                extraOrder = extraOrder,
                code = product.code,
                price = p.price,
                quantity = p.quantity
            )
        }
        return extraOrderProductRepository.saveAll(extraOrderProducts)
    }

    fun delete(id: Long): Any {
        return if (extraOrderProductRepository.existsById(id)) {
            extraOrderProductRepository.deleteById(id)
        } else {
            throw ResourceNotFoundException("Extra Order Product not found")
        }
    }

    fun edit(id: Long, request: ExtraOrderProductEditSchema): ExtraOrderProductModel {
        val extraOrderProduct = findById(id)
        val editedExtraOrderProduct = extraOrderProduct.copy(
            code = request.code?.let { productService.findByCode(it).code } ?: extraOrderProduct.code,
            price = request.price ?: extraOrderProduct.price,
            quantity = request.quantity ?: extraOrderProduct.quantity
        )
        return extraOrderProductRepository.save(editedExtraOrderProduct)
    }
}