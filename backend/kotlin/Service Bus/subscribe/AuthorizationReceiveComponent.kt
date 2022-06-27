package br.com.authorization

import br.com.authorization.service.SendEmailSbSenderService
import br.com.authorization.service.UserService
import br.com.authorization.util.Resources
import br.com.commons.sb.schema.Authorization
import br.com.commons.sb.schema.EmailRequest
import com.azure.messaging.servicebus.ServiceBusClientBuilder
import com.azure.messaging.servicebus.ServiceBusErrorContext
import com.azure.messaging.servicebus.ServiceBusProcessorClient
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.simple.parser.ParseException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@Component
class AuthorizationReceiveComponent(
    private val userService: UserService,
    private val sendEmailSb: SendEmailSbSenderService,
    private val mapper: ObjectMapper
) : ApplicationRunner {
    @Value("\${service-bus.receive.connection-string}")
    private val sbConnectionString: String? = null

    @Value("\${service-bus.receive.queue-name}")
    private val sbQueueName: String? = null

    private lateinit var processorClient: ServiceBusProcessorClient

    private val processMessage = Consumer<ServiceBusReceivedMessageContext> {
        try {
            val authorization: Authorization =
                mapper.readValue(it.message.body.toString(), Authorization::class.java)
            logger.info("processMessage authorization to ${authorization.userDocument}")
            val user = userService.create(authorization)

            val template = Resources.template("welcome.html")
                .replace("TAG_USER_NAME", user.name)
                .replace("TAG_COMPANY_DOCUMENT", authorization.companyDocument)
                .replace("TAG_COMPANY_NAME", authorization.companyName)
                .replace("TAG_EMAIL", authorization.email)
                .replace("\r\n", "")

            val emailRequest = EmailRequest(
                toEmail = authorization.email,
                subject = "Boas Vindas!",
                body = template,
                contentType = "text/html",
                attachments = null
            )

            sendEmailSb.sendMessage(emailRequest)

            it.complete()
            logger.info("success to create a user")
        } catch (e: Exception) {
            when (e) {
                is IOException,
                is ParseException -> {
                    logger.error(e.message, e)
                }
            }
            logger.warn("fail to create a user detail: ${e.message}")
            it.abandon()
        }
    }

    private var processError = Consumer { errorContext: ServiceBusErrorContext ->
        logger.info("Error occurred while receiving message: ${errorContext.exception}")
    }

    override fun run(args: ApplicationArguments?) {
        receiveMessages()
    }

    @Throws(InterruptedException::class)
    fun receiveMessages() {
        try {
            processorClient = ServiceBusClientBuilder()
                .connectionString(sbConnectionString)
                .processor()
                .queueName(sbQueueName)
                .processMessage(processMessage)
                .processError(processError)
                .disableAutoComplete()
                .buildProcessorClient()
            logger.info("Starting the processor")
            processorClient.start()
            TimeUnit.SECONDS.sleep(5)
        } catch (e: Exception) {
            logger.warn("Stopping and closing the processor, error: ${e.message}")
            processorClient.close()
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AuthorizationReceiveComponent::class.java)
    }

}
