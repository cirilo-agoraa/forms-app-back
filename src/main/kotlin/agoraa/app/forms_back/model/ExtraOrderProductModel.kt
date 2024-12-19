package agoraa.app.forms_back.model

import jakarta.persistence.*

@Entity
@Table(name = "extra_order_products")
@NamedEntityGraph(
    name = "graph.ExtraOrderProductModel.all",
    attributeNodes = [ NamedAttributeNode("product") ],
    subgraphs = [
        NamedSubgraph(
            name = "product",
            attributeNodes = [
                NamedAttributeNode("id"),
                NamedAttributeNode("name"),
                NamedAttributeNode("code"),
                NamedAttributeNode("store"),
                NamedAttributeNode("barcode"),
                NamedAttributeNode("brand"),
                NamedAttributeNode("supplier")
            ]
        )
    ]
)
data class ExtraOrderProductModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductModel,

    @ManyToOne
    @JoinColumn(name = "extra_order_id", nullable = false)
    val extraOrder: ExtraOrderModel,

    @Column(nullable = false)
    var price: Double,

    @Column(nullable = false)
    var quantity: Int
)
