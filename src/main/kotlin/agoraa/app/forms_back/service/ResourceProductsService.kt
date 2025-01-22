package agoraa.app.forms_back.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.dto.resource_products.ResourceProductsDto
import agoraa.app.forms_back.model.ProductModel
import agoraa.app.forms_back.model.ResourceModel
import agoraa.app.forms_back.model.ResourceProductsModel
import agoraa.app.forms_back.repository.ResourceProductsRepository
import agoraa.app.forms_back.schema.resource_products.ResourceProductsCreateSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy
import org.springframework.data.jpa.domain.Specification

@Service
class ResourceProductsService(
    private val resourceProductsRepository: ResourceProductsRepository,
    private val productService: ProductService,
    @Lazy val resourceService: ResourceService
) {

    private fun editMultiple(resourceProducts: List<ResourceProductsModel>, products: List<ResourceProductsCreateSchema>) {
        val editedResourceProducts = resourceProducts.map { rp ->
            val updatedProduct = products.find { it.productId == rp.product.id }
            rp.copy(
                quantity = updatedProduct?.quantity ?: rp.quantity
            )
        }
        resourceProductsRepository.saveAllAndFlush(editedResourceProducts)
    }

    private fun createCriteria(
        resourceId: Long? = null,
        name: String? = null,
        code: String? = null,
        sector: String? = null
    ): Specification<ResourceProductsModel> {
        return Specification { root: Root<ResourceProductsModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            resourceId?.let {
                predicates.add(criteriaBuilder.equal(root.get<ResourceModel>("resource").get<Long>("id"), it))
            }

            name?.let {
                predicates.add(criteriaBuilder.like(root.get<ProductModel>("product").get("name"), "%$it%"))
            }

            code?.let {
                predicates.add(criteriaBuilder.like(root.get<ProductModel>("product").get("code"), "%$it%"))
            }

            sector?.let {
                predicates.add(criteriaBuilder.like(root.get<ProductModel>("product").get("sector"), "%$it%"))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun createDto(resourceProduct: ResourceProductsModel): ResourceProductsDto {
        val productDto = productService.createDto(resourceProduct.product)

        return ResourceProductsDto(
            id = resourceProduct.id,
            product = productDto,
            quantity = resourceProduct.quantity
        )
    }

    fun findByResourceId(
        resourceId: Long,
        name: String? = null,
        code: String? = null,
        sector: String? = null
    ): List<ResourceProductsDto> {
        val spec = createCriteria(resourceId, name, code, sector)

        return resourceProductsRepository.findAll(spec).map { createDto(it) }
    }

    fun create(resource: ResourceModel, products: List<ResourceProductsCreateSchema>) {
        val resourceProducts = products.map { p ->
            val product = productService.findById(p.productId)
            ResourceProductsModel(
                resource = resource,
                product = product,
                quantity = p.quantity
            )
        }
        resourceProductsRepository.saveAll(resourceProducts)
    }

    fun edit(resource: ResourceModel, products: List<ResourceProductsCreateSchema>) {
        val spec = createCriteria(resource.id)
        val resourceProducts = resourceProductsRepository.findAll(spec)
        val currentProductsSet = resourceProducts.map { it.product.id }.toSet()
        val newProductsSet = products.map { it.productId }.toSet()

        val toAdd = products.filter { it.productId !in currentProductsSet }
        create(resource, toAdd)

        val toDelete = resourceProducts.filter { it.product.id !in newProductsSet }
        resourceProductsRepository.deleteAll(toDelete)

        val toEdit = resourceProducts.filter { it.product.id in newProductsSet }
        editMultiple(toEdit, products)
    }
}