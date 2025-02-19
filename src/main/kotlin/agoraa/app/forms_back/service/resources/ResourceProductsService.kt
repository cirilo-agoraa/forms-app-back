package agoraa.app.forms_back.service.resources

import agoraa.app.forms_back.dto.resource_products.ResourceProductsDto
import agoraa.app.forms_back.model.ProductModel
import agoraa.app.forms_back.model.resources.ResourceModel
import agoraa.app.forms_back.model.resources.ResourceProductsModel
import agoraa.app.forms_back.repository.ResourceProductsRepository
import agoraa.app.forms_back.schema.resource_products.ResourceProductsEditSchema
import agoraa.app.forms_back.service.ProductService
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class ResourceProductsService(
    private val resourceProductsRepository: ResourceProductsRepository,
    private val productService: ProductService,
) {

    private fun editMultiple(
        resourceProducts: List<ResourceProductsModel>,
        products: List<ResourceProductsEditSchema>
    ) {
        val editedResourceProducts = resourceProducts.map { rp ->
            val updatedProduct = products.find { it.productId == rp.product.id }
            rp.copy(
                quantity = updatedProduct?.quantity ?: rp.quantity,
                qttSent = updatedProduct?.qttSent ?: rp.qttSent,
                qttReceived = updatedProduct?.qttReceived ?: rp.qttReceived
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
            quantity = resourceProduct.quantity,
            qttSent = resourceProduct.qttSent,
            qttReceived = resourceProduct.qttReceived
        )
    }

    fun findByParentId(
        resourceId: Long,
    ): List<ResourceProductsDto> {
        val spec = createCriteria(resourceId)

        return resourceProductsRepository.findAll(spec).map { createDto(it) }
    }

    fun create(resource: ResourceModel, products: List<ResourceProductsEditSchema>) {
        val resourceProducts = products.map { p ->
            val product = productService.findById(p.productId)
            ResourceProductsModel(
                resource = resource,
                product = product,
                quantity = p.quantity ?: throw IllegalArgumentException("Quantity is required"),
            )
        }
        resourceProductsRepository.saveAll(resourceProducts)
    }

    fun edit(resource: ResourceModel, products: List<ResourceProductsEditSchema>) {
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