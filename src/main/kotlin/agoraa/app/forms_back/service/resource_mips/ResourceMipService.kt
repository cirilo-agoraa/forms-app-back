package agoraa.app.forms_back.service.resource_mips

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.dto.resource_mips.ResourceMipDto
import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.exception.NotAllowedException
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.model.resource_mip.ResourceMipModel
import agoraa.app.forms_back.repository.resource_mips.ResourceMipRepository
import agoraa.app.forms_back.schema.resource_mips.ResourceMipCreateSchema
import agoraa.app.forms_back.schema.resource_mips.ResourceMipEditSchema
import agoraa.app.forms_back.service.UserService
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ResourceMipService(
    private val resourceMipRepository: ResourceMipRepository,
    private val resourceMipProductsService: ResourceMipProductsService,
    private val userService: UserService
) {
    private fun createCriteria(
        username: String? = null,
        createdAt: LocalDateTime? = null,
        processed: Boolean? = null,
        stores: List<StoresEnum>? = null,
        userId: Long? = null,
    ): Specification<ResourceMipModel> {
        return Specification { root: Root<ResourceMipModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
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

            processed?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("processed"), it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun hasPermission(
        customUserDetails: CustomUserDetails,
        resourceMip: ResourceMipModel
    ): Boolean {
        val currentUser = customUserDetails.getUserModel()
        val isAdmin = customUserDetails.authorities.any { it.authority == "ROLE_ADMIN" }
        val isOwner = resourceMip.user.id == currentUser.id

        return isAdmin || isOwner
    }

    fun createDto(resourceMip: ResourceMipModel, full: Boolean = false): ResourceMipDto {
        val userDto = userService.createDto(resourceMip.user)
        val resourceMipDto = ResourceMipDto(
            id = resourceMip.id,
            user = userDto,
            store = resourceMip.store,
            createdAt = resourceMip.createdAt,
            processed = resourceMip.processed,
        )

        return when {
            full -> {
                val resourceMipItems = resourceMipProductsService.findByParentId(resourceMip.id)

                resourceMipDto.items = resourceMipItems
                resourceMipDto
            }

            else -> resourceMipDto
        }
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): ResourceMipModel {
        val resourceMip = resourceMipRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Resource Mip with id $id not found") }

        return when {
            hasPermission(customUserDetails, resourceMip) -> resourceMip
            else -> throw NotAllowedException("You don't have permission to access this resource")
        }
    }

    fun getById(
        customUserDetails: CustomUserDetails,
        id: Long,
        full: Boolean = false
    ): ResourceMipDto {
        val resourceMip = findById(customUserDetails, id)
        return createDto(resourceMip, full)
    }

    fun getAll(
        full: Boolean,
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        username: String?,
        createdAt: LocalDateTime?,
        processed: Boolean?,
    ): Any {
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec = createCriteria(username, createdAt, processed)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = resourceMipRepository.findAll(spec, pageable)
                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val resourceMips = resourceMipRepository.findAll(spec, sortBy)

                resourceMips.map { createDto(it, full) }
            }
        }
    }

    fun getAllByCurrentUser(
        customUserDetails: CustomUserDetails,
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        createdAt: LocalDateTime?,
        accepted: Boolean?,
    ): Any {
        val currentUser = customUserDetails.getUserModel()
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec =
            createCriteria(
                createdAt = createdAt,
                processed = accepted,
                userId = currentUser.id
            )

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = resourceMipRepository.findAll(spec, pageable)
                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val resourceMips = resourceMipRepository.findAll(spec, sortBy)

                resourceMips.map { createDto(it) }
            }
        }
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: ResourceMipCreateSchema) {
        val currentUser = customUserDetails.getUserModel()

        val resourceMip = resourceMipRepository.saveAndFlush(
            ResourceMipModel(
                user = currentUser,
                store = request.store,
            )
        )

        resourceMipProductsService.create(resourceMip, request.items)
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: ResourceMipEditSchema) {
        val resourceMip = findById(customUserDetails, id)

        val resourceMipEdit = resourceMipRepository.saveAndFlush(
            resourceMip.copy(
                processed = request.processed ?: resourceMip.processed,
            )
        )

        request.items?.let {
            resourceMipProductsService.edit(resourceMipEdit, it)
        }
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val resourceMip = findById(customUserDetails, id)
        resourceMipRepository.delete(resourceMip)
    }
}