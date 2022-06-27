package br.com.registry.customer

import br.com.commons.data.CustomerCreateRequest
import br.com.commons.data.CustomerGridResponse
import br.com.commons.data.CustomerResponse
import br.com.commons.data.CustomerUpdateRequest
import br.com.commons.message.InfoMessage
import org.springframework.stereotype.Service
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository
) : CustomerService {

    override fun getByEstablishmentId(establishmentId: String): List<CustomerGridResponse> {
        return customerRepository.findByEstablishmentId(establishmentId).map {
            CustomerGridResponse(
                id = it.id,
                companyName = it.companyName,
                tradingName = it.tradingName,
                documentNumber = it.documentNumber,
                active = it.active,
                email = it.email,
                phone = it.phone,
            )
        }
    }

    override fun getById(id: Long, establishmentId: String): CustomerResponse {
        val customerDB = customerRepository.findByIdAndEstablishmentId(id, establishmentId)
            ?: throw EntityNotFoundException(InfoMessage.ENTITY_NOT_FOUND)
        return getCustomerResponse(customerDB)
    }

    override fun create(customerCreateRequest: CustomerCreateRequest, establishmentId: String): CustomerResponse {
        if (customerRepository.customerExist(customerCreateRequest.documentNumber, establishmentId))
            throw EntityExistsException(InfoMessage.ENTITY_EXISTS)
        val customer = Customer(
            companyName = customerCreateRequest.companyName,
            tradingName = customerCreateRequest.tradingName,
            documentNumber = customerCreateRequest.documentNumber,
            nationalRegistry = customerCreateRequest.nationalRegistry,
            active = customerCreateRequest.active,
            email = customerCreateRequest.email,
            phone = customerCreateRequest.phone,
            mobilePhone = customerCreateRequest.mobilePhone,
            address = customerCreateRequest.address,
            note = customerCreateRequest.note,
            establishmentId = establishmentId,
        )
        val customerDB = customerRepository.save(customer)
        return getCustomerResponse(customerDB)
    }

    override fun update(
        id: Long,
        establishmentId: String,
        customerUpdateRequest: CustomerUpdateRequest
    ): CustomerResponse {
        val customerDB = customerRepository.findByIdAndEstablishmentId(id, establishmentId)
            ?: throw EntityNotFoundException(InfoMessage.ENTITY_NOT_FOUND)
        customerDB.companyName = customerUpdateRequest.companyName
        customerDB.tradingName = customerUpdateRequest.tradingName
        customerDB.nationalRegistry = customerUpdateRequest.nationalRegistry
        customerDB.active = customerUpdateRequest.active
        customerDB.email = customerUpdateRequest.email
        customerDB.phone = customerUpdateRequest.phone
        customerDB.mobilePhone = customerUpdateRequest.mobilePhone
        customerDB.address = customerUpdateRequest.address
        customerDB.note = customerUpdateRequest.note
        customerDB.revision = customerUpdateRequest.revision
        val customerUpdate = customerRepository.save(customerDB)
        return getCustomerResponse(customerUpdate)
    }

    @Transactional
    override fun delete(id: Long, establishmentId: String) {
        customerRepository.deleteByIdAndEstablishmentId(id, establishmentId)
    }

    private fun getCustomerResponse(customer: Customer): CustomerResponse {
        return CustomerResponse(
            id = customer.id,
            companyName = customer.companyName,
            tradingName = customer.tradingName,
            documentNumber = customer.documentNumber,
            nationalRegistry = customer.nationalRegistry,
            active = customer.active,
            email = customer.email,
            phone = customer.phone,
            mobilePhone = customer.mobilePhone,
            address = customer.address,
            createdAt = customer.createdAt,
            note = customer.note,
            revision = customer.revision
        )
    }

}