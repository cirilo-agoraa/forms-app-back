package agoraa.app.forms_back.ruptures.service

import agoraa.app.forms_back.ruptures.model.RupturaModel
import agoraa.app.forms_back.ruptures.repository.RupturaRepository
import org.springframework.stereotype.Service
import agoraa.app.forms_back.ruptures.dto.RupturaWithProductResponse
import agoraa.app.forms_back.products.products.repository.ProductRepository
import agoraa.app.forms_back.products.products.service.ProductService
import agoraa.app.forms_back.shared.service.ChatsacService
import agoraa.app.forms_back.shared.enums.StoresEnum
import agoraa.app.forms_back.orders.orders.service.OrderService
import agoraa.app.forms_back.shared.enums.BuyersEnum
import java.time.format.DateTimeFormatter


@Service
class RupturaService(
    private val repository: RupturaRepository, 
    private val productRepository: ProductRepository,
    private val chatsacService: ChatsacService,
    private val productService: ProductService,
    private val orderService: OrderService

) {
    fun create(ruptura: RupturaModel): RupturaModel {
        val saved = repository.save(ruptura)
        val product = productRepository.findById(ruptura.productId).orElse(null)
        val relatedProducts = productService.getAllProductsByCode(product?.code ?: "")
            .filter { it.store == StoresEnum.TRESMANN_SMJ || it.store == StoresEnum.TRESMANN_STT }   

        val resumoEstoques = relatedProducts.joinToString("\n") { 
            "• ${it.store}: ${it.availableStock}" 
        }
        val fornecedorName = product?.supplier?.name

        val orders = orderService.getAllBySupplier(product.supplier.id)
                .filter { 
                    it.issued == true && 
                    it.received == false && 
                    it.buyer != BuyersEnum.RECEBIMENTO
                }

        println("Orders: $orders")
        val ordersInfo = if (orders.isNotEmpty()) {
            orders.joinToString("\n") {
                "• Pedido ${it.orderNumber} - Entrega: ${it.deliveryDate}"
            }
        } else {
            "Nenhum pedido emitido e não recebido para este fornecedor."
        }

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val msg = buildString {
            appendLine("Ruptura registrada:")
            product?.let {
                appendLine("* Produto: ${it.name} (código: ${it.code})")
                appendLine("* Fornecedor: ${fornecedorName ?: "Não informado"}")
                appendLine()
                appendLine("Estoques:")
                appendLine("* Estoque Rede: ${it.networkStock}")
                relatedProducts.forEach { prod ->
                    appendLine("* ${prod.store}: ${prod.availableStock}")
                }
                appendLine()
                appendLine("Pd em Aberto")
                appendLine("- Qtde Pd Aberto: ${it.openOrder}")
                if (orders.isNotEmpty()) {
                    orders.forEach { order ->
                        val dataEntrega = order.deliveryDate?.format(dateFormatter) ?: "-"
                        appendLine("* Pedido ${order.orderNumber} - Entrega: $dataEntrega")
                    }
                } else {
                    appendLine("* Nenhum pedido emitido e não recebido para este fornecedor.")
                }
            } ?: appendLine("Produto não encontrado para o ID: ${ruptura.productId}")
        }

        chatsacService.sendMsg(msg, "663a53e93b0a671bbcb23c93").subscribe()
        return saved
    }  
    
    fun getAll(): List<RupturaWithProductResponse> =
        repository.findAll().map { ruptura ->
            RupturaWithProductResponse(
                id = ruptura.id,
                createdAt = ruptura.createdAt,
                product = productRepository.findById(ruptura.productId).orElse(null) // injete o productRepository no service
            )
     }   
    fun getById(id: Long): RupturaModel? = repository.findById(id).orElse(null)
    fun delete(id: Long) = repository.deleteById(id)
}