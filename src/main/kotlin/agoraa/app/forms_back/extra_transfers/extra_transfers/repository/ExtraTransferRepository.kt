package agoraa.app.forms_back.extra_transfers.extra_transfers.repository

import agoraa.app.forms_back.extra_transfers.extra_transfers.model.ExtraTransferModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ExtraTransferRepository : JpaRepository<ExtraTransferModel, Long>,
    JpaSpecificationExecutor<ExtraTransferModel> {
}