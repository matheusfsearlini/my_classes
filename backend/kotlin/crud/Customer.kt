package br.com.registry.customer

import br.com.commons.models.Address
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "tb_customer")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
data class Customer(

    @Id
    @SequenceGenerator(name = "customer_id_seq", sequenceName = "customer_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq")
    var id: Long? = null,
    /**
     * RAZAO SOCIAL
     */
    @Column(nullable = false)
    var companyName: String = "",
    /**
     * NOME FANTASIA
     */
    @Column(nullable = false)
    var tradingName: String = "",
    /**
     * CPF-CNPJ
     */
    @Column(length = 14, nullable = false)
    var documentNumber: String = "",
    /**
     * RG-IE
     */
    var nationalRegistry: String = "",
    @Column(nullable = false, columnDefinition = "boolean default true")
    var active: Boolean = true,
    @Column(length = 30)
    var email: String = "",
    @Column(length = 14)
    var phone: String = "",
    @Column(length = 14)
    var mobilePhone: String = "",
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    var address: List<Address>?,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var note: String = "",
    @Column(nullable = false)
    var establishmentId: String,
    @Version
    var revision: Long? = null,

    )