package agoraa.app.forms_back.schema.supplier

import agoraa.app.forms_back.enum.OrderDaysEnum
import agoraa.app.forms_back.enum.StoresEnum
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SupplierStoresEditSchema(
    @field:NotNull(message = "Store cannot be null")
    val store: StoresEnum,

    val orderDay: Optional<OrderDaysEnum>? = Optional.empty(),
    val frequency: Optional<Int>? = Optional.empty(),
    val stock: Optional<Float>? = null,
    val exchangeStock: Optional<Float>? = null,
    val openOrder: Optional<Int>? = Optional.empty(),
    val orderTerm: Optional<Int>? = Optional.empty(),
    val orderMeanDeliveryTime: Optional<Float>? = null,
    val nextOrder: Optional<LocalDateTime>? = Optional.empty(),
    val openOrderExpectedDelivery: Optional<LocalDateTime>? = Optional.empty(),
    val openOrderRealDelivery: Optional<LocalDateTime>? = Optional.empty(),
)
