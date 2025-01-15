package agoraa.app.forms_back.service

import agoraa.app.forms_back.dto.product.ProductDto
import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.enum.product.ProductDtoOptionsEnum
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.ProductModel
import agoraa.app.forms_back.model.SupplierModel
import agoraa.app.forms_back.repository.ProductRepository
import agoraa.app.forms_back.schema.product.ProductCreateSchema
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
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val supplierService: SupplierService
) {

    fun createDto(fields: List<String>, productModel: ProductModel): ProductDto {
        val productDto = ProductDto(
            id = productModel.id
        )

        val productModelProperties = ProductModel::class.memberProperties.associateBy { it.name }
        val productDtoProperties = ProductDto::class.memberProperties.associateBy { it.name }

        fields.forEach { field ->
            val modelProperty = productModelProperties[field]
            val dtoProperty = productDtoProperties[field]

            if (modelProperty != null && dtoProperty != null) {
                if (dtoProperty is KMutableProperty<*>) {
                    val value = modelProperty.get(productModel)
                    dtoProperty.setter.call(productDto, value)
                }
            }
        }

        if (fields.contains("supplier")) {
            productDto.supplier = supplierService.createDto(fields, productModel.supplier)
        }

        return productDto
    }

    private fun createCriteria(
        outOfMix: Boolean? = null,
        supplierId: Long? = null,
        supplierName: String? = null,
        name: String? = null,
        code: String? = null,
        store: List<StoresEnum>? = null,
        isResource: Boolean? = null,
    ): Specification<ProductModel> {
        return Specification { root: Root<ProductModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            outOfMix?.let {
                predicates.add(root.get<Boolean>("outOfMix").`in`(it))
            }

            supplierId?.let {
                predicates.add(criteriaBuilder.equal(root.get<SupplierModel>("supplier").get<Long>("id"), it))
            }

            supplierName?.let {
                predicates.add(criteriaBuilder.like(root.get<SupplierModel>("supplier").get("name"), "%$it%"))
            }

            name?.let {
                predicates.add(criteriaBuilder.like(root.get("name"), "%$it%"))
            }

            code?.let {
                predicates.add(criteriaBuilder.like(root.get("code"), "%$it%"))
            }

            store?.let {
                predicates.add(root.get<StoresEnum>("store").`in`(it))
            }

            isResource?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("isResource"), it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun getAll(
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        dtoOptions: ProductDtoOptionsEnum,
        outOfMix: Boolean?,
        supplierId: Long?,
        supplierName: String?,
        name: String?,
        code: String?,
        isResource: Boolean?
    ): Any {
        val spec = createCriteria(outOfMix, supplierId, supplierName, name, code, isResource=isResource)
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = productRepository.findAll(spec, pageable)
                PageImpl(
                    pageResult.content.map { productModel ->
                        createDto(dtoOptions.fields, productModel)
                    },
                    pageable,
                    pageResult.totalElements
                )
            }

            else -> {
                productRepository.findAll(spec, sortBy).map { productModel ->
                    createDto(dtoOptions.fields, productModel)
                }
            }
        }
    }

    fun findById(id: Long): ProductModel {
        return productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found") }
    }

    fun findByCodeAndStore(code: String, store: StoresEnum): ProductModel {
        return productRepository.findByCodeAndStore(code, store)
            .orElseThrow { ResourceNotFoundException("Product not found") }
    }

    fun returnById(dtoOptions: ProductDtoOptionsEnum, id: Long, ): ProductDto {
        return createDto(dtoOptions.fields ,findById(id))
    }

    @Transactional
    fun createMultiple(dtoOptions: ProductDtoOptionsEnum, request: List<ProductCreateSchema>): Iterable<ProductDto> {
        val products = request.mapNotNull { product ->
            try {
                val supplier = supplierService.findByName(product.supplier)

                val newProduct = ProductModel(
                    code = product.code,
                    name = product.name,
                    supplier = supplier,
                    barcode = product.barcode,
                    store = StoresEnum.valueOf(product.store),
                    outOfMix = product.outOfMix,
                    weight = product.weight,
                    sector = product.sector,
                    groupName = product.group,
                    subgroup = product.subgroup,
                    packageQuantity = product.packageQuantity,
                    minimumStock = product.minimumStock,
                    salesLast30Days = product.salesLast30Days,
                    salesLast12Months = product.salesLast12Months,
                    salesLast7Days = product.salesLast7Days,
                    dailySales = product.dailySales,
                    lastCost = product.lastCost,
                    averageSalesLast30Days = product.averageSalesLast30Days,
                    currentStock = product.currentStock,
                    openOrder = product.openOrder,
                    expirationDate = product.expirationDate,
                    lossQuantity = product.lossQuantity,
                    promotionType = product.promotionType,
                    brand = product.brand,
                    exchangeQuantity = product.exchangeQuantity,
                    flag1 = product.flag1,
                    flag2 = product.flag2,
                    flag3 = product.flag3,
                    flag4 = product.flag4,
                    flag5 = product.flag5,
                    averageExpiration = product.averageExpiration,
                    networkStock = product.networkStock,
                    transferPackage = product.transferPackage,
                    promotionQuantity = product.promotionQuantity,
                    category = product.category,
                    noDeliveryQuantity = product.noDeliveryQuantity,
                    averageSales30d12m = product.averageSales30d12m,
                    highestSales = product.highestSales,
                    dailySalesAmount = product.dailySalesAmount,
                    daysToExpire = product.daysToExpire,
                    salesProjection = product.salesProjection,
                    inProjection = product.inProjection,
                    excessStock = product.excessStock,
                    totalCost = product.totalCost,
                    totalSales = product.totalSales,
                    term = product.term,
                    currentStockPerPackage = product.currentStockPerPackage,
                    averageSales = product.averageSales,
                    costP = product.costP,
                    salesP = product.salesP,
                    availableStock = product.availableStock,
                    stockTurnover = product.stockTurnover,
                    netCost = product.netCost,
                    salesPrice = product.salesPrice,
                    salesPrice2 = product.salesPrice2,
                    promotionPrice = product.promotionPrice,
                )
                val createdProduct = productRepository.save(newProduct)
                createdProduct

            } catch (e: ResourceNotFoundException) {
                null
            }
        }
        return products.map { createDto(dtoOptions.fields, it) }
    }

    @Transactional
    fun editOrCreateMultipleByCodeAndStore(dtoOptions: ProductDtoOptionsEnum, request: List<ProductCreateSchema>): Iterable<ProductDto> {
        val products = request.map { product ->
            try {
                val store = StoresEnum.valueOf(product.store)
                val existingProduct = findByCodeAndStore(product.code, store)

                val editedProduct = existingProduct.copy(
                    name = product.name,
                    store = store,
                    outOfMix = product.outOfMix,
                    weight = product.weight,
                    sector = product.sector,
                    groupName = product.group,
                    subgroup = product.subgroup,
                    packageQuantity = product.packageQuantity,
                    minimumStock = product.minimumStock,
                    salesLast30Days = product.salesLast30Days,
                    salesLast12Months = product.salesLast12Months,
                    salesLast7Days = product.salesLast7Days,
                    dailySales = product.dailySales,
                    lastCost = product.lastCost,
                    averageSalesLast30Days = product.averageSalesLast30Days,
                    currentStock = product.currentStock,
                    openOrder = product.openOrder,
                    expirationDate = product.expirationDate,
                    lossQuantity = product.lossQuantity,
                    promotionType = product.promotionType,
                    brand = product.brand,
                    exchangeQuantity = product.exchangeQuantity,
                    flag1 = product.flag1,
                    flag2 = product.flag2,
                    flag3 = product.flag3,
                    flag4 = product.flag4,
                    flag5 = product.flag5,
                    averageExpiration = product.averageExpiration,
                    networkStock = product.networkStock,
                    transferPackage = product.transferPackage,
                    promotionQuantity = product.promotionQuantity,
                    category = product.category,
                    noDeliveryQuantity = product.noDeliveryQuantity,
                    averageSales30d12m = product.averageSales30d12m,
                    highestSales = product.highestSales,
                    dailySalesAmount = product.dailySalesAmount,
                    daysToExpire = product.daysToExpire,
                    salesProjection = product.salesProjection,
                    inProjection = product.inProjection,
                    excessStock = product.excessStock,
                    totalCost = product.totalCost,
                    totalSales = product.totalSales,
                    term = product.term,
                    currentStockPerPackage = product.currentStockPerPackage,
                    averageSales = product.averageSales,
                    costP = product.costP,
                    salesP = product.salesP,
                    availableStock = product.availableStock,
                    stockTurnover = product.stockTurnover,
                    netCost = product.netCost,
                    salesPrice = product.salesPrice,
                    salesPrice2 = product.salesPrice2,
                    promotionPrice = product.promotionPrice,
                )
                productRepository.save(editedProduct)

            } catch (e: ResourceNotFoundException) {
                val supplier = supplierService.findByName(product.supplier)

                val newProduct = ProductModel(
                    code = product.code,
                    name = product.name,
                    supplier = supplier,
                    barcode = product.barcode,
                    store = StoresEnum.valueOf(product.store),
                    outOfMix = product.outOfMix,
                    weight = product.weight,
                    sector = product.sector,
                    groupName = product.group,
                    subgroup = product.subgroup,
                    packageQuantity = product.packageQuantity,
                    minimumStock = product.minimumStock,
                    salesLast30Days = product.salesLast30Days,
                    salesLast12Months = product.salesLast12Months,
                    salesLast7Days = product.salesLast7Days,
                    dailySales = product.dailySales,
                    lastCost = product.lastCost,
                    averageSalesLast30Days = product.averageSalesLast30Days,
                    currentStock = product.currentStock,
                    openOrder = product.openOrder,
                    expirationDate = product.expirationDate,
                    lossQuantity = product.lossQuantity,
                    promotionType = product.promotionType,
                    brand = product.brand,
                    exchangeQuantity = product.exchangeQuantity,
                    flag1 = product.flag1,
                    flag2 = product.flag2,
                    flag3 = product.flag3,
                    flag4 = product.flag4,
                    flag5 = product.flag5,
                    averageExpiration = product.averageExpiration,
                    networkStock = product.networkStock,
                    transferPackage = product.transferPackage,
                    promotionQuantity = product.promotionQuantity,
                    category = product.category,
                    noDeliveryQuantity = product.noDeliveryQuantity,
                    averageSales30d12m = product.averageSales30d12m,
                    highestSales = product.highestSales,
                    dailySalesAmount = product.dailySalesAmount,
                    daysToExpire = product.daysToExpire,
                    salesProjection = product.salesProjection,
                    inProjection = product.inProjection,
                    excessStock = product.excessStock,
                    totalCost = product.totalCost,
                    totalSales = product.totalSales,
                    term = product.term,
                    currentStockPerPackage = product.currentStockPerPackage,
                    averageSales = product.averageSales,
                    costP = product.costP,
                    salesP = product.salesP,
                    availableStock = product.availableStock,
                    stockTurnover = product.stockTurnover,
                    netCost = product.netCost,
                    salesPrice = product.salesPrice,
                    salesPrice2 = product.salesPrice2,
                    promotionPrice = product.promotionPrice,
                )
                val createdProduct = productRepository.save(newProduct)
                createdProduct
            }
        }
        return products.map { createDto(dtoOptions.fields, it) }
    }
}