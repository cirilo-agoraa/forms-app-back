package agoraa.app.forms_back.service.resource_mips

import agoraa.app.forms_back.dto.resource_mips.ResourceMipItemsDto
import agoraa.app.forms_back.model.resource_mip.ResourceMipItemsModel
import agoraa.app.forms_back.model.resource_mip.ResourceMipModel
import agoraa.app.forms_back.repository.resource_mips.ResourceMipItemsRepository
import agoraa.app.forms_back.schema.resource_mips.ResourceMipItemsCreateSchema
import agoraa.app.forms_back.schema.resource_mips.ResourceMipItemsEditSchema
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class ResourceMipItemsService(private val resourceMipItemsRepository: ResourceMipItemsRepository) {
    private fun editMultiple(
        resourceMipItems: List<ResourceMipItemsModel>,
        items: List<ResourceMipItemsEditSchema>
    ) {
        val editedResourceMipItems = resourceMipItems.map { resourceMipItem ->
            val updatedResourceMipItem = items.find { it.category == resourceMipItem.category }
            resourceMipItem.copy(
                quantity = updatedResourceMipItem?.quantity ?: resourceMipItem.quantity,
            )
        }
        resourceMipItemsRepository.saveAllAndFlush(editedResourceMipItems)
    }

    private fun createCriteria(
        resourceMipId: Long? = null,
    ): Specification<ResourceMipItemsModel> {
        return Specification { root: Root<ResourceMipItemsModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
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

    fun createDto(resourceMipItems: ResourceMipItemsModel): ResourceMipItemsDto {
        return ResourceMipItemsDto(
            id = resourceMipItems.id,
            category = resourceMipItems.category,
            quantity = resourceMipItems.quantity,
        )
    }

    fun findByParentId(
        resourceMipId: Long,
    ): List<ResourceMipItemsDto> {
        val spec = createCriteria(resourceMipId)

        return resourceMipItemsRepository.findAll(spec).map { createDto(it) }
    }

    fun create(resourceMip: ResourceMipModel, items: List<ResourceMipItemsCreateSchema>) {
        val resourceMipItems = items.map { i ->
            ResourceMipItemsModel(
                resourceMip = resourceMip,
                category = i.category,
                quantity = i.quantity,
            )
        }
        resourceMipItemsRepository.saveAll(resourceMipItems)
    }

    fun edit(resourceMip: ResourceMipModel, items: List<ResourceMipItemsEditSchema>) {
        val spec = createCriteria(resourceMip.id)
        val resourceMipItems = resourceMipItemsRepository.findAll(spec)
        val editResourceMipItemsSet = items.map { it.category }.toSet()

        val toAdd = items.filter { it.category !in editResourceMipItemsSet }
        val newResourceMipItems = toAdd.map { i ->
            ResourceMipItemsModel(
                resourceMip = resourceMip,
                category = i.category ?: throw IllegalArgumentException("Category is required"),
                quantity = i.quantity ?: throw IllegalArgumentException("Quantity is required"),
            )
        }
        resourceMipItemsRepository.saveAll(newResourceMipItems)

        val toDelete = resourceMipItems.filter { it.category !in editResourceMipItemsSet }
        resourceMipItemsRepository.deleteAll(toDelete)

        val toEdit = resourceMipItems.filter { it.category in editResourceMipItemsSet }
        editMultiple(toEdit, items)
    }
}