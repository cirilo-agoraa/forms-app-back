package agoraa.app.forms_back.product_resume.service

import agoraa.app.forms_back.products_resume.model.ProductsResumeModel
import agoraa.app.forms_back.products_resume.dto.ProductsResumeDto
import agoraa.app.forms_back.products_resume.repository.ProductsResumeRepository
import org.springframework.stereotype.Service

@Service
class ProductResumeService(
    private val productsResumeRepository: ProductsResumeRepository
) {
    fun getAll(): List<ProductsResumeDto> =
        productsResumeRepository.findAll().map { it.toDto() }

    fun getById(id: Long): ProductsResumeDto? =
        productsResumeRepository.findById(id).orElse(null)?.toDto()

    fun save(request: ProductsResumeDto): ProductsResumeDto {
        val entity = ProductsResumeModel(
            id = request.id,
            code = request.code,
            name = request.name,
            barcode = request.barcode,
            store = request.store,
            outOfMixSmj = request.outOfMixSmj,
            outOfMixStt = request.outOfMixStt,
            sector = request.sector,
            groupName = request.groupName,
            subgroup = request.subgroup,
            brand = request.brand,
            supplierName = request.supplierName,
            supplierId = request.supplierId
        )
        return productsResumeRepository.save(entity).toDto()
    }

    fun patch(id: Long, dto: ProductsResumeDto): ProductsResumeDto? {
        val entity = productsResumeRepository.findById(id).orElse(null) ?: return null
        val updated = entity.copy(
            code = dto.code ?: entity.code,
            name = dto.name ?: entity.name,
            barcode = dto.barcode ?: entity.barcode,
            store = dto.store ?: entity.store,
            outOfMixSmj = dto.outOfMixSmj ?: entity.outOfMixSmj,
            outOfMixStt = dto.outOfMixStt ?: entity.outOfMixStt,
            sector = dto.sector ?: entity.sector,
            groupName = dto.groupName ?: entity.groupName,
            subgroup = dto.subgroup ?: entity.subgroup,
            brand = dto.brand ?: entity.brand,
            supplierName = dto.supplierName ?: entity.supplierName,
            supplierId = dto.supplierId ?: entity.supplierId
        )
        return productsResumeRepository.save(updated).toDto()
    }

    fun getByCode(code: String): ProductsResumeDto? {
        val entity = productsResumeRepository.findByCode(code)
        return entity?.toDto()
    }   
}

// Extension function para converter model em DTO
fun ProductsResumeModel.toDto() = ProductsResumeDto(
    id = id,
    code = code,
    name = name,
    barcode = barcode,
    store = store,
    outOfMixSmj = outOfMixSmj,
    outOfMixStt = outOfMixStt,
    sector = sector,
    groupName = groupName,
    subgroup = subgroup,
    brand = brand,
    supplierName = supplierName,
    supplierId = supplierId
)