package agoraa.app.forms_back.service

import agoraa.app.forms_back.dto.product.ProductDto
import agoraa.app.forms_back.enum.SectorsEnum
import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.exception.ResourceNotFoundException
import agoraa.app.forms_back.model.products.ProductModel
import agoraa.app.forms_back.model.suppliers.SupplierModel
import agoraa.app.forms_back.repository.ProductRepository
import agoraa.app.forms_back.schema.product.ProductCreateSchema
import agoraa.app.forms_back.schema.product.ProductEditOrCreateSchema
import agoraa.app.forms_back.schema.product.ProductEditSchema
import agoraa.app.forms_back.service.suppliers.SupplierService
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
import java.util.*

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val supplierService: SupplierService
) {

    fun createDto(productModel: ProductModel, full: Boolean = false): ProductDto {
        return ProductDto(
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
            salesLast30Days = productModel.salesLast30Days,
            salesLast12Months = productModel.salesLast12Months,
            salesLast7Days = productModel.salesLast7Days,
            dailySales = productModel.dailySales,
            lastCost = productModel.lastCost,
            averageSalesLast30Days = productModel.averageSalesLast30Days,
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
            averageSales30d12m = productModel.averageSales30d12m,
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
            supplier = productModel.supplier
        )
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

    fun getAll(
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
        sector: SectorsEnum?
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
                codes
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

    fun findByCodeAndStore(code: String, store: StoresEnum): Optional<ProductModel> {
        return productRepository.findByCodeAndStore(code, store)
    }

    fun returnById(id: Long): ProductDto {
        return createDto(findById(id), true)
    }

    @Transactional
    fun create(request: ProductCreateSchema) {
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
                salesLast30Days = request.salesLast30Days,
                salesLast12Months = request.salesLast12Months,
                salesLast7Days = request.salesLast7Days,
                dailySales = request.dailySales,
                lastCost = request.lastCost,
                averageSalesLast30Days = request.averageSalesLast30Days,
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
                averageSales30d12m = request.averageSales30d12m,
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
            )
        )
    }

    @Transactional
    fun edit(id: Long, request: ProductEditSchema) {
        val product = findById(id)

        val supplier = request.supplier?.let { supplier ->
            supplierService.findByName(supplier)
                .orElseThrow { ResourceNotFoundException("Supplier not found") }
        }

        productRepository.save(
            product.copy(
                code = request.code ?: product.code,
                name = request.name ?: product.name,
                supplier = supplier ?: product.supplier,
                barcode = request.barcode ?: product.barcode,
                outOfMix = request.outOfMix ?: product.outOfMix,
                weight = request.weight ?: product.weight,
                sector = request.sector ?: product.sector,
                groupName = request.groupName ?: product.groupName,
                subgroup = request.subgroup ?: product.subgroup,
                packageQuantity = request.packageQuantity ?: product.packageQuantity,
                minimumStock = request.minimumStock ?: product.minimumStock,
                salesLast30Days = request.salesLast30Days ?: product.salesLast30Days,
                salesLast12Months = request.salesLast12Months ?: product.salesLast12Months,
                salesLast7Days = request.salesLast7Days ?: product.salesLast7Days,
                dailySales = request.dailySales ?: product.dailySales,
                lastCost = request.lastCost ?: product.lastCost,
                averageSalesLast30Days = request.averageSalesLast30Days ?: product.averageSalesLast30Days,
                currentStock = request.currentStock ?: product.currentStock,
                openOrder = request.openOrder ?: product.openOrder,
                expirationDate = request.expirationDate ?: product.expirationDate,
                lossQuantity = request.lossQuantity ?: product.lossQuantity,
                promotionType = request.promotionType ?: product.promotionType,
                brand = request.brand ?: product.brand,
                exchangeQuantity = request.exchangeQuantity ?: product.exchangeQuantity,
                flag1 = request.flag1 ?: product.flag1,
                flag2 = request.flag2 ?: product.flag2,
                flag3 = request.flag3 ?: product.flag3,
                flag4 = request.flag4 ?: product.flag4,
                flag5 = request.flag5 ?: product.flag5,
                averageExpiration = request.averageExpiration ?: product.averageExpiration,
                networkStock = request.networkStock ?: product.networkStock,
                transferPackage = request.transferPackage ?: product.transferPackage,
                promotionQuantity = request.promotionQuantity ?: product.promotionQuantity,
                category = request.category ?: product.category,
                noDeliveryQuantity = request.noDeliveryQuantity ?: product.noDeliveryQuantity,
                averageSales30d12m = request.averageSales30d12m ?: product.averageSales30d12m,
                highestSales = request.highestSales ?: product.highestSales,
                dailySalesAmount = request.dailySalesAmount ?: product.dailySalesAmount,
                daysToExpire = request.daysToExpire ?: product.daysToExpire,
                salesProjection = request.salesProjection ?: product.salesProjection,
                inProjection = request.inProjection ?: product.inProjection,
                excessStock = request.excessStock ?: product.excessStock,
                totalCost = request.totalCost ?: product.totalCost,
                totalSales = request.totalSales ?: product.totalSales,
                term = request.term ?: product.term,
                currentStockPerPackage = request.currentStockPerPackage ?: product.currentStockPerPackage,
                averageSales = request.averageSales ?: product.averageSales,
                costP = request.costP ?: product.costP,
                salesP = request.salesP ?: product.salesP,
                availableStock = request.availableStock ?: product.availableStock,
                stockTurnover = request.stockTurnover ?: product.stockTurnover,
                netCost = request.netCost ?: product.netCost,
                salesPrice = request.salesPrice ?: product.salesPrice,
                salesPrice2 = request.salesPrice2 ?: product.salesPrice2,
                promotionPrice = request.promotionPrice ?: product.promotionPrice,
            )
        )
    }

    @Transactional
    fun editOrCreateMultiple(request: List<ProductEditOrCreateSchema>) {
        val products = request.map { p ->
            val product = findByCodeAndStore(p.code, p.store).orElse(null)
            val supplier = p.supplier?.let { supplier ->
                supplierService.findByName(supplier)
                    .orElseThrow { ResourceNotFoundException("Supplier not found") }
            }

            product?.copy(
                name = p.name ?: product.name,
                supplier = supplier ?: product.supplier,
                barcode = p.barcode ?: product.barcode,
                outOfMix = p.outOfMix ?: product.outOfMix,
                weight = p.weight ?: product.weight,
                sector = p.sector ?: product.sector,
                groupName = p.groupName ?: product.groupName,
                subgroup = p.subgroup ?: product.subgroup,
                packageQuantity = p.packageQuantity ?: product.packageQuantity,
                minimumStock = p.minimumStock ?: product.minimumStock,
                salesLast30Days = p.salesLast30Days ?: product.salesLast30Days,
                salesLast12Months = p.salesLast12Months ?: product.salesLast12Months,
                salesLast7Days = p.salesLast7Days ?: product.salesLast7Days,
                dailySales = p.dailySales ?: product.dailySales,
                lastCost = p.lastCost ?: product.lastCost,
                averageSalesLast30Days = p.averageSalesLast30Days ?: product.averageSalesLast30Days,
                currentStock = p.currentStock ?: product.currentStock,
                openOrder = p.openOrder ?: product.openOrder,
                expirationDate = p.expirationDate ?: product.expirationDate,
                lossQuantity = p.lossQuantity ?: product.lossQuantity,
                promotionType = p.promotionType ?: product.promotionType,
                brand = p.brand ?: product.brand,
                exchangeQuantity = p.exchangeQuantity ?: product.exchangeQuantity,
                flag1 = p.flag1 ?: product.flag1,
                flag2 = p.flag2 ?: product.flag2,
                flag3 = p.flag3 ?: product.flag3,
                flag4 = p.flag4 ?: product.flag4,
                flag5 = p.flag5 ?: product.flag5,
                averageExpiration = p.averageExpiration ?: product.averageExpiration,
                networkStock = p.networkStock ?: product.networkStock,
                transferPackage = p.transferPackage ?: product.transferPackage,
                promotionQuantity = p.promotionQuantity ?: product.promotionQuantity,
                category = p.category ?: product.category,
                noDeliveryQuantity = p.noDeliveryQuantity ?: product.noDeliveryQuantity,
                averageSales30d12m = p.averageSales30d12m ?: product.averageSales30d12m,
                highestSales = p.highestSales ?: product.highestSales,
                dailySalesAmount = p.dailySalesAmount ?: product.dailySalesAmount,
                daysToExpire = p.daysToExpire ?: product.daysToExpire,
                salesProjection = p.salesProjection ?: product.salesProjection,
                inProjection = p.inProjection ?: product.inProjection,
                excessStock = p.excessStock ?: product.excessStock,
                totalCost = p.totalCost ?: product.totalCost,
                totalSales = p.totalSales ?: product.totalSales,
                term = p.term ?: product.term,
                currentStockPerPackage = p.currentStockPerPackage ?: product.currentStockPerPackage,
                averageSales = p.averageSales ?: product.averageSales,
                costP = p.costP ?: product.costP,
                salesP = p.salesP ?: product.salesP,
                availableStock = p.availableStock ?: product.availableStock,
                stockTurnover = p.stockTurnover ?: product.stockTurnover,
                netCost = p.netCost ?: product.netCost,
                salesPrice = p.salesPrice ?: product.salesPrice,
                salesPrice2 = p.salesPrice2 ?: product.salesPrice2,
                promotionPrice = p.promotionPrice ?: product.promotionPrice,
            )
                ?: ProductModel(
                    code = p.code,
                    store = p.store,
                    name = p.name ?: throw IllegalArgumentException("name is required"),
                    supplier = supplier ?: throw IllegalArgumentException("supplier is required"),
                    barcode = p.barcode ?: throw IllegalArgumentException("barcode is required"),
                    outOfMix = p.outOfMix ?: throw IllegalArgumentException("outOfMix is required"),
                    weight = p.weight ?: throw IllegalArgumentException("weight is required"),
                    sector = p.sector ?: throw IllegalArgumentException("sector is required"),
                    groupName = p.groupName,
                    subgroup = p.subgroup,
                    packageQuantity = p.packageQuantity,
                    minimumStock = p.minimumStock ?: throw IllegalArgumentException("minimumStock is required"),
                    salesLast30Days = p.salesLast30Days
                        ?: throw IllegalArgumentException("salesLast30Days is required"),
                    salesLast12Months = p.salesLast12Months
                        ?: throw IllegalArgumentException("salesLast12Months is required"),
                    salesLast7Days = p.salesLast7Days
                        ?: throw IllegalArgumentException("salesLast7Days is required"),
                    dailySales = p.dailySales ?: throw IllegalArgumentException("dailySales is required"),
                    lastCost = p.lastCost ?: throw IllegalArgumentException("lastCost is required"),
                    averageSalesLast30Days = p.averageSalesLast30Days
                        ?: throw IllegalArgumentException("averageSalesLast30Days is required"),
                    currentStock = p.currentStock,
                    openOrder = p.openOrder ?: throw IllegalArgumentException("openOrder is required"),
                    expirationDate = p.expirationDate,
                    lossQuantity = p.lossQuantity ?: throw IllegalArgumentException("lossQuantity is required"),
                    promotionType = p.promotionType,
                    brand = p.brand,
                    exchangeQuantity = p.exchangeQuantity
                        ?: throw IllegalArgumentException("exchangeQuantity is required"),
                    flag1 = p.flag1,
                    flag2 = p.flag2,
                    flag3 = p.flag3,
                    flag4 = p.flag4,
                    flag5 = p.flag5,
                    averageExpiration = p.averageExpiration
                        ?: throw IllegalArgumentException("averageExpiration is required"),
                    networkStock = p.networkStock ?: throw IllegalArgumentException("networkStock is required"),
                    transferPackage = p.transferPackage
                        ?: throw IllegalArgumentException("transferPackage is required"),
                    promotionQuantity = p.promotionQuantity
                        ?: throw IllegalArgumentException("promotionQuantity is required"),
                    category = p.category,
                    noDeliveryQuantity = p.noDeliveryQuantity
                        ?: throw IllegalArgumentException("noDeliveryQuantity is required"),
                    averageSales30d12m = p.averageSales30d12m
                        ?: throw IllegalArgumentException("averageSales30d12m is required"),
                    highestSales = p.highestSales,
                    dailySalesAmount = p.dailySalesAmount
                        ?: throw IllegalArgumentException("dailySalesAmount is required"),
                    daysToExpire = p.daysToExpire ?: throw IllegalArgumentException("daysToExpire is required"),
                    salesProjection = p.salesProjection
                        ?: throw IllegalArgumentException("salesProjection is required"),
                    inProjection = p.inProjection ?: throw IllegalArgumentException("inProjection is required"),
                    excessStock = p.excessStock ?: throw IllegalArgumentException("excessStock is required"),
                    totalCost = p.totalCost,
                    totalSales = p.totalSales,
                    term = p.term ?: throw IllegalArgumentException("term is required"),
                    currentStockPerPackage = p.currentStockPerPackage
                        ?: throw IllegalArgumentException("currentStockPerPackage is required"),
                    averageSales = p.averageSales ?: throw IllegalArgumentException("averageSales is required"),
                    costP = p.costP,
                    salesP = p.salesP,
                    availableStock = p.availableStock
                        ?: throw IllegalArgumentException("availableStock is required"),
                    stockTurnover = p.stockTurnover,
                    netCost = p.netCost,
                    salesPrice = p.salesPrice,
                    salesPrice2 = p.salesPrice2,
                    promotionPrice = p.promotionPrice,
                )
        }
        productRepository.saveAll(products)
    }
}