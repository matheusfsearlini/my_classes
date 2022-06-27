package br.com.registry.customer

import br.com.commons.data.CustomerCreateRequest
import br.com.commons.data.CustomerGridResponse
import br.com.commons.data.CustomerResponse
import br.com.commons.data.CustomerUpdateRequest

interface CustomerService {

    fun getByEstablishmentId(establishmentId: String): List<CustomerGridResponse>
    fun getById(id: Long, establishmentId: String): CustomerResponse
    fun create(customerCreateRequest: CustomerCreateRequest, establishmentId: String): CustomerResponse
    fun update(id: Long, establishmentId: String, customerUpdateRequest: CustomerUpdateRequest): CustomerResponse
    fun delete(id: Long, establishmentId: String)

}