package agoraa.app.forms_back.model

import agoraa.app.forms_back.enum.extra_order.OriginEnum
import agoraa.app.forms_back.enum.extra_order.PartialCompleteEnum
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "extra_orders")
data class ExtraOrderModel(
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
    @Enumerated(EnumType.STRING)
    val partialComplete: PartialCompleteEnum,

    @Column(nullable = false)
    val processed: Boolean = false,

    @Column(nullable = false)
    val dateSubmitted: LocalDate = LocalDate.now(),

    @OneToMany(mappedBy = "extraOrder", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    val stores: MutableList<ExtraOrderStoreModel> = mutableListOf(),

    @OneToMany(mappedBy = "extraOrder", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    var products: MutableList<ExtraOrderProductModel> = mutableListOf(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var origin: OriginEnum? = null
)
