package agoraa.app.forms_back.service.resource_mips

import agoraa.app.forms_back.dto.resource_mips.ResourceMipProductsDto
import agoraa.app.forms_back.model.resource_mip.ResourceMipModel
import agoraa.app.forms_back.model.resource_mip.ResourceMipProductsModel
import agoraa.app.forms_back.repository.resource_mips.ResourceMipProductsRepository
import agoraa.app.forms_back.schema.resource_mips.ResourceMipProductsCreateSchema
import agoraa.app.forms_back.schema.resource_mips.ResourceMipProductsEditSchema
import agoraa.app.forms_back.service.ProductService
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class ResourceMipProductsService(
    private val resourceMipProductsRepository: ResourceMipProductsRepository,
    private val productService: ProductService
) {
    private fun editMultiple(
        resourceMipProducts: List<ResourceMipProductsModel>,
        products: List<ResourceMipProductsEditSchema>
    ) {
        val editedResourceMipProducts = resourceMipProducts.map { resourceMipProduct ->
            val updatedResourceMipProduct = products.find { it.productId == resourceMipProduct.product.id }
            resourceMipProduct.copy(
                quantity = updatedResourceMipProduct?.quantity ?: resourceMipProduct.quantity,
            )
        }
        resourceMipProductsRepository.saveAllAndFlush(editedResourceMipProducts)
    }

    private fun createCriteria(
        resourceMipId: Long? = null,
    ): Specification<ResourceMipProductsModel> {
        return Specification { root: Root<ResourceMipProductsModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            resourceMipId?.let {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get<ResourceMipModel>("resourceMip").get<Long>("id"), it
                    )
                )
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun createDto(resourceMipProducts: ResourceMipProductsModel): ResourceMipProductsDto {
        val productDto = productService.createDto(resourceMipProducts.product)

        return ResourceMipProductsDto(
            id = resourceMipProducts.id,
            product = productDto,
            quantity = resourceMipProducts.quantity,
        )
    }

    fun findByParentId(
        resourceMipId: Long,
    ): List<ResourceMipProductsDto> {
        val spec = createCriteria(resourceMipId)

        return resourceMipProductsRepository.findAll(spec).map { createDto(it) }
    }

    fun create(resourceMip: ResourceMipModel, products: List<ResourceMipProductsCreateSchema>) {
        val resourceMipProducts = products.map { i ->
            val product = productService.findById(i.productId)

            ResourceMipProductsModel(
                resourceMip = resourceMip,
                product = product,
                quantity = i.quantity,
            )
        }
        resourceMipProductsRepository.saveAll(resourceMipProducts)
    }

    fun edit(resourceMip: ResourceMipModel, products: List<ResourceMipProductsEditSchema>) {
        val spec = createCriteria(resourceMip.id)
        val resourceMipProducts = resourceMipProductsRepository.findAll(spec)
        val currentResourceMipProducts = resourceMipProducts.map { it.product.id }.toSet()
        val editResourceMipProductsSet = products.map { it.productId }.toSet()

        val toAdd = products.filter { it.productId !in currentResourceMipProducts }
        val newResourceMipProducts = toAdd.map { i ->
            val product = productService.findById(i.productId)
            ResourceMipProductsModel(
                resourceMip = resourceMip,
                product = product,
                quantity = i.quantity ?: throw IllegalArgumentException("Quantity is required"),
            )
        }
        resourceMipProductsRepository.saveAll(newResourceMipProducts)

        val toDelete = resourceMipProducts.filter { it.product.id !in editResourceMipProductsSet }
        resourceMipProductsRepository.deleteAll(toDelete)

        val toEdit = resourceMipProducts.filter { it.product.id in editResourceMipProductsSet }
        editMultiple(toEdit, products)
    }
}