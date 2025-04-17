package agoraa.app.forms_back.store_audits.store_audit_config.model

import jakarta.persistence.*

@Entity
@Table(name = "store_audit_config")
data class StoreAuditConfigModel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val daysToNotRepeatProducts: Long = 30,

    @Column(nullable = false)
    val dailyProductsLimit: Int = 100,

    @Column(nullable = false)
    val formDurationDays: Long = 2,

    @OneToMany(
        mappedBy = "storeAuditConfig",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    var excludeSectors: MutableList<StoreAuditConfigSectorsModel> = mutableListOf(),

    @OneToMany(
        mappedBy = "storeAuditConfig",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    var excludeGroups: MutableList<StoreAuditConfigGroupsModel> = mutableListOf()
)
