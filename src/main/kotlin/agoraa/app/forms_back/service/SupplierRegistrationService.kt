package agoraa.app.forms_back.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.dto.supplier_registration.SupplierRegistrationDto
import agoraa.app.forms_back.enum.suppliers_registration.SuppliersRegistrationTypesEnum
import agoraa.app.forms_back.exception.NotAllowedException
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.SupplierRegistrationModel
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.repository.SupplierRegistrationRepository
import agoraa.app.forms_back.schema.supplier_registration.SupplierRegistrationCreateSchema
import agoraa.app.forms_back.schema.supplier_registration.SupplierRegistrationEditSchema
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
class SupplierRegistrationService(
    private val userService: UserService,
    private val supplierRegistrationService: SupplierRegistrationStoresService,
    private val supplierRegistrationStoresService: SupplierRegistrationStoresService,
    private val supplierRegistrationRepository: SupplierRegistrationRepository
) {

    private fun validateSchema(request: SupplierRegistrationCreateSchema) {
        val requiredFields = when (request.type) {
            "REPOSICAO" -> listOf(
                request.address,
                request.factoryWebsite,
                request.exchange,
                request.exchangePhysical,
                request.priceTableFilePath,
                request.catalogFilePath,
                request.sampleDate,
                request.investmentsOnStore,
                request.purchaseGondola,
                request.participateInInsert,
                request.birthdayParty,
                request.otherParticipation,
                request.negotiateBonusOnFirstPurchase
            )
            else -> listOf(
                request.sellerName,
                request.supplierWebsite,
                request.minimumOrderValue,
                request.weeklyQuotation
            )
        }

        if (requiredFields.any { it == null }) {
            throw IllegalArgumentException("Invalid payload")
        }
    }

    private fun createCriteria(
        username: String? = null,
        createdAt: LocalDateTime? = null,
        accepted: Boolean? = null,
        type: SuppliersRegistrationTypesEnum? = null,
        cnpj: String? = null,
        userId: Long? = null,
    ): Specification<SupplierRegistrationModel> {
        return Specification { root: Root<SupplierRegistrationModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
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

            accepted?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("accepted"), it))
            }

            type?.let {
                predicates.add(criteriaBuilder.equal(root.get<SuppliersRegistrationTypesEnum>("type"), it))
            }

            cnpj?.let {
                predicates.add(criteriaBuilder.like(root.get("cnpj"), "%$it%"))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun hasPermission(
        customUserDetails: CustomUserDetails,
        supplierRegistration: SupplierRegistrationModel
    ): Boolean {
        val currentUser = customUserDetails.getUserModel()
        val isAdmin = customUserDetails.authorities.any { it.authority == "ROLE_ADMIN" }
        val isOwner = supplierRegistration.user.id == currentUser.id

        return isAdmin || isOwner
    }

    fun createDto(supplierRegistration: SupplierRegistrationModel, full: Boolean? = null): SupplierRegistrationDto {
        val userDto = userService.createDto(supplierRegistration.user)
        val supplierRegistrationDto = SupplierRegistrationDto(
            id = supplierRegistration.id,
            user = userDto,
            createdAt = supplierRegistration.createdAt,
            accepted = supplierRegistration.accepted,
        )

        return when {
            full == true -> {
                val supplierRegistrationStores = supplierRegistrationService.findByParentId(supplierRegistration.id)
                supplierRegistrationDto.stores = supplierRegistrationStores
                supplierRegistrationDto
            }

            else -> supplierRegistrationDto
        }
    }

    fun findById(customUserDetails: CustomUserDetails, id: Long): SupplierRegistrationModel {
        val supplierRegistration = supplierRegistrationRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Supplier Registration with id $id not found") }

        return when {
            hasPermission(customUserDetails, supplierRegistration) -> supplierRegistration
            else -> throw NotAllowedException("You don't have permission to access this resource")
        }
    }

    fun getById(
        customUserDetails: CustomUserDetails,
        id: Long,
        full: Boolean? = null
    ): SupplierRegistrationDto {
        val supplierRegistration = findById(customUserDetails, id)

        return createDto(supplierRegistration, full)
    }

    fun getAll(
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        username: String?,
        createdAt: LocalDateTime?,
        accepted: Boolean?,
        type: SuppliersRegistrationTypesEnum?,
        cnpj: String?,
    ): Any {
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec = createCriteria(username, createdAt, accepted)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = supplierRegistrationRepository.findAll(spec, pageable)

                return pageResult
            }

            else -> {
                val supplierRegistrations = supplierRegistrationRepository.findAll(spec, sortBy)

                supplierRegistrations
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
        accepted: Boolean?,
        type: SuppliersRegistrationTypesEnum?,
        cnpj: String?,
    ): Any {
        val currentUser = customUserDetails.getUserModel()
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val pageable = PageRequest.of(page, size, sortBy)
        val spec =
            createCriteria(
                createdAt = createdAt,
                accepted = accepted,
                type = type,
                cnpj = cnpj,
                userId = currentUser.id
            )

        return supplierRegistrationRepository.findAll(spec, pageable)
    }

    @Transactional
    fun create(customUserDetails: CustomUserDetails, request: SupplierRegistrationCreateSchema) {
        val currentUser = customUserDetails.getUserModel()
        validateSchema(request)

        val supplierRegistration = supplierRegistrationRepository.saveAndFlush(
            SupplierRegistrationModel(
                user = currentUser,
                companyName = request.companyName,
                paymentTerm = request.paymentTerm,
                type = SuppliersRegistrationTypesEnum.valueOf(request.type),
                sellerPhone = request.sellerPhone,
                sellerEmail = request.sellerEmail,
                cnpj = request.cnpj,

                address = request.address,
                exchange = request.exchange,
                factoryWebsite = request.factoryWebsite,
                sampleDate = request.sampleDate,
                negotiateBonusOnFirstPurchase = request.negotiateBonusOnFirstPurchase,
                birthdayParty = request.birthdayParty,
                priceTableFilePath = request.priceTableFilePath,
                catalogFilePath = request.catalogFilePath,
                exchangePhysical = request.exchangePhysical,
                investmentsOnStore = request.investmentsOnStore,
                otherParticipation = request.otherParticipation,
                participateInInsert = request.participateInInsert,
                purchaseGondola = request.purchaseGondola,

                sellerName = request.sellerName,
                supplierWebsite = request.supplierWebsite,
                minimumOrderValue = request.minimumOrderValue,
                weeklyQuotation = request.weeklyQuotation,

                obs = request.obs,
            )
        )

        supplierRegistrationStoresService.create(supplierRegistration, request.stores)

    }

    @Transactional
    fun edit(customUserDetails: CustomUserDetails, id: Long, request: SupplierRegistrationEditSchema) {
        val supplierRegistration = findById(customUserDetails, id)

        val supplierRegistrationEdited = supplierRegistrationRepository.saveAndFlush(
            supplierRegistration.copy(
                sellerPhone = request.sellerPhone ?: supplierRegistration.sellerPhone,
                sellerName = request.sellerName ?: supplierRegistration.sellerName,
                sellerEmail = request.sellerEmail ?: supplierRegistration.sellerEmail,
                supplierWebsite = request.supplierWebsite ?: supplierRegistration.supplierWebsite,
                cnpj = request.cnpj ?: supplierRegistration.cnpj,
                exchangePhysical = request.exchangePhysical ?: supplierRegistration.exchangePhysical,
                participateInInsert = request.participateInInsert ?: supplierRegistration.participateInInsert,
                minimumOrderValue = request.minimumOrderValue ?: supplierRegistration.minimumOrderValue,
                investmentsOnStore = request.investmentsOnStore ?: supplierRegistration.investmentsOnStore,
                otherParticipation = request.otherParticipation ?: supplierRegistration.otherParticipation,
                priceTableFilePath = request.priceTableFilePath ?: supplierRegistration.priceTableFilePath,
                catalogFilePath = request.catalogFilePath ?: supplierRegistration.catalogFilePath,
                factoryWebsite = request.factoryWebsite ?: supplierRegistration.factoryWebsite,
                sampleDate = request.sampleDate ?: supplierRegistration.sampleDate,
                birthdayParty = request.birthdayParty ?: supplierRegistration.birthdayParty,
                address = request.address ?: supplierRegistration.address,
                companyName = request.companyName ?: supplierRegistration.companyName,
                paymentTerm = request.paymentTerm ?: supplierRegistration.paymentTerm,
                type = request.type ?: supplierRegistration.type,
                obs = request.obs ?: supplierRegistration.obs,
                purchaseGondola = request.purchaseGondola ?: supplierRegistration.purchaseGondola,
                weeklyQuotation = request.weeklyQuotation ?: supplierRegistration.weeklyQuotation,
                exchange = request.exchange ?: supplierRegistration.exchange,
                negotiateBonusOnFirstPurchase = request.negotiateBonusOnFirstPurchase
                    ?: supplierRegistration.negotiateBonusOnFirstPurchase,
            )
        )

        request.stores?.let { supplierRegistrationStoresService.edit(supplierRegistrationEdited, it) }
    }

    @Transactional
    fun delete(customUserDetails: CustomUserDetails, id: Long) {
        val supplierRegistration = findById(customUserDetails, id)
        supplierRegistrationRepository.delete(supplierRegistration)
    }
}
