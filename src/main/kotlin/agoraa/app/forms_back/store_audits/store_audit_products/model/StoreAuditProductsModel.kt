package agoraa.app.forms_back.store_audits.store_audit_products.model

import agoraa.app.forms_back.products.products.model.ProductModel
import agoraa.app.forms_back.store_audits.store_audits.model.StoreAuditModel
import jakarta.persistence.*

@Entity
@Table(name = "store_audit_products")
data class StoreAuditProductsModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "store_audit_id", nullable = false)
    val storeAudit: StoreAuditModel,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductModel,

    @Column(nullable = false)
    val inStore: Boolean = false
)
