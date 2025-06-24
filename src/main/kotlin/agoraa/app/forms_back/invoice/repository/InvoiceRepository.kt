package agoraa.app.forms_back.invoice.repository

import agoraa.app.forms_back.invoice.model.Invoice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceRepository : JpaRepository<Invoice, Long> {
    fun findByToBonusTrue(): List<Invoice>
    fun findByRetainedStatusGreaterThan(status: Int): List<Invoice>
    fun findByToBonusTrueOrderByCreatedAtAsc(): List<Invoice>
}