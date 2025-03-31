package agoraa.app.forms_back.service

import agoraa.app.forms_back.dto.product.ProductDto
import agoraa.app.forms_back.enums.MipsCategoriesEnum
import agoraa.app.forms_back.enums.SectorsEnum
import agoraa.app.forms_back.enums.StoresEnum
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.products.ProductModel
import agoraa.app.forms_back.repository.ProductRepository
import agoraa.app.forms_back.schema.product.ProductSchema
import agoraa.app.forms_back.suppliers.suppliers.model.SupplierModel
import agoraa.app.forms_back.suppliers.suppliers.service.SupplierService
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

    fun createDto(productModel: ProductModel, full: Boolean = false): ProductDto {
        val productDto = ProductDto(
            id = productModel.id,
            code = productModel.code,
            name = productModel.name,
            barcode = productModel.barcode,
            store = productModel.store,
            outOfMix = productModel.outOfMix,
            sector = productModel.sector,
            groupName = productModel.groupName,
            subgroup = productModel.subgroup,
            brand = productModel.brand,
            packageQuantity = productModel.packageQuantity,
            category = productModel.category,
            transferPackage = productModel.transferPackage,
            isResource = productModel.isResource,
            weight = productModel.weight,
            minimumStock = productModel.minimumStock,
            salesLastThirtyDays = productModel.salesLastThirtyDays,
            salesLastTwelveMonths = productModel.salesLastTwelveMonths,
            salesLastSevenDays = productModel.salesLastSevenDays,
            dailySales = productModel.dailySales,
            lastCost = productModel.lastCost,
            averageSalesLastThirtyDays = productModel.averageSalesLastThirtyDays,
            currentStock = productModel.currentStock,
            openOrder = productModel.openOrder,
            expirationDate = productModel.expirationDate,
            lossQuantity = productModel.lossQuantity,
            promotionType = productModel.promotionType,
            flag1 = productModel.flag1,
            flag2 = productModel.flag2,
            flag3 = productModel.flag3,
            flag4 = productModel.flag4,
            flag5 = productModel.flag5,
            averageExpiration = productModel.averageExpiration,
            networkStock = productModel.networkStock,
            promotionQuantity = productModel.promotionQuantity,
            noDeliveryQuantity = productModel.noDeliveryQuantity,
            averageSalesLastThirtyDaysTwelveMonths = productModel.averageSalesLastThirtyDaysTwelveMonths,
            highestSales = productModel.highestSales,
            dailySalesAmount = productModel.dailySalesAmount,
            daysToExpire = productModel.daysToExpire,
            salesProjection = productModel.salesProjection,
            inProjection = productModel.inProjection,
            excessStock = productModel.excessStock,
            totalCost = productModel.totalCost,
            totalSales = productModel.totalSales,
            term = productModel.term,
            currentStockPerPackage = productModel.currentStockPerPackage,
            averageSales = productModel.averageSales,
            costP = productModel.costP,
            salesP = productModel.salesP,
            availableStock = productModel.availableStock,
            stockTurnover = productModel.stockTurnover,
            netCost = productModel.netCost,
            salesPrice = productModel.salesPrice,
            salesPrice2 = productModel.salesPrice2,
            promotionPrice = productModel.promotionPrice,
            exchangeQuantity = productModel.exchangeQuantity,
            mipCategory = productModel.mipCategory,
            supplier = productModel.supplier,
        )

        return productDto
    }

    private fun createCriteria(
        outOfMix: Boolean? = null,
        supplierId: Long? = null,
        supplierName: String? = null,
        name: String? = null,
        code: String? = null,
        stores: List<StoresEnum>? = null,
        isResource: Boolean? = null,
        sector: SectorsEnum? = null,
        codes: List<String>? = null,
        mipCategories: List<MipsCategoriesEnum>? = null,
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

            stores?.let {
                predicates.add(root.get<StoresEnum>("store").`in`(it))
            }

            mipCategories?.let {
                predicates.add(root.get<MipsCategoriesEnum>("mipCategory").`in`(it))
            }

            isResource?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("isResource"), it))
            }

            sector?.let {
                predicates.add(criteriaBuilder.equal(root.get<SectorsEnum>("sector"), it))
            }

            codes?.let {
                predicates.add(root.get<String>("code").`in`(it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun findAll(
        codes: List<String>? = null
    ): List<ProductModel> {
        val spec = createCriteria(codes = codes)
        return productRepository.findAll(spec)
    }

    fun getAll(
        full: Boolean,
        pagination: Boolean,
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        outOfMix: Boolean?,
        supplierId: Long?,
        supplierName: String?,
        name: String?,
        code: String?,
        codes: List<String>?,
        isResource: Boolean?,
        stores: List<StoresEnum>?,
        sector: SectorsEnum?,
        mipCategories: List<MipsCategoriesEnum>?
    ): Any {
        val spec =
            createCriteria(
                outOfMix,
                supplierId,
                supplierName,
                name,
                code,
                stores,
                isResource = isResource,
                sector,
                codes,
                mipCategories
            )
        val sortDirection =
            if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val sortBy = Sort.by(sortDirection, sort)

        return when {
            pagination -> {
                val pageable = PageRequest.of(page, size, sortBy)
                val pageResult = productRepository.findAll(spec, pageable)
                PageImpl(
                    pageResult.content.map { productModel ->
                        createDto(productModel)
                    },
                    pageable,
                    pageResult.totalElements
                )
            }

            else -> {
                productRepository.findAll(spec, sortBy).map { productModel ->
                    createDto(productModel)
                }
            }
        }
    }

    fun findById(id: Long): ProductModel {
        return productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found") }
    }

    fun returnById(id: Long): ProductDto {
        return createDto(findById(id), true)
    }

    @Transactional
    fun create(request: ProductSchema) {
        val supplier = supplierService.findByName(request.supplier)
            .orElseThrow { ResourceNotFoundException("Supplier not found") }

        productRepository.save(
            ProductModel(
                code = request.code,
                name = request.name,
                supplier = supplier,
                barcode = request.barcode,
                store = request.store,
                outOfMix = request.outOfMix,
                weight = request.weight,
                sector = request.sector,
                groupName = request.groupName,
                subgroup = request.subgroup,
                packageQuantity = request.packageQuantity,
                minimumStock = request.minimumStock,
                salesLastThirtyDays = request.salesLastThirtyDays,
                salesLastTwelveMonths = request.salesLastTwelveMonths,
                salesLastSevenDays = request.salesLastSevenDays,
                dailySales = request.dailySales,
                lastCost = request.lastCost,
                averageSalesLastThirtyDays = request.averageSalesLastThirtyDays,
                currentStock = request.currentStock,
                openOrder = request.openOrder,
                expirationDate = request.expirationDate,
                lossQuantity = request.lossQuantity,
                promotionType = request.promotionType,
                brand = request.brand,
                exchangeQuantity = request.exchangeQuantity,
                flag1 = request.flag1,
                flag2 = request.flag2,
                flag3 = request.flag3,
                flag4 = request.flag4,
                flag5 = request.flag5,
                averageExpiration = request.averageExpiration,
                networkStock = request.networkStock,
                transferPackage = request.transferPackage,
                promotionQuantity = request.promotionQuantity,
                category = request.category,
                noDeliveryQuantity = request.noDeliveryQuantity,
                averageSalesLastThirtyDaysTwelveMonths = request.averageSalesThirtyDaysTwelveMonths,
                highestSales = request.highestSales,
                dailySalesAmount = request.dailySalesAmount,
                daysToExpire = request.daysToExpire,
                salesProjection = request.salesProjection,
                inProjection = request.inProjection,
                excessStock = request.excessStock,
                totalCost = request.totalCost,
                totalSales = request.totalSales,
                term = request.term,
                currentStockPerPackage = request.currentStockPerPackage,
                averageSales = request.averageSales,
                costP = request.costP,
                salesP = request.salesP,
                availableStock = request.availableStock,
                stockTurnover = request.stockTurnover,
                netCost = request.netCost,
                salesPrice = request.salesPrice,
                salesPrice2 = request.salesPrice2,
                promotionPrice = request.promotionPrice,
                mipCategory = request.mipCategory,
            )
        )
    }

    @Transactional
    fun edit(id: Long, request: ProductSchema) {
        val product = findById(id)

        val supplier = supplierService.findByName(request.supplier)
            .orElseThrow { ResourceNotFoundException("Supplier not found") }

        productRepository.save(
            product.copy(
                code = request.code,
                name = request.name,
                supplier = supplier,
                barcode = request.barcode,
                store = request.store,
                outOfMix = request.outOfMix,
                weight = request.weight,
                sector = request.sector,
                groupName = request.groupName,
                subgroup = request.subgroup,
                packageQuantity = request.packageQuantity,
                minimumStock = request.minimumStock,
                salesLastThirtyDays = request.salesLastThirtyDays,
                salesLastTwelveMonths = request.salesLastTwelveMonths,
                salesLastSevenDays = request.salesLastSevenDays,
                dailySales = request.dailySales,
                lastCost = request.lastCost,
                averageSalesLastThirtyDays = request.averageSalesLastThirtyDays,
                currentStock = request.currentStock,
                openOrder = request.openOrder,
                expirationDate = request.expirationDate,
                lossQuantity = request.lossQuantity,
                promotionType = request.promotionType,
                brand = request.brand,
                exchangeQuantity = request.exchangeQuantity,
                flag1 = request.flag1,
                flag2 = request.flag2,
                flag3 = request.flag3,
                flag4 = request.flag4,
                flag5 = request.flag5,
                averageExpiration = request.averageExpiration,
                networkStock = request.networkStock,
                transferPackage = request.transferPackage,
                promotionQuantity = request.promotionQuantity,
                category = request.category,
                noDeliveryQuantity = request.noDeliveryQuantity,
                averageSalesLastThirtyDaysTwelveMonths = request.averageSalesThirtyDaysTwelveMonths,
                highestSales = request.highestSales,
                dailySalesAmount = request.dailySalesAmount,
                daysToExpire = request.daysToExpire,
                salesProjection = request.salesProjection,
                inProjection = request.inProjection,
                excessStock = request.excessStock,
                totalCost = request.totalCost,
                totalSales = request.totalSales,
                term = request.term,
                currentStockPerPackage = request.currentStockPerPackage,
                averageSales = request.averageSales,
                costP = request.costP,
                salesP = request.salesP,
                availableStock = request.availableStock,
                stockTurnover = request.stockTurnover,
                netCost = request.netCost,
                salesPrice = request.salesPrice,
                salesPrice2 = request.salesPrice2,
                promotionPrice = request.promotionPrice,
                mipCategory = request.mipCategory,
            )
        )
    }

    @Transactional
    fun editOrCreateMultiple(request: List<ProductSchema>) {
        val suppliersNames = request.map { it.supplier }.distinct()
        val suppliers = supplierService.getAll(suppliersNames)

        if (suppliersNames.size != suppliers.size) throw IllegalArgumentException("One or more suppliers not found")

        val supplierMap = suppliers.associateBy { it.name }
        val spec = createCriteria(codes = request.map { it.code }.distinct(), stores = request.map { it.store }.distinct())
        val products = productRepository.findAll(spec)
        val productMap = products.associateBy { it.code to it.store }

        val resultProducts = request.map { p ->
            val supp = supplierMap[p.supplier] ?: throw IllegalArgumentException("Supplier not Found")
            val existingProduct = productMap[p.code to p.store]

            existingProduct?.copy(
                code = p.code,
                store = p.store,
                name = p.name,
                supplier = supp,
                barcode = p.barcode,
                outOfMix = p.outOfMix,
                weight = p.weight,
                sector = p.sector,
                groupName = p.groupName,
                subgroup = p.subgroup,
                packageQuantity = p.packageQuantity,
                minimumStock = p.minimumStock,
                salesLastThirtyDays = p.salesLastThirtyDays,
                salesLastTwelveMonths = p.salesLastTwelveMonths,
                salesLastSevenDays = p.salesLastSevenDays,
                dailySales = p.dailySales,
                lastCost = p.lastCost,
                averageSalesLastThirtyDays = p.averageSalesLastThirtyDays,
                currentStock = p.currentStock,
                openOrder = p.openOrder,
                expirationDate = p.expirationDate,
                lossQuantity = p.lossQuantity,
                promotionType = p.promotionType,
                brand = p.brand,
                exchangeQuantity = p.exchangeQuantity,
                flag1 = p.flag1,
                flag2 = p.flag2,
                flag3 = p.flag3,
                flag4 = p.flag4,
                flag5 = p.flag5,
                averageExpiration = p.averageExpiration,
                networkStock = p.networkStock,
                transferPackage = p.transferPackage,
                promotionQuantity = p.promotionQuantity,
                category = p.category,
                noDeliveryQuantity = p.noDeliveryQuantity,
                averageSalesLastThirtyDaysTwelveMonths = p.averageSalesThirtyDaysTwelveMonths,
                highestSales = p.highestSales,
                dailySalesAmount = p.dailySalesAmount,
                daysToExpire = p.daysToExpire,
                salesProjection = p.salesProjection,
                inProjection = p.inProjection,
                excessStock = p.excessStock,
                totalCost = p.totalCost,
                totalSales = p.totalSales,
                term = p.term,
                currentStockPerPackage = p.currentStockPerPackage,
                averageSales = p.averageSales,
                costP = p.costP,
                salesP = p.salesP,
                availableStock = p.availableStock,
                stockTurnover = p.stockTurnover,
                netCost = p.netCost,
                salesPrice = p.salesPrice,
                salesPrice2 = p.salesPrice2,
                promotionPrice = p.promotionPrice,
            )
                ?: ProductModel(
                    code = p.code,
                    store = p.store,
                    name = p.name,
                    supplier = supp,
                    barcode = p.barcode,
                    outOfMix = p.outOfMix,
                    weight = p.weight,
                    sector = p.sector,
                    groupName = p.groupName,
                    subgroup = p.subgroup,
                    packageQuantity = p.packageQuantity,
                    minimumStock = p.minimumStock,
                    salesLastThirtyDays = p.salesLastThirtyDays,
                    salesLastTwelveMonths = p.salesLastTwelveMonths,
                    salesLastSevenDays = p.salesLastSevenDays,
                    dailySales = p.dailySales,
                    lastCost = p.lastCost,
                    averageSalesLastThirtyDays = p.averageSalesLastThirtyDays,
                    currentStock = p.currentStock,
                    openOrder = p.openOrder,
                    expirationDate = p.expirationDate,
                    lossQuantity = p.lossQuantity,
                    promotionType = p.promotionType,
                    brand = p.brand,
                    exchangeQuantity = p.exchangeQuantity,
                    flag1 = p.flag1,
                    flag2 = p.flag2,
                    flag3 = p.flag3,
                    flag4 = p.flag4,
                    flag5 = p.flag5,
                    averageExpiration = p.averageExpiration,
                    networkStock = p.networkStock,
                    transferPackage = p.transferPackage,
                    promotionQuantity = p.promotionQuantity,
                    category = p.category,
                    noDeliveryQuantity = p.noDeliveryQuantity,
                    averageSalesLastThirtyDaysTwelveMonths = p.averageSalesThirtyDaysTwelveMonths,
                    highestSales = p.highestSales,
                    dailySalesAmount = p.dailySalesAmount,
                    daysToExpire = p.daysToExpire,
                    salesProjection = p.salesProjection,
                    inProjection = p.inProjection,
                    excessStock = p.excessStock,
                    totalCost = p.totalCost,
                    totalSales = p.totalSales,
                    term = p.term,
                    currentStockPerPackage = p.currentStockPerPackage,
                    averageSales = p.averageSales,
                    costP = p.costP,
                    salesP = p.salesP,
                    availableStock = p.availableStock,
                    stockTurnover = p.stockTurnover,
                    netCost = p.netCost,
                    salesPrice = p.salesPrice,
                    salesPrice2 = p.salesPrice2,
                    promotionPrice = p.promotionPrice,
                )
        }
        productRepository.saveAll(resultProducts)
    }
}