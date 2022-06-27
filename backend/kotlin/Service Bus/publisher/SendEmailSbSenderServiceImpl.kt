package br.com.authorization.service.impl

import br.com.authorization.service.SendEmailSbSenderService
import br.com.commons.sb.schema.EmailRequest
import com.azure.messaging.servicebus.ServiceBusClientBuilder
import com.azure.messaging.servicebus.ServiceBusMessage
import com.azure.messaging.servicebus.ServiceBusSenderClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class SendEmailSbSenderServiceImpl(
    private val mapper: ObjectMapper
) : SendEmailSbSenderService {

    @Value("\${send-email.service-bus.sender.connection-string}")
    private lateinit var sendEmailSbConnectionString: String

    private lateinit var serviceBusSenderClient: ServiceBusSenderClient

    @PostConstruct
    fun init() {
        serviceBusSenderClient = ServiceBusClientBuilder()
            .connectionString(sendEmailSbConnectionString)
            .sender()
            .buildClient()

    }

    override fun sendMessage(emailRequest: EmailRequest) {
        serviceBusSenderClient.sendMessage(ServiceBusMessage(mapper.writeValueAsString(emailRequest)))
    }

}