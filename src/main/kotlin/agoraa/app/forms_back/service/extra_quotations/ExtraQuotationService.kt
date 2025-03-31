package agoraa.app.forms_back.service.extra_quotations

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.dto.extra_quotations.ExtraQuotationDto
import agoraa.app.forms_back.exception.NotAllowedException
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.extra_quotations.ExtraQuotationModel
import agoraa.app.forms_back.repository.extra_quotations.ExtraQuotationRepository
import agoraa.app.forms_back.schema.extra_quotations.ExtraQuotationCreateSchema
import agoraa.app.forms_back.schema.extra_quotations.ExtraQuotationEditSchema
import agoraa.app.forms_back.users.users.model.UserModel
import agoraa.app.forms_back.users.users.service.UserService
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
class ExtraQuotationService(
    private val extraQuotationRepository: ExtraQuotationRepository,
    private val extraQuotationProductsService: ExtraQuotationProductsService,
    private val userService: UserService
) {

    private fun createCriteria(
        username: String? = null,
        createdAt: LocalDateTime? = null,
        processed: Boolean? = null,
        userId: Long? = null,
    ): Specification<ExtraQuotationModel> {
        return Specification { root: Root<ExtraQuotationModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            userId?.let {
                predicates.add(criteriaBuilder.equal(root.get<UserModel>("user").get<Long>("id"), it))
            }

            username?.let {
                predicates.add(criteriaBuilder.like(root.get<UserModel>("user").get("username"), "%$it%"))
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
        extraOrder: ExtraQuotationModel
    ): Boolean {
        val currentUser = customUserDetails.getUserModel()
        val isAdmin = customUserDetails.authorities.any { it.authority == "ROLE_ADMIN" }
        val isOwner = extraOrder.user.id == currentUser.id

        return isAdmin || isOwner
    }

    fun createDto(extraQuotation: ExtraQuotationModel, full: Boolean = false): ExtraQuotationDto {
        val userDto = userService.createDto(extraQuotation.user)
        val extraQuotationDto = ExtraQuotationDto(
            id = extraQuotation.id,
            user = userDto,
            createdAt = extraQuotation.createdAt,
            processed = extraQuotation.processed,
        )

        if (full) {
            val extraQuotationProducts = extraQuotationProductsService.findByParentId(extraQuotation.id)

            extraQuotationDto.products = extraQuotationProducts
        }

        return extraQuotationDto
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): ExtraQuotationModel {
        val extraQuotation = extraQuotationRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Extra Order with id $id not found") }

        return when {
            hasPermission(customUserDetails, extraQuotation) -> extraQuotation
            else -> throw NotAllowedException("You don't have permission to access this resource")
        }
    }

    fun getById(
        customUserDetails: CustomUserDetails,
        id: Long,
        full: Boolean = false
    ): ExtraQuotationDto {
        val extraQuotation = findById(customUserDetails, id)
        return createDto(extraQuotation, full)
    }

    fun getAll(
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
                val pageResult = extraQuotationRepository.findAll(spec, pageable)
                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val extraorders = extraQuotationRepository.findAll(spec, sortBy)

                extraorders.map { createDto(it) }
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
                val pageResult = extraQuotationRepository.findAll(spec, pageable)
                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val extraorders = extraQuotationRepository.findAll(spec, sortBy)

                extraorders.map { createDto(it) }
            }
        }
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: ExtraQuotationCreateSchema) {
        val currentUser = customUserDetails.getUserModel()

        val extraQuotation = extraQuotationRepository.saveAndFlush(
            ExtraQuotationModel(
                user = currentUser,
            )
        )

        extraQuotationProductsService.create(extraQuotation, request.products)
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: ExtraQuotationEditSchema) {
        val extraQuotation = findById(customUserDetails, id)

        val extraQuotationEdit = extraQuotationRepository.saveAndFlush(
            extraQuotation.copy(
                processed = request.processed ?: extraQuotation.processed,
            )
        )

        request.products?.let {
            extraQuotationProductsService.edit(extraQuotationEdit, it)
        }
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val extraQuotation = findById(customUserDetails, id)
        extraQuotationRepository.delete(extraQuotation)
    }
}
