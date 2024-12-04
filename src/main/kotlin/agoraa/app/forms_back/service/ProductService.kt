package agoraa.app.forms_back.service

import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.ProductModel
import agoraa.app.forms_back.repository.ProductRepository
import agoraa.app.forms_back.schema.product.ProductCreateSchema
import agoraa.app.forms_back.schema.product.ProductEditSchema
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val supplierService: SupplierService
) {

    fun findAll(
        outOfMix: String,
        supplierId: Long,
        supplierName: String,
        name: String,
        code: String,
        page: Int,
        size: Int,
        sort: String,
        direction: String
    ): Page<ProductModel> {
        val sortDirection = if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))

        val queryMap = mapOf(
            "outOfMix" to { productRepository.findByOutOfMix(outOfMix.toBoolean(), pageable) },
            "supplierId" to { productRepository.findBySupplierIdAndOutOfMixEquals(supplierId, outOfMix.toBoolean(), pageable) },
            "supplierName" to { productRepository.findBySupplierNameContainingAndOutOfMixEquals(supplierName, outOfMix.toBoolean(), pageable) },
            "name" to { productRepository.findByNameContainingAndOutOfMixEquals(name, outOfMix.toBoolean(), pageable) },
            "code" to { productRepository.findByCodeContainingAndOutOfMixEquals(code, outOfMix.toBoolean(), pageable) }
        )

        return when {
            outOfMix.isNotEmpty() && outOfMix.toBoolean() -> queryMap.entries.firstOrNull()?.value?.invoke()
                ?: productRepository.findByOutOfMix(outOfMix.toBoolean(), pageable)
            supplierId != 0L -> productRepository.findBySupplierId(supplierId, pageable)
            supplierName.isNotEmpty() -> productRepository.findBySupplierNameContaining(supplierName, pageable)
            name.isNotEmpty() -> productRepository.findByNameContaining(name, pageable)
            code.isNotEmpty() -> productRepository.findByCodeContaining(code, pageable)
            else -> productRepository.findAll(pageable)
        }
    }

    fun findById(id: Long): ProductModel {
        return productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found") }
    }

    fun findByCode(code: String): ProductModel {
        return productRepository.findByCode(code)
            .orElseThrow { ResourceNotFoundException("Product not found") }
    }

    @Transactional
    fun createBatch(request: List<ProductCreateSchema>): Iterable<ProductModel> {
        val products = request.map { product ->
            val supplier = supplierService.findByName(product.supplier)

            ProductModel(
                code = product.code,
                name = product.name,
                supplier = supplier,
                barcode = product.barcode,
                store = product.store,
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
        return productRepository.saveAll(products)
    }

    fun create(request: ProductCreateSchema): ProductModel {
        val supplier = supplierService.findByName(request.supplier)
        return productRepository.save(
            ProductModel(
                code = request.code,
                name = request.name,
                supplier = supplier,
                barcode = request.barcode,
                store = request.store,
                outOfMix = request.outOfMix,
                weight = request.weight,
                sector = request.sector,
                groupName = request.group,
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

    fun edit(id: Long, request: ProductEditSchema): ProductModel {
        val product = findById(id)
        val editedProduct = product.copy(
            name = request.name,
            outOfMix = request.outOfMix,
            weight = request.weight,
            sector = request.sector,
            groupName = request.group,
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
        return productRepository.save(editedProduct)
    }
}