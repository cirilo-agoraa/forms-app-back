package agoraa.app.forms_back.resources.resources.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.resources.resource_products.service.ResourceProductsService
import agoraa.app.forms_back.resources.resources.dto.request.ResourcePatchRequest
import agoraa.app.forms_back.resources.resources.dto.request.ResourceRequest
import agoraa.app.forms_back.resources.resources.dto.response.ResourceResponse
import agoraa.app.forms_back.resources.resources.model.ResourceModel
import agoraa.app.forms_back.resources.resources.repository.ResourceRepository
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.users.users.model.UserModel
import agoraa.app.forms_back.users.users.service.UserService
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ResourceService(
    private val resourceRepository: ResourceRepository,
    private val resourceProductService: ResourceProductsService,
    private val userService: UserService
) {

    private fun createCriteria(
        username: String? = null,
        stores: List<agoraa.app.forms_back.shared.enums.StoresEnum>? = null,
        createdAt: LocalDateTime? = null,
        processed: Boolean? = null,
        userId: Long? = null,
        minDate: LocalDateTime? = null,
        maxDate: LocalDateTime? = null,
    ): Specification<ResourceModel> {
        return Specification { root: Root<ResourceModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            userId?.let {
                predicates.add(criteriaBuilder.equal(root.get<UserModel>("user").get<Long>("id"), it))
            }

            username?.let {
                predicates.add(criteriaBuilder.like(root.get<UserModel>("user").get("username"), "%$it%"))
            }

            stores?.let {
                predicates.add(root.get<agoraa.app.forms_back.shared.enums.StoresEnum>("store").`in`(it))
            }

            createdAt?.let {
                predicates.add(criteriaBuilder.equal(root.get<LocalDateTime>("createdAt"), it))
            }

            minDate?.let {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), it))
            }

            maxDate?.let {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), it))
            }

            processed?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("processed"), it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun hasPermission(customUserDetails: CustomUserDetails, resource: ResourceModel): Boolean {
        val currentUser = customUserDetails.getUserModel()
        val isAdmin = customUserDetails.authorities.any { it.authority == "ROLE_ADMIN" }
        val isOwner = resource.user.id == currentUser.id

        return isAdmin || isOwner
    }

    fun createDto(resource: ResourceModel, full: Boolean = false): ResourceResponse {
        val userDto = userService.createDto(resource.user)
        val resourceDto = ResourceResponse(
            id = resource.id,
            user = userDto,
            store = resource.store,
            createdAt = resource.createdAt,
            processed = resource.processed,
            orderNumber = resource.orderNumber
        )

        if (full) {
            val resourceProducts = resourceProductService.findByParentId(resource.id)
            resourceDto.products = resourceProducts
        }

        return resourceDto
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): ResourceModel {
        val resource = resourceRepository.findById(id)
            .orElseThrow { agoraa.app.forms_back.shared.exception.ResourceNotFoundException("Resource with id $id not found") }

        return when {
            hasPermission(customUserDetails, resource) -> resource
            else -> throw agoraa.app.forms_back.shared.exception.NotAllowedException("You don't have permission to access this resource")
        }
    }

    fun getById(
        customUserDetails: CustomUserDetails,
        id: Long,
        full: Boolean
    ): ResourceResponse {
        val resource = findById(customUserDetails, id)

        return createDto(resource, full)
    }

    fun getAll(
        full: Boolean,
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        username: String?,
        stores: List<agoraa.app.forms_back.shared.enums.StoresEnum>?,
        createdAt: LocalDateTime?,
        maxDate: LocalDateTime?,
        minDate: LocalDateTime?,
        processed: Boolean?,
    ): Any {
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec = createCriteria(username, stores, createdAt, processed, minDate = minDate, maxDate = maxDate)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = resourceRepository.findAll(spec, pageable)

                return pageResult
            }

            else -> {
                val resources = resourceRepository.findAll(spec, sortBy)

                if (full) {
                    resources.map { createDto(it, full) }
                } else {
                    resources
                }
            }
        }
    }

    fun getAllByCurrentUser(
        customUserDetails: CustomUserDetails,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        stores: List<StoresEnum>?,
        createdAt: LocalDateTime?,
        processed: Boolean?
    ): Any {
        val currentUser = customUserDetails.getUserModel()
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val pageable = PageRequest.of(page, size, sortBy)
        val spec =
            createCriteria(stores = stores, createdAt = createdAt, processed = processed, userId = currentUser.id)

        return resourceRepository.findAll(spec, pageable)
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: ResourceRequest) {
        val currentUser = customUserDetails.getUserModel()
        val resource = resourceRepository.saveAndFlush(
            ResourceModel(
                user = currentUser,
                store = request.store
            )
        )

        resourceProductService.editOrCreateOrDelete(resource, request.products)

    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: ResourceRequest) {
        val resource = findById(customUserDetails, id)

        val resourceEdited = resourceRepository.saveAndFlush(
            resource.copy(
                store = request.store,
                processed = request.processed,
                orderNumber = request.orderNumber
            )
        )

        resourceProductService.editOrCreateOrDelete(resourceEdited, request.products)
    }

    fun patch(customUserDetails: CustomUserDetails, id: Long, request: ResourcePatchRequest) {
        val resource = findById(customUserDetails, id)

        resourceRepository.saveAndFlush(
            resource.copy(
                processed = request.processed ?: resource.processed,
            )
        )

        request.products?.let { resourceProductService.patchProducts(resource, it) }
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val resource = findById(customUserDetails, id)
        resourceRepository.delete(resource)
    }
}