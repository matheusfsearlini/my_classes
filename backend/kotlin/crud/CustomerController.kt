package br.com.registry.customer

import br.com.commons.data.CustomerCreateRequest
import br.com.commons.data.CustomerUpdateRequest
import br.com.registry.utils.ResponseUtils
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
class CustomerController(
    private val customerService: CustomerService,
    private val customerJasperService: CustomerJasperService
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllEstablishmentId(
        @RequestHeader("establishmentId") establishmentId: String
    ) = customerService.getByEstablishmentId(establishmentId)

    @GetMapping(value = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun getCustomerById(
        @PathVariable id: Long,
        @RequestHeader("establishmentId") establishmentId: String
    ) = customerService.getById(id, establishmentId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody customerCreateRequest: CustomerCreateRequest,
        @RequestHeader("establishmentId") establishmentId: String
    ) = customerService.create(customerCreateRequest, establishmentId)

    @PutMapping(value = ["/{id}"])
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun update(
        @PathVariable id: Long,
        @RequestBody customerUpdateRequest: CustomerUpdateRequest,
        @RequestHeader("establishmentId") establishmentId: String
    ) = customerService.update(id, establishmentId, customerUpdateRequest)

    @DeleteMapping(value = ["/{id}"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: Long,
        @RequestHeader("establishmentId") establishmentId: String
    ) = customerService.delete(id, establishmentId)

}