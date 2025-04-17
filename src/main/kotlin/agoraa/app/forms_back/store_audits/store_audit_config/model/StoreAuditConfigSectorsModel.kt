package agoraa.app.forms_back.store_audits.store_audit_config.model

import agoraa.app.forms_back.shared.enums.ProductSectorsEnum
import jakarta.persistence.*

@Entity
@Table(name = "store_audit_config_sectors")
data class StoreAuditConfigSectorsModel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "store_audit_config_id", nullable = false)
    val storeAuditConfig: StoreAuditConfigModel,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val sector: ProductSectorsEnum
)