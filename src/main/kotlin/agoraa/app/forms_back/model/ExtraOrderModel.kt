package agoraa.app.forms_back.model

import agoraa.app.forms_back.enums.extra_order.OriginEnum
import agoraa.app.forms_back.enums.extra_order.PartialCompleteEnum
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "extra_orders")
@NamedEntityGraph(
    name = "graph.ExtraOrderModel.all",
    attributeNodes = [
        NamedAttributeNode("user"),
        NamedAttributeNode("supplier"),
        NamedAttributeNode("products")
    ],
    subgraphs = [
        NamedSubgraph(
            name = "user",
            attributeNodes = [
                NamedAttributeNode("id"),
                NamedAttributeNode("username"),
            ]
        ),
        NamedSubgraph(
            name = "supplier",
            attributeNodes = [
                NamedAttributeNode("id"),
                NamedAttributeNode("name")
            ]
        ),
        NamedSubgraph(
            name = "product",
            attributeNodes = [
                NamedAttributeNode("id"),
                NamedAttributeNode("name"),
                NamedAttributeNode("code"),
                NamedAttributeNode("store"),
                NamedAttributeNode("barcode"),
                NamedAttributeNode("brand"),
            ]
        )
    ],
)
data class ExtraOrderModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserModel,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    val supplier: SupplierModel,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val partialComplete: PartialCompleteEnum,

    @Column(nullable = false)
    val processed: Boolean = false,

    @Column(nullable = false)
    val dateSubmitted: LocalDate = LocalDate.now(),

    @OneToMany(mappedBy = "extraOrder", cascade = [CascadeType.ALL], orphanRemoval = true)
    val stores: MutableList<ExtraOrderStoreModel> = mutableListOf(),

    @OneToMany(mappedBy = "extraOrder", cascade = [CascadeType.ALL], orphanRemoval = true)
    var products: MutableList<ExtraOrderProductModel> = mutableListOf(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var origin: OriginEnum? = null
)
