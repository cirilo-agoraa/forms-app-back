package agoraa.app.forms_back.service

import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.ProductModel
import agoraa.app.forms_back.model.SupplierModel
import agoraa.app.forms_back.repository.ProductRepository
import agoraa.app.forms_back.schema.product.ProductCreateSchema
import agoraa.app.forms_back.schema.product.ProductDTO
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

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val supplierService: SupplierService
) {

    private fun createCriteria(
        outOfMix: Boolean?,
        supplierId: Long?,
        supplierName: String?,
        name: String?,
        code: String?,
        store: List<StoresEnum>?,
    ): Specification<ProductModel> {
        return Specification { root: Root<ProductModel>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
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

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun createProductDTO(product: ProductModel): ProductDTO{
        return ProductDTO(
            id = product.id,
            code = product.code,
            name = product.name,
            supplier = product.supplier.name,
            barcode = product.barcode,
            store = product.store,
            outOfMix = product.outOfMix,
            weight = product.weight,
            sector = product.sector,
            groupName = product.groupName,
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
    }

    fun findAll(
        pagination: Boolean,
        convertToDTO: Boolean,
        outOfMix: Boolean?,
        supplierId: Long?,
        supplierName: String?,
        name: String?,
        code: String?,
        store: List<StoresEnum>?,
        page: Int,
        size: Int,
        sort: String,
        direction: String
    ): Any {
        val spec = createCriteria(outOfMix, supplierId, supplierName, name, code, store)

        return if (pagination) {
            val sortDirection =
                if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
            val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))

            val productPage = productRepository.findAll(spec, pageable)

            if (convertToDTO){
                val productDTOs = productPage.content.map { createProductDTO(it) }
                PageImpl(productDTOs, pageable, productPage.totalElements)
            } else {
                productPage
            }

        } else {
            val products = productRepository.findAll(spec)
            if (convertToDTO){
                products.map { createProductDTO(it) }
            } else {
                products
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

    fun returnById(id: Long): ProductDTO{
        val product = findById(id)
        return createProductDTO(product)
    }

    @Transactional
    fun createMultiple(request: List<ProductCreateSchema>): Iterable<ProductDTO> {
        val products = request.mapNotNull { product ->
            try {
                val supplier = supplierService.findByName(product.supplier)

                ProductModel(
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
            } catch (e: ResourceNotFoundException) {
                null
            }
        }
        productRepository.saveAll(products)

        return products.map { createProductDTO(it) }
    }

    @Transactional
    fun editOrCreateMultipleByCodeAndStore(request: List<ProductCreateSchema>): Iterable<ProductDTO> {
        val products = request.map { product ->
            try {
                val store = StoresEnum.valueOf(product.store)
                val existingProduct = findByCodeAndStore(product.code, store)
                existingProduct.copy(
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
            } catch (e: ResourceNotFoundException) {
                val supplier = supplierService.findByName(product.supplier)
                ProductModel(
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
            }
        }
        productRepository.saveAll(products)

        return products.map { createProductDTO(it) }
    }
}