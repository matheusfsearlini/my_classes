package br.com.commons.data

import br.com.commons.models.Address
import java.time.LocalDateTime

data class CustomerCreateRequest(
    val companyName: String,
    val tradingName: String,
    val documentNumber: String,
    val nationalRegistry: String,
    val active: Boolean,
    val email: String,
    val phone: String,
    val mobilePhone: String,
    val address: List<Address>?,
    val note: String,
)

data class CustomerUpdateRequest(
    val companyName: String,
    val tradingName: String,
    val nationalRegistry: String,
    val active: Boolean,
    val email: String,
    val phone: String,
    val mobilePhone: String,
    val address: List<Address>?,
    val note: String,
    val revision: Long
)

data class CustomerResponse(
    val id: Long?,
    val companyName: String,
    val tradingName: String,
    val documentNumber: String,
    val nationalRegistry: String,
    val active: Boolean,
    val email: String,
    val phone: String,
    val mobilePhone: String,
    val address: List<Address>?,
    val createdAt: LocalDateTime,
    val note: String,
    val revision: Long?
)

data class CustomerGridResponse(
    val id: Long?,
    val companyName: String,
    val tradingName: String,
    val documentNumber: String,
    val active: Boolean,
    val email: String,
    val phone: String,
)