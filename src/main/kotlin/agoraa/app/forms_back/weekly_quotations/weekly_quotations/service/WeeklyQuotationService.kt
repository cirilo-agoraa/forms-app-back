package agoraa.app.forms_back.weekly_quotations.weekly_quotations.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.shared.enums.ProductSectorsEnum
import agoraa.app.forms_back.users.users.model.UserModel
import agoraa.app.forms_back.users.users.service.UserService
import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.dto.response.WeeklyQuotationSummariesAnalysisResponse
import agoraa.app.forms_back.weekly_quotations.weekly_quotation_summaries.service.WeeklyQuotationSummariesService
import agoraa.app.forms_back.weekly_quotations.weekly_quotations.dto.request.WeeklyQuotationRequest
import agoraa.app.forms_back.weekly_quotations.weekly_quotations.dto.response.WeeklyQuotationResponse
import agoraa.app.forms_back.weekly_quotations.weekly_quotations.model.WeeklyQuotationModel
import agoraa.app.forms_back.weekly_quotations.weekly_quotations.repository.WeeklyQuotationRepository
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
class WeeklyQuotationService(
    private val weeklyQuotationRepository: WeeklyQuotationRepository,
    private val weeklyQuotationSummariesService: WeeklyQuotationSummariesService,
    private val userService: UserService
) {
    private fun createCriteria(
        usernameLike: String? = null,
        createdAtEqual: LocalDateTime? = null,
        sectorEqual: ProductSectorsEnum? = null,
        userIdEqual: Long? = null,
    ): Specification<WeeklyQuotationModel> {
        return Specification { root: Root<WeeklyQuotationModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            userIdEqual?.let {
                predicates.add(criteriaBuilder.equal(root.get<UserModel>("user").get<Long>("id"), it))
            }

            usernameLike?.let {
                predicates.add(criteriaBuilder.like(root.get<UserModel>("user").get("username"), "%$it%"))
            }

            createdAtEqual?.let {
                predicates.add(criteriaBuilder.equal(root.get<LocalDateTime>("createdAt"), it))
            }

            sectorEqual?.let {
                predicates.add(criteriaBuilder.equal(root.get<ProductSectorsEnum>("sector"), it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun hasPermission(
        customUserDetails: CustomUserDetails,
        weeklyQuotationModel: WeeklyQuotationModel
    ): Boolean {
        val currentUser = customUserDetails.getUserModel()
        val isAdmin = customUserDetails.authorities.any { it.authority == "ROLE_ADMIN" }
        val isOwner = weeklyQuotationModel.user.id == currentUser.id

        return isAdmin || isOwner
    }

    fun createDto(
        weeklyQuotationModel: WeeklyQuotationModel,
        full: Boolean = false
    ): WeeklyQuotationResponse {
        val userDto = userService.createDto(weeklyQuotationModel.user)
        val weeklyQuotationResponse = WeeklyQuotationResponse(
            id = weeklyQuotationModel.id,
            user = userDto,
            createdAt = weeklyQuotationModel.createdAt,
            sector = weeklyQuotationModel.sector,
            quotationDate = weeklyQuotationModel.quotationDate
        )

        if (full) {
            val passiveQuotationProducts = weeklyQuotationSummariesService.findByParentId(weeklyQuotationModel.id)

            weeklyQuotationResponse.summaries =
                passiveQuotationProducts.map { weeklyQuotationSummariesService.createDto(it) }
        }

        return weeklyQuotationResponse
    }

    fun findById(
        customUserDetails: CustomUserDetails,
        id: Long
    ): WeeklyQuotationModel {
        val weeklyQuotationModel = weeklyQuotationRepository.findById(id)
            .orElseThrow { agoraa.app.forms_back.shared.exception.ResourceNotFoundException("Weekly Quotation with id $id not found") }

        return when {
            hasPermission(customUserDetails, weeklyQuotationModel) -> weeklyQuotationModel
            else -> throw agoraa.app.forms_back.shared.exception.NotAllowedException("You don't have permission to access this resource")
        }
    }

    fun getById(
        customUserDetails: CustomUserDetails,
        id: Long,
        full: Boolean = false
    ): WeeklyQuotationResponse {
        val weeklyQuotationModel = findById(customUserDetails, id)
        return createDto(weeklyQuotationModel, full)
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
        sector: ProductSectorsEnum?,
    ): Any {
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec = createCriteria(username, createdAt, sector)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = weeklyQuotationRepository.findAll(spec, pageable)
                return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
            }

            else -> {
                val weeklyQuotationModels = weeklyQuotationRepository.findAll(spec, sortBy)

                weeklyQuotationModels.map { createDto(it, full) }
            }
        }
    }

    fun getAllByCurrentUser(
        customUserDetails: CustomUserDetails,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        createdAt: LocalDateTime?,
        sector: ProductSectorsEnum?,
    ): Any {
        val currentUser = customUserDetails.getUserModel()
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec =
            createCriteria(
                createdAtEqual = createdAt,
                sectorEqual = sector,
                userIdEqual = currentUser.id
            )

        val pageable = PageRequest.of(page, size, sortBy)
        val pageResult = weeklyQuotationRepository.findAll(spec, pageable)
        return PageImpl(pageResult.content.map { createDto(it) }, pageable, pageResult.totalElements)
    }

    fun getSummaryAnalysis(sector: ProductSectorsEnum): List<WeeklyQuotationSummariesAnalysisResponse> {
        return weeklyQuotationSummariesService.summaryAnalysis(sector)
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: WeeklyQuotationRequest) {
        val currentUser = customUserDetails.getUserModel()

        val weeklyQuotationModel = weeklyQuotationRepository.saveAndFlush(
            WeeklyQuotationModel(
                user = currentUser,
                sector = request.sector,
                quotationDate = request.quotationDate
            )
        )

        weeklyQuotationSummariesService.editOrCreateOrDelete(weeklyQuotationModel, request.summaries)
    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: WeeklyQuotationRequest) {
        val weeklyQuotationModel = findById(customUserDetails, id)

        val editedWeeklyQuotationModel = weeklyQuotationRepository.saveAndFlush(
            weeklyQuotationModel.copy(
                sector = request.sector,
                quotationDate = request.quotationDate
            )
        )
        weeklyQuotationSummariesService.editOrCreateOrDelete(editedWeeklyQuotationModel, request.summaries)
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val weeklyQuotationModel = findById(customUserDetails, id)
        weeklyQuotationRepository.delete(weeklyQuotationModel)
    }
}