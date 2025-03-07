package agoraa.app.forms_back.service.resources

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.dto.resource.ResourceDto
import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.exception.NotAllowedException
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.model.resources.ResourceModel
import agoraa.app.forms_back.repository.resources.ResourceRepository
import agoraa.app.forms_back.schema.resources.ResourceCreateSchema
import agoraa.app.forms_back.schema.resources.ResourceEditSchema
import agoraa.app.forms_back.service.UserService
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
        stores: List<StoresEnum>? = null,
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
                predicates.add(root.get<StoresEnum>("store").`in`(it))
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

    fun createDto(resource: ResourceModel, full: Boolean = false): ResourceDto {
        val userDto = userService.createDto(resource.user)
        val resourceDto = ResourceDto(
            id = resource.id,
            user = userDto,
            store = resource.store,
            createdAt = resource.createdAt,
            processed = resource.processed,
            orderNumber = resource.orderNumber
        )

        return when {
            full -> {
                val resourceProducts = resourceProductService.findByParentId(resource.id)
                resourceDto.products = resourceProducts
                resourceDto
            }

            else -> resourceDto
        }
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): ResourceModel {
        val resource = resourceRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Resource with id $id not found") }

        return when {
            hasPermission(customUserDetails, resource) -> resource
            else -> throw NotAllowedException("You don't have permission to access this resource")
        }
    }

    fun getById(
        customUserDetails: CustomUserDetails,
        id: Long,
        full: Boolean
    ): ResourceDto {
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
        stores: List<StoresEnum>?,
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
    fun create(customUserDetails: CustomUserDetails, request: ResourceCreateSchema) {
        val currentUser = customUserDetails.getUserModel()
        val resource = resourceRepository.saveAndFlush(
            ResourceModel(
                user = currentUser,
                store = request.store
            )
        )

        resourceProductService.create(resource, request.products)

    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: ResourceEditSchema) {
        val resource = findById(customUserDetails, id)

        val resourceEdited = resourceRepository.saveAndFlush(
            resource.copy(
                store = request.store ?: resource.store,
                processed = request.processed ?: resource.processed,
                orderNumber = request.orderNumber ?: resource.orderNumber
            )
        )

        request.products?.let { resourceProductService.edit(resourceEdited, it) }
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val resource = findById(customUserDetails, id)
        resourceRepository.delete(resource)
    }
}