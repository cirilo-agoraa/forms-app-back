package agoraa.app.forms_back.model

import agoraa.app.forms_back.enum.StoresEnum
import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table(name = "extra_order_stores")
data class ExtraOrderStoreModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "extra_order_id", nullable = false)
    @JsonBackReference
    val extraOrder: ExtraOrderModel,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val store: StoresEnum
)