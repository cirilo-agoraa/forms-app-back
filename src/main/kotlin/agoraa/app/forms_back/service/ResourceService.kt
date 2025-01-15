package agoraa.app.forms_back.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.exception.NotAllowedException
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.ResourceModel
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.repository.ResourceRepository
import agoraa.app.forms_back.schema.resource.ResourceCreateSchema
import agoraa.app.forms_back.schema.resource.ResourceEditSchema
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
    private val resourceProductService: ResourceProductsService
) {

    private fun createCriteria(
        username: String? = null,
        store: StoresEnum? = null,
        createdAt: LocalDateTime? = null,
        processed: Boolean? = null,
        userId: Long? = null,
    ): Specification<ResourceModel> {
        return Specification { root: Root<ResourceModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            userId.let {
                predicates.add(criteriaBuilder.equal(root.get<UserModel>("user").get<Long>("id"), it))
            }

            username?.let {
                predicates.add(criteriaBuilder.equal(root.get<UserModel>("user").get<String>("username"), it))
            }

            store?.let {
                predicates.add(criteriaBuilder.equal(root.get<StoresEnum>("store"), it))
            }

            createdAt?.let {
                predicates.add(criteriaBuilder.equal(root.get<LocalDateTime>("createdAt"), it))
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
        id: Long
    ): ResourceModel {
        return findById(customUserDetails, id)
    }

    fun getAll(
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        username: String?,
        store: StoresEnum?,
        createdAt: LocalDateTime?,
        processed: Boolean?
    ): Any {
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val pageable = PageRequest.of(page, size, sortBy)
        val spec = createCriteria(username, store, createdAt, processed)

        return resourceRepository.findAll(spec, pageable)
    }

    fun getAllByCurrentUser(
        customUserDetails: CustomUserDetails,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        store: StoresEnum?,
        createdAt: LocalDateTime?,
        processed: Boolean?
    ): Any {
        val currentUser = customUserDetails.getUserModel()
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val pageable = PageRequest.of(page, size, sortBy)
        val spec = createCriteria(store = store, createdAt = createdAt, processed = processed, userId = currentUser.id)

        return resourceRepository.findAll(spec, pageable)
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: ResourceCreateSchema): ResourceModel {
        val currentUser = customUserDetails.getUserModel()
        val resource = resourceRepository.saveAndFlush(
            ResourceModel(
                user = currentUser,
                store = request.store
            )
        )

        resourceProductService.create(resource, request.products)

        return resource
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: ResourceEditSchema): ResourceModel {
        val resource = findById(customUserDetails, id)

        val resourceEdited = resourceRepository.saveAndFlush(
            resource.copy(
                store = request.store ?: resource.store,
                processed = request.processed ?: resource.processed
            )
        )

        request.products?.let { resourceProductService.edit(resourceEdited, it) }

        return resourceEdited
    }
}