package agoraa.app.forms_back.store_audits.store_audits.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.products.products.model.ProductModel
import agoraa.app.forms_back.products.products.service.ProductService
import agoraa.app.forms_back.shared.enums.ProductGroupsEnum
import agoraa.app.forms_back.shared.enums.ProductSectorsEnum
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.shared.exception.NotAllowedException
import agoraa.app.forms_back.shared.exception.ResourceNotFoundException
import agoraa.app.forms_back.store_audits.store_audit_products.service.StoreAuditProductsService
import agoraa.app.forms_back.store_audits.store_audits.dto.request.StoreAuditPatchRequest
import agoraa.app.forms_back.store_audits.store_audits.dto.request.StoreAuditRequest
import agoraa.app.forms_back.store_audits.store_audits.dto.response.StoreAuditResponse
import agoraa.app.forms_back.store_audits.store_audits.model.StoreAuditModel
import agoraa.app.forms_back.store_audits.store_audits.repository.StoreAuditRepository
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
class StoreAuditService(
    private val storeAuditRepository: StoreAuditRepository,
    private val storeAuditProductsService: StoreAuditProductsService,
    private val userService: UserService,
    private val productsService: ProductService
) {
    private fun createCriteria(
        username: String? = null,
        createdAt: LocalDateTime? = null,
        processed: Boolean? = null,
        userId: Long? = null,
        createdAtGreaterThanEqual: LocalDateTime? = null,
    ): Specification<StoreAuditModel> {
        return Specification { root: Root<StoreAuditModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
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

            createdAtGreaterThanEqual?.let {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun hasPermission(
        customUserDetails: CustomUserDetails,
        storeAudit: StoreAuditModel
    ): Boolean {
        val currentUser = customUserDetails.getUserModel()
        val isAdmin = customUserDetails.authorities.any { it.authority == "ROLE_ADMIN" }
        val isOwner = storeAudit.user.id == currentUser.id

        return isAdmin || isOwner
    }

    fun createDto(
        storeAudit: StoreAuditModel,
        full: Boolean = false
    ): StoreAuditResponse {
        val userDto = userService.createDto(storeAudit.user)
        val storeAuditResponse = StoreAuditResponse(
            id = storeAudit.id,
            user = userDto,
            createdAt = storeAudit.createdAt,
            processed = storeAudit.processed
        )

        if (full) {
            val passiveQuotationProducts = storeAuditProductsService.findByParentId(storeAudit.id)

            storeAuditResponse.products =
                passiveQuotationProducts.map { storeAuditProductsService.createDto(it) }
        }

        return storeAuditResponse
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): StoreAuditModel {
        val storeAudit = storeAuditRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Store Audit with id $id not found") }

        return when {
            hasPermission(customUserDetails, storeAudit) -> storeAudit
            else -> throw NotAllowedException("You don't have permission to access this resource")
        }
    }

    fun getById(
        customUserDetails: CustomUserDetails,
        id: Long,
        full: Boolean = false
    ): StoreAuditResponse {
        val storeAudit = findById(customUserDetails, id)
        return createDto(storeAudit, full)
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
                val pageResult = storeAuditRepository.findAll(spec, pageable)
                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val extraTransfers = storeAuditRepository.findAll(spec, sortBy)

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
                val pageResult = storeAuditRepository.findAll(spec, pageable)
                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val extraTransfers = storeAuditRepository.findAll(spec, sortBy)

                extraTransfers.map { createDto(it) }
            }
        }
    }

    fun getAuditProducts(
        productsQtt: Int,
        days: Int,
        sectorsNotIn: List<ProductSectorsEnum>,
        groupNamesNotIn: List<ProductGroupsEnum>
    ): List<ProductModel> {
        val today = LocalDateTime.now()
        val targetDay = today.minusDays(days.toLong())

        val spec = createCriteria(createdAtGreaterThanEqual = targetDay)
        val storeAudits = storeAuditRepository.findAll(spec)
        val storeAuditResponses = storeAudits.map { createDto(it) }

        val storeAuditProducts = storeAuditResponses.flatMap { storeAuditResponse ->
            storeAuditResponse.products?.map { storeAuditProducts -> storeAuditProducts.product } ?: emptyList()
        }.distinct().toSet()
        val products = productsService.findAll(
            stores = listOf(StoresEnum.TRESMANN_VIX),
            currentStockGreaterThan = 0.0,
            salesLastSevenDaysEqual = 0.0,
            outOfMix = false,
            sectorsNotIn = sectorsNotIn,
            groupNamesNotIn = groupNamesNotIn
        ).toSet()

        return (products - storeAuditProducts).toList()
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: StoreAuditRequest) {
        val currentUser = customUserDetails.getUserModel()
        val storeAudit = storeAuditRepository.saveAndFlush(StoreAuditModel(user = currentUser))

        storeAuditProductsService.editOrCreateOrDelete(storeAudit, request.products)
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: StoreAuditRequest) {
        val storeAudit = findById(customUserDetails, id)
        val storeAuditEdit = storeAuditRepository.saveAndFlush(storeAudit.copy(processed = request.processed))

        storeAuditProductsService.editOrCreateOrDelete(storeAuditEdit, request.products)
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val storeAudit = findById(customUserDetails, id)

        storeAuditRepository.delete(storeAudit)
    }

    fun patch(customUserDetails: CustomUserDetails, id: Long, request: StoreAuditPatchRequest) {
        val storeAudit = findById(customUserDetails, id)

        storeAuditRepository.save(storeAudit.copy(processed = request.processed ?: storeAudit.processed))
    }
}
