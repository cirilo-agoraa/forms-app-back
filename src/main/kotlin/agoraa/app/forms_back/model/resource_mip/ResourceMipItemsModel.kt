package agoraa.app.forms_back.model.resource_mip

import agoraa.app.forms_back.enum.MipsCategoriesEnum
import jakarta.persistence.*

@Entity
@Table(name = "resource_mip_items")
data class ResourceMipItemsModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "resource_mip_id", nullable = false)
    val resourceMip: ResourceMipModel,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val category: MipsCategoriesEnum,

    @Column(nullable = false)
    val quantity: Int
)
