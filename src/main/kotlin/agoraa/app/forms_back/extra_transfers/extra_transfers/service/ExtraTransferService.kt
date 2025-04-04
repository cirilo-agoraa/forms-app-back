package agoraa.app.forms_back.extra_transfers.extra_transfers.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.shared.exception.NotAllowedException
import agoraa.app.forms_back.shared.exception.ResourceNotFoundException
import agoraa.app.forms_back.extra_transfers.extra_transfer_products.service.ExtraTransferProductsService
import agoraa.app.forms_back.extra_transfers.extra_transfers.dto.request.ExtraTransferPatchRequest
import agoraa.app.forms_back.extra_transfers.extra_transfers.dto.request.ExtraTransferRequest
import agoraa.app.forms_back.extra_transfers.extra_transfers.dto.response.ExtraTransferResponse
import agoraa.app.forms_back.extra_transfers.extra_transfers.model.ExtraTransferModel
import agoraa.app.forms_back.extra_transfers.extra_transfers.repository.ExtraTransferRepository
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
                predicates.add(criteriaBuilder.equal(root.get<agoraa.app.forms_back.shared.enums.StoresEnum>("originStore"), it))
            }

            destinyStore?.let {
                predicates.add(criteriaBuilder.equal(root.get<agoraa.app.forms_back.shared.enums.StoresEnum>("destinyStore"), it))
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

    fun createDto(extraTransfer: ExtraTransferModel, full: Boolean = false): ExtraTransferResponse {
        val userDto = userService.createDto(extraTransfer.user)
        val extraTransferDto = ExtraTransferResponse(
            id = extraTransfer.id,
            user = userDto,
            createdAt = extraTransfer.createdAt,
            processed = extraTransfer.processed,
            originStore = extraTransfer.originStore,
            destinyStore = extraTransfer.destinyStore,
            name = extraTransfer.name
        )

        if (full) {
            val extraTransferProducts = extraTransferProductsService.findByParentId(extraTransfer.id)

            extraTransferDto.products = extraTransferProducts
        }

        return extraTransferDto
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): ExtraTransferModel {
        val extraTransfer = extraTransferRepository.findById(id)
            .orElseThrow { agoraa.app.forms_back.shared.exception.ResourceNotFoundException("Extra Transfer with id $id not found") }

        return when {
            hasPermission(customUserDetails, extraTransfer) -> extraTransfer
            else -> throw agoraa.app.forms_back.shared.exception.NotAllowedException("You don't have permission to access this resource")
        }
    }

    fun getById(
        customUserDetails: CustomUserDetails,
        id: Long,
        full: Boolean = false
    ): ExtraTransferResponse {
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
    fun create(customUserDetails: CustomUserDetails, request: ExtraTransferRequest) {
        val currentUser = customUserDetails.getUserModel()

        val extraTransfer = extraTransferRepository.saveAndFlush(
            ExtraTransferModel(
                user = currentUser,
                originStore = request.originStore,
                destinyStore = request.destinyStore,
                name = request.name
            )
        )

        extraTransferProductsService.editOrCreateOrDelete(extraTransfer, request.products)
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: ExtraTransferRequest) {
        val extraTransfer = findById(customUserDetails, id)

        val extraTransferEdit = extraTransferRepository.saveAndFlush(
            extraTransfer.copy(
                processed = request.processed,
                destinyStore = request.destinyStore,
                originStore = request.originStore,
                name = request.name
            )
        )
        extraTransferProductsService.editOrCreateOrDelete(extraTransferEdit, request.products)
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val extraTransfer = findById(customUserDetails, id)
        extraTransferRepository.delete(extraTransfer)
    }

    fun patch(customUserDetails: CustomUserDetails, id: Long, request: ExtraTransferPatchRequest) {
        val extraTransfer = findById(customUserDetails, id)
        extraTransferRepository.save(
            extraTransfer.copy(
                processed = request.processed ?: extraTransfer.processed,
            )
        )
    }
}
