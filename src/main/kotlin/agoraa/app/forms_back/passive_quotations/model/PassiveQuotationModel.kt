package agoraa.app.forms_back.passive_quotations.model

import agoraa.app.forms_back.enum.PaymentTermsEnum
import agoraa.app.forms_back.enum.StoresEnum
import agoraa.app.forms_back.enum.WppGroupsEnum
import agoraa.app.forms_back.model.UserModel
import agoraa.app.forms_back.model.suppliers.SupplierModel
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "passive-quotations")
data class PassiveQuotationModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserModel,

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    val supplier: SupplierModel,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val store: StoresEnum,

    @Column(nullable = false)
    val wppGroup: WppGroupsEnum,

    @Column(nullable = false)
    val paymentTerm: PaymentTermsEnum,

    @Column(nullable = false)
    val worstTerm: Int = 60,

    @Column(nullable = false)
    val bestTerm: Int = 60,

    @Column(nullable = false)
    val storesQuantity: Int = 1,

    @Column(nullable = false)
    val variation: Double = 0.03,

    @Column(nullable = false)
    val param1: Double = 0.5,

    @Column(nullable = false)
    val param2: Double = 1.0,

    @Column(nullable = false)
    val param3: Double = 3.0,

    @Column(nullable = false)
    val param4: Double = 3.0,

    @Column(nullable = false)
    val param5: Double = 3.0,

    @Column(nullable = false)
    val param6: Double = 0.15,

    @Column(nullable = false)
    val param7: Double = 1.3,

    @Column(nullable = false)
    val param8: Double = 0.7,
)
