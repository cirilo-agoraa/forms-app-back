package agoraa.app.forms_back.extra_transfers.extra_transfer_products.repository

import agoraa.app.forms_back.extra_transfers.extra_transfer_products.model.ExtraTransferProductsModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ExtraTransferProductsRepository : JpaRepository<ExtraTransferProductsModel, Long>,
    JpaSpecificationExecutor<ExtraTransferProductsModel> {

    fun findByExtraTransferId(extraTransferId: Long): List<ExtraTransferProductsModel>
}