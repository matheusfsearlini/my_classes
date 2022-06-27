package br.com.registry.customer

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CustomerRepository : JpaRepository<Customer, Long> {
    @Query(
        "SELECT CASE WHEN COUNT(c) >0 THEN true ELSE false " +
                "END FROM Customer c WHERE c.documentNumber = :documentNumber AND c.establishmentId = :establishmentId"
    )
    fun customerExist(documentNumber: String?, establishmentId: String): Boolean
    fun findByIdAndEstablishmentId(id: Long, establishmentId: String): Customer?
    fun findByEstablishmentId(establishmentId: String): List<Customer>
    fun deleteByIdAndEstablishmentId(id: Long, establishmentId: String)
}