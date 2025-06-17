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
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.util.Date
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DateUtil
import java.text.SimpleDateFormat
import java.util.Locale

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
        print(product)
        print(product?.supplier?.name)
        // return ruptura
        val relatedProducts = productService.getAllProductsByCode(product?.code ?: "")
            .filter { it.store == StoresEnum.TRESMANN_SMJ || it.store == StoresEnum.TRESMANN_STT }   

        val lojaSelecionada = ruptura.store
        val estoqueLoja = relatedProducts.find { it.store == lojaSelecionada }

        val resumoEstoques = if (estoqueLoja != null) {
            "* ${estoqueLoja.store}: ${estoqueLoja.availableStock}"
        } else {
            "* Estoque não encontrado para a loja ${lojaSelecionada.name}"
        }
        println("Resumo Estoques: $resumoEstoques")
        val fornecedorName = product?.supplier?.name
        val lojaNumero = lojaEnumToNumero(lojaSelecionada)
        println("Fornecedor: $fornecedorName, Loja Número: $lojaNumero")
        val orders = getOrdersFromExcel(fornecedorName ?:"", lojaNumero)
            .filter { 
                it.emitido == "True" && 
                it.recebido == "False" 
                && 
                it.nomeDoComprador != BuyersEnum.RECEBIMENTO.name &&
                it.loja == lojaNumero
        }

        println("Orders: $orders")
        // return ruptura
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val msg = buildString {
            appendLine("Ruptura registrada:")
            product?.let {
                appendLine("* Produto: ${it.name} (código: ${it.code})")
                appendLine("* Loja: ${ruptura.store ?: "Não informada"}")
                appendLine("* Fornecedor: ${fornecedorName ?: "Não informado"}")
                appendLine()
                appendLine("Estoques:")
                appendLine("* Estoque Rede: ${product.networkStock}")
                relatedProducts.forEach { prod ->
                    appendLine("* ${prod.store}: ${prod.availableStock}")
                }               
                appendLine()
                appendLine("Pd em Aberto")
                appendLine("- Qtde Pd Aberto: ${it.openOrder}")
                if (orders.isNotEmpty()) {
                    orders.forEach { order ->
                        // val dataEntrega = order.dataEntrega?.format(dateFormatter) ?: "-"
                        val formatoBR = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                        val dataEntregaFormatada = if (order.dataEntrega is java.util.Date) {
                            formatoBR.format(order.dataEntrega)
                        } else {
                            order.dataEntrega?.toString() ?: ""
}
                        appendLine("* Pedido ${order.pedido} - Entrega: $dataEntregaFormatada")
                        // appendLine("* Pedido ${order.pedido}")

                    }
                } else {
                    appendLine("* Nenhum pedido emitido e não recebido para este fornecedor.")
                }
            } ?: appendLine("Produto não encontrado para o ID: ${ruptura.productId}")
        }

        chatsacService.sendMsg(msg, "663a53e93b0a671bbcb23c93").subscribe()
        println("Mensagem enviada: $msg")
        return saved
    }  
    
    fun getAll(): List<RupturaWithProductResponse> =
        repository.findAll()
            .sortedByDescending { it.createdAt }
            .map { ruptura ->
                RupturaWithProductResponse(
                    id = ruptura.id,
                    createdAt = ruptura.createdAt,
                    product = productRepository.findById(ruptura.productId).orElse(null),
                    store = ruptura.store
                )
            }

    fun getById(id: Long): RupturaModel? = repository.findById(id).orElse(null)
    fun delete(id: Long) = repository.deleteById(id)

    data class OrderExcel(
        val loja: String,
        val pedido: String,
        val data: Date?,
        val dataEntrega: Date?,
        val fornecedor: String,
        val valorTotal: Double,
        val valorPendente: Double,
        val valorRecebido: Double,
        val emitido: String,
        val recebido: String,
        val origSaldoEntrada: String,
        val email: String,
        val nomeDaLoja: String,
        val chavePedidoOriginal: String,
        val nomeDoComprador: String,
        val tabelaDePrecos: String,
        val tipoPedido: String,
        val pedidoReplicado: String,
        val dataRecebimento: String
    )

    fun getOrdersFromExcel(supplierName: String, lojaNumero: String): List<OrderExcel> {
        val excelPath = "F:\\BI\\Bases\\historico de pedidos _oficial.xlsx"
        val file = File(excelPath)
        val orders = mutableListOf<OrderExcel>()
        val workbook = WorkbookFactory.create(file)
        val sheet = workbook.getSheetAt(0)
        println("supplierName: ${supplierName}")
        println("Loja Número: ${lojaNumero}")


        for (row in sheet.drop(1)) {
            val fornecedor = row.getCell(4)?.toString()?.trim()?.uppercase() ?: ""
            val loja = row.getCell(0)?.toString()?.replace(".0", "")?.trim() ?: ""

            if (fornecedor == supplierName.trim().uppercase() && loja == lojaNumero.trim()) {
                val order = OrderExcel(
                    loja = loja,
                    pedido = row.getCell(1)?.toString() ?: "",
                    data = try { row.getCell(2)?.dateCellValue } catch (e: Exception) { null },
                    dataEntrega = try { row.getCell(3)?.dateCellValue } catch (e: Exception) { null },
                    fornecedor = fornecedor,
                    valorTotal = try { row.getCell(5)?.numericCellValue ?: 0.0 } catch (e: Exception) { 0.0 },
                    valorPendente = try { row.getCell(6)?.numericCellValue ?: 0.0 } catch (e: Exception) { 0.0 },
                    valorRecebido = try { row.getCell(7)?.numericCellValue ?: 0.0 } catch (e: Exception) { 0.0 },
                    emitido = row.getCell(8)?.toString() ?: "",
                    recebido = row.getCell(9)?.toString() ?: "",
                    origSaldoEntrada = row.getCell(10)?.toString() ?: "",
                    email = row.getCell(11)?.toString() ?: "",
                    nomeDaLoja = row.getCell(12)?.toString() ?: "",
                    chavePedidoOriginal = row.getCell(13)?.toString() ?: "",
                    nomeDoComprador = row.getCell(14)?.toString() ?: "",
                    tabelaDePrecos = row.getCell(15)?.toString() ?: "",
                    tipoPedido = row.getCell(16)?.toString() ?: "",
                    pedidoReplicado = row.getCell(17)?.toString() ?: "",
                    dataRecebimento = row.getCell(18)?.toString() ?: ""
                )
                orders.add(order)
            }
        }
        workbook.close()
        return orders
    }

    fun lojaEnumToNumero(loja: StoresEnum): String = when (loja) {
        StoresEnum.TRESMANN_SMJ -> "1"
        StoresEnum.TRESMANN_STT -> "2"
        StoresEnum.TRESMANN_VIX -> "3"
        StoresEnum.MERCAPP -> "4"
        else -> ""
    }
}