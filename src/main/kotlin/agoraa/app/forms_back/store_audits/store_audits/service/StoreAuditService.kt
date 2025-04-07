package agoraa.app.forms_back.store_audits.store_audits.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.products.products.service.ProductService
import agoraa.app.forms_back.shared.enums.ProductGroupsEnum
import agoraa.app.forms_back.shared.enums.ProductSectorsEnum
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.shared.exception.NotAllowedException
import agoraa.app.forms_back.shared.exception.ResourceNotFoundException
import agoraa.app.forms_back.store_audits.store_audit_products.dto.request.StoreAuditProductsRequest
import agoraa.app.forms_back.store_audits.store_audit_products.service.StoreAuditProductsService
import agoraa.app.forms_back.store_audits.store_audits.dto.request.StoreAuditPatchRequest
import agoraa.app.forms_back.store_audits.store_audits.dto.request.StoreAuditRequest
import agoraa.app.forms_back.store_audits.store_audits.dto.response.StoreAuditResponse
import agoraa.app.forms_back.store_audits.store_audits.model.StoreAuditModel
import agoraa.app.forms_back.store_audits.store_audits.repository.StoreAuditRepository
import agoraa.app.forms_back.users.users.model.UserModel
import agoraa.app.forms_back.users.users.service.UserService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime

@Service
class StoreAuditService(
    private val storeAuditRepository: StoreAuditRepository,
    private val storeAuditProductsService: StoreAuditProductsService,
    private val userService: UserService,
    private val productService: ProductService
) {
    private val objectMapper = jacksonObjectMapper()
    private val configFilePath =
        "F:\\COMPRAS\\Automações.Compras\\JSONS PARAMETROS\\config_formulario_auditoria_de_loja.json"
    private val config = readConfig()

    private fun readConfig(): Map<String, Any> {
        val path = Paths.get(configFilePath)
        val jsonString = Files.readString(path)
        return objectMapper.readValue(jsonString)
    }

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
        createdAtGreaterThanEqual: LocalDateTime? = null
    ): Any {
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)
        val spec = createCriteria(username, createdAt, processed, createdAtGreaterThanEqual = createdAtGreaterThanEqual)

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

    @Scheduled(cron = "0 35 16 * * ?", zone = "America/Sao_Paulo")
    @Transactional
    fun createAudit() {
        val botUser = userService.findByUsername("bot@forms.com")
        val sectorsNotIn = config["EXCECAO_SETORES"] as List<ProductSectorsEnum>
        val groupNamesNotIn = config["EXCECAO_GRUPOS"] as List<ProductGroupsEnum>
        val targetDate = config["DIAS_PARA_NAO_REPETIR_PRODUTOS"] as Long
        val dailyProductsLimit = config["LIMITE_DIARIO_DE_PRODUTOS"] as Int

        val products = productService.findAll(
            stores = listOf(StoresEnum.TRESMANN_VIX),
            outOfMix = false,
            currentStockGreaterThan = 0.0,
            salesLastSevenDaysEqual = 0.0,
            sectorsNotIn = sectorsNotIn,
            groupNamesNotIn = groupNamesNotIn,
        ).toSet()

        val spec = createCriteria(createdAtGreaterThanEqual = LocalDateTime.now().minusDays(targetDate))
        val storeAudits = storeAuditRepository.findAll(spec)
        val storeAuditsProducts = storeAudits.map { storeAuditProductsService.findByParentId(it.id) }
            .map { storeAuditProducts -> storeAuditProducts.map { it.product } }
            .flatten()
            .toSet()

        println("Products: ${products.size}")
        println("Store Audits Products: ${storeAuditsProducts.size}")

        val finalProducts = (products - storeAuditsProducts).take(dailyProductsLimit).map { finalProduct ->
            StoreAuditProductsRequest(
                product = finalProduct,
                inStore = false
            )
        }

        val newAudit = storeAuditRepository.saveAndFlush(StoreAuditModel(user = botUser))

        storeAuditProductsService.editOrCreateOrDelete(newAudit, finalProducts)
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "America/Sao_Paulo")
    fun closeExpiredAudits() {
        val today = LocalDateTime.now()
        val paramDate = config["DURACAO_DO_FORMULARIO_EM_DIAS"] as Long

        val spec = createCriteria(processed = false)
        val audits = storeAuditRepository.findAll(spec)
        val closedAudits = audits.mapNotNull { audit ->
            if (audit.createdAt.plusDays(paramDate) <= today) {
                audit.copy(processed = true)
            } else {
                null
            }
        }
        storeAuditRepository.saveAll(closedAudits)
    }
}
