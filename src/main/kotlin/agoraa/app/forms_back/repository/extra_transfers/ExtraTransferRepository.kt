package agoraa.app.forms_back.repository.extra_transfers

import agoraa.app.forms_back.model.extra_transfers.ExtraTransferModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ExtraTransferRepository : JpaRepository<ExtraTransferModel, Long>,
    JpaSpecificationExecutor<ExtraTransferModel> {
}