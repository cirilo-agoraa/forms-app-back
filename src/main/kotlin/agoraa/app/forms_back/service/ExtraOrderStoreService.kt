package agoraa.app.forms_back.service

import agoraa.app.forms_back.exceptions.ResourceNotFoundException
import agoraa.app.forms_back.model.ExtraOrderStoreModel
import agoraa.app.forms_back.repository.ExtraOrderStoreRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ExtraOrderStoreService(
    private val extraOrderStoreRepository: ExtraOrderStoreRepository
) {

    fun findAll(page: Int, size: Int, sort: String, direction: String): Page<ExtraOrderStoreModel> {
        val sortDirection = if (direction.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort))

        return extraOrderStoreRepository.findAll(pageable)
    }

    fun findById(id: Long): ExtraOrderStoreModel {
        return extraOrderStoreRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Extra Order Store not found.") }
    }

    fun findByExtraOrderId(extraOrderId: Long): List<ExtraOrderStoreModel> {
        return extraOrderStoreRepository.findByExtraOrderId(extraOrderId)
    }
}