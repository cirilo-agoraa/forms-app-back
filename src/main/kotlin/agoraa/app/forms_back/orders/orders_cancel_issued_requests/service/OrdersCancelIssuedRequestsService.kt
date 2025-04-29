package agoraa.app.forms_back.orders.orders_cancel_issued_requests.service

import agoraa.app.forms_back.config.CustomUserDetails
import agoraa.app.forms_back.orders.orders_cancel_issued_requests.dto.request.OrdersCancelIssuedRequestsPatchRequest
import agoraa.app.forms_back.orders.orders_cancel_issued_requests.dto.request.OrdersCancelIssuedRequestsRequest
import agoraa.app.forms_back.orders.orders_cancel_issued_requests.model.OrderCancelIssuedRequestModel
import agoraa.app.forms_back.orders.orders_cancel_issued_requests.repository.OrdersCancelIssuedRequestsRepository
import agoraa.app.forms_back.shared.exception.ResourceNotFoundException
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class OrdersCancelIssuedRequestsService(private val ordersCancelIssuedRequestsRepository: OrdersCancelIssuedRequestsRepository) {

    private fun createCriteria(
        processed: Boolean? = null
    ): Specification<OrderCancelIssuedRequestModel> {
        return Specification { root: Root<OrderCancelIssuedRequestModel>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            processed?.let {
                predicates.add(criteriaBuilder.equal(root.get<Boolean>("processed"), it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun findById(id: Long): OrderCancelIssuedRequestModel {
        return ordersCancelIssuedRequestsRepository.findById(id).orElseThrow {
            throw ResourceNotFoundException("Order cancel issued request not found")
        }
    }

    fun getAll(processed: Boolean?): List<OrderCancelIssuedRequestModel> {
        val specification = createCriteria(processed)
        return ordersCancelIssuedRequestsRepository.findAll(specification)
    }

    fun create(customUserDetails: CustomUserDetails, request: OrdersCancelIssuedRequestsRequest) {
        val user = customUserDetails.getUserModel()

        ordersCancelIssuedRequestsRepository.save(
            OrderCancelIssuedRequestModel(
                order = request.order,
                user = user,
                motive = request.motive,
            )
        )
    }

    fun patch(id: Long, request: OrdersCancelIssuedRequestsPatchRequest) {
        val orderCancelIssuedRequest = findById(id)

        ordersCancelIssuedRequestsRepository.save(
            orderCancelIssuedRequest.copy(
                processed = request.processed ?: orderCancelIssuedRequest.processed,
            )
        )

    }
}