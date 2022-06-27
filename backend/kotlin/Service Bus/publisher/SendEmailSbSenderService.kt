package br.com.authorization.service

import br.com.commons.sb.schema.EmailRequest

interface SendEmailSbSenderService {
    fun sendMessage(emailRequest: EmailRequest)
}