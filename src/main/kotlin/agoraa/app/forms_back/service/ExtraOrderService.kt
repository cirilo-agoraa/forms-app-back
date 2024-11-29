package agoraa.app.forms_back.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.enums.extra_order.OriginEnum
import agoraa.app.forms_back.enums.extra_order.PartialCompleteEnum
import agoraa.app.forms_back.exceptions.NotAllowedException
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.ExtraOrderModel
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.repository.ExtraOrderRepository
import agoraa.app.forms_back.schema.extra_order.ExtraOrderCreateSchema
import agoraa.app.forms_back.schema.extra_order.ExtraOrderEditSchema
import jakarta.transaction.Transactional
import org.springdoc.api.OpenApiResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class ExtraOrderService(
    private val extraOrderRepository: ExtraOrderRepository,
    private val supplierService: SupplierService,
    private val userService: UserService,
    private val extraOrderProductService: ExtraOrderProductService
) {

    fun findAll(
        customUserDetails: CustomUserDetails,
        supplierId: Long,
        userId: Long,
        processed: String,
        dateSubmitted: String,
        origin: String,
        partialComplete: String,
        page: Int,
        size: Int,
        sort: String,
        direction: String
    ): Page<ExtraOrderModel> {
        val currentUser = customUserDetails.getUserModel()
        val sortDirection = if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))

        return when {
            supplierId != 0L -> {
                val supplier = supplierService.findById(supplierId)
                if (customUserDetails.authorities.map { it.authority }.contains("ROLE_ADMIN")) {
                    extraOrderRepository.findBySupplierId(supplier.id, pageable)
                } else {
                    throw NotAllowedException("You are not allowed to access this resource")
                }
            }

            userId != 0L -> {
                val user = userService.findById(userId)
                if (customUserDetails.authorities.map { it.authority }.contains("ROLE_ADMIN")) {
                    extraOrderRepository.findByUserId(user.id, pageable)
                } else {
                    throw NotAllowedException("You are not allowed to access this resource")
                }
            }

            processed.isNotEmpty() -> {
                if (customUserDetails.authorities.map { it.authority }.contains("ROLE_ADMIN")) {
                    extraOrderRepository.findByProcessed(processed.toBoolean(), pageable)
                } else {
                    extraOrderRepository.findByProcessedAndUserId(processed.toBoolean(), currentUser.id, pageable)
                }
            }

            dateSubmitted.isNotEmpty() -> {
                if (customUserDetails.authorities.map { it.authority }.contains("ROLE_ADMIN")) {
                    extraOrderRepository.findByDateSubmitted(LocalDate.parse(dateSubmitted), pageable)
                } else {
                    extraOrderRepository.findByDateSubmittedAndUserId(
                        LocalDate.parse(dateSubmitted),
                        currentUser.id,
                        pageable
                    )
                }
            }

            origin.isNotEmpty() -> {
                if (customUserDetails.authorities.map { it.authority }.contains("ROLE_ADMIN")) {
                    extraOrderRepository.findByOrigin(origin, pageable)
                } else {
                    extraOrderRepository.findByOriginAndUserId(origin, currentUser.id, pageable)
                }
            }

            partialComplete.isNotEmpty() -> {
                if (customUserDetails.authorities.map { it.authority }.contains("ROLE_ADMIN")) {
                    extraOrderRepository.findByPartialComplete(partialComplete, pageable)
                } else {
                    extraOrderRepository.findByPartialCompleteAndUserId(partialComplete, currentUser.id, pageable)
                }
            }

            else -> {
                if (customUserDetails.authorities.map { it.authority }.contains("ROLE_ADMIN")) {
                    extraOrderRepository.findAll(pageable)
                } else {
                    extraOrderRepository.findByUserId(currentUser.id, pageable)
                }
            }
        }
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): ExtraOrderModel {
        val user = customUserDetails.getUserModel()
        return extraOrderRepository.findById(id)
            .map { extraOrder ->
                if (extraOrder.user.id == user.id || customUserDetails.authorities.map { it.authority }
                        .contains("ROLE_ADMIN")) {
                    extraOrder
                } else {
                    throw NotAllowedException("You are not allowed to access this resource")
                }
            }
            .orElseThrow { throw ResourceNotFoundException("Extra Order not Found") }
    }

    fun edit(customUserDetails: CustomUserDetails, id: Long, request: ExtraOrderEditSchema): ExtraOrderModel {
        val extraOrder = findById(customUserDetails, id)
        val user = request.userId?.let { userService.findById(it) }
        val supplier = request.supplierId?.let { supplierService.findById(it) }

        val extraOrderEdited = extraOrder.copy(
            user = user ?: extraOrder.user,
            supplier = supplier ?: extraOrder.supplier,
            partialComplete = request.partialComplete?.let { PartialCompleteEnum.valueOf(it) }
                ?: extraOrder.partialComplete,
            origin = request.origin?.let { OriginEnum.valueOf(it) } ?: extraOrder.origin,
            storesComplete = request.storesComplete?.map { StoresEnum.valueOf(it) }
                ?: extraOrder.storesComplete,
            storePartial = request.storePartial?.let { StoresEnum.valueOf(it) } ?: extraOrder.storePartial,
            processed = request.processed ?: extraOrder.processed,
            dateSubmitted = request.dateSubmitted?.let { LocalDate.parse(it) } ?: extraOrder.dateSubmitted
        )
        return extraOrderRepository.save(extraOrderEdited)
    }

    @Transactional
    fun create(user: UserModel, request: ExtraOrderCreateSchema): ExtraOrderModel {
        val supplier = supplierService.findById(request.supplierId)

        when (PartialCompleteEnum.valueOf(request.partialComplete)) {
            PartialCompleteEnum.PARCIAL -> {
                if (request.storePartial == null || request.origin == null) {
                    throw IllegalArgumentException("Partial order must have origin and store")
                }
            }

            PartialCompleteEnum.COMPLETO -> {
                if (request.storesComplete == null) {
                    throw IllegalArgumentException("Complete order must have at least one store")
                }
            }
        }

        val extraOrder = extraOrderRepository.save(
            ExtraOrderModel(
                user = user,
                supplier = supplier,
                partialComplete = PartialCompleteEnum.valueOf(request.partialComplete),
                origin = request.origin?.let { OriginEnum.valueOf(it) },
                storesComplete = request.storesComplete?.map { StoresEnum.valueOf(it) },
                storePartial = request.storePartial?.let { StoresEnum.valueOf(it) }
            )
        )
        extraOrderProductService.create(extraOrder, request.productsInfo)

        return extraOrder
    }
}