package agoraa.app.forms_back.service.extra_transfers

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.dto.extra_transfers.ExtraTransferDto
import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.exception.NotAllowedException
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.model.extra_transfers.ExtraTransferModel
import agoraa.app.forms_back.repository.extra_transfers.ExtraTransferRepository
import agoraa.app.forms_back.schema.extra_transfers.ExtraTransferCreateSchema
import agoraa.app.forms_back.schema.extra_transfers.ExtraTransferEditSchema
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
class ExtraTransferService(
    private val extraTransferRepository: ExtraTransferRepository,
    private val extraTransferProductsService: ExtraTransferProductsService,
    private val userService: UserService
) {

    private fun createCriteria(
        username: String? = null,
        createdAt: LocalDateTime? = null,
        processed: Boolean? = null,
        userId: Long? = null,
        originStore: StoresEnum? = null,
        destinyStore: StoresEnum? = null,
    ): Specification<ExtraTransferModel> {
        return Specification { root: Root<ExtraTransferModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
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

            originStore?.let {
                predicates.add(criteriaBuilder.equal(root.get<StoresEnum>("originStore"), it))
            }

            destinyStore?.let {
                predicates.add(criteriaBuilder.equal(root.get<StoresEnum>("destinyStore"), it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun hasPermission(
        customUserDetails: CustomUserDetails,
        extraTransfer: ExtraTransferModel
    ): Boolean {
        val currentUser = customUserDetails.getUserModel()
        val isAdmin = customUserDetails.authorities.any { it.authority == "ROLE_ADMIN" }
        val isOwner = extraTransfer.user.id == currentUser.id

        return isAdmin || isOwner
    }

    fun createDto(extraTransfer: ExtraTransferModel, full: Boolean = false): ExtraTransferDto {
        val userDto = userService.createDto(extraTransfer.user)
        val extraTransferDto = ExtraTransferDto(
            id = extraTransfer.id,
            user = userDto,
            createdAt = extraTransfer.createdAt,
            processed = extraTransfer.processed,
            originStore = extraTransfer.originStore,
            destinyStore = extraTransfer.destinyStore,
        )

        return when {
            full -> {
                val extraTransferProducts = extraTransferProductsService.findByParentId(extraTransfer.id)

                extraTransferDto.products = extraTransferProducts
                extraTransferDto
            }

            else -> extraTransferDto
        }
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): ExtraTransferModel {
        val extraTransfer = extraTransferRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Extra Transfer with id $id not found") }

        return when {
            hasPermission(customUserDetails, extraTransfer) -> extraTransfer
            else -> throw NotAllowedException("You don't have permission to access this resource")
        }
    }

    fun getById(
        customUserDetails: CustomUserDetails,
        id: Long,
        full: Boolean = false
    ): ExtraTransferDto {
        val extraTransfer = findById(customUserDetails, id)
        return createDto(extraTransfer, full)
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
                val pageResult = extraTransferRepository.findAll(spec, pageable)
                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val extraTransfers = extraTransferRepository.findAll(spec, sortBy)

                extraTransfers.map { createDto(it, full) }
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
                val pageResult = extraTransferRepository.findAll(spec, pageable)
                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val extraTransfers = extraTransferRepository.findAll(spec, sortBy)

                extraTransfers.map { createDto(it) }
            }
        }
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: ExtraTransferCreateSchema) {
        val currentUser = customUserDetails.getUserModel()

        val extraTransfer = extraTransferRepository.saveAndFlush(
            ExtraTransferModel(
                user = currentUser,
                originStore = request.originStore,
                destinyStore = request.destinyStore,
            )
        )

        extraTransferProductsService.create(extraTransfer, request.products)
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: ExtraTransferEditSchema) {
        val extraTransfer = findById(customUserDetails, id)

        val extraTransferEdit = extraTransferRepository.saveAndFlush(
            extraTransfer.copy(
                processed = request.processed ?: extraTransfer.processed,
                destinyStore = request.destinyStore ?: extraTransfer.destinyStore,
                originStore = request.originStore ?: extraTransfer.originStore,
            )
        )

        request.products?.let {
            extraTransferProductsService.edit(extraTransferEdit, it)
        }
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val extraTransfer = findById(customUserDetails, id)
        extraTransferRepository.delete(extraTransfer)
    }
}
