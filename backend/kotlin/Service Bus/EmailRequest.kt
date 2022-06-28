package br.com.commons.sb.schema

data class EmailRequest(
    val toEmail: String,
    val subject: String,
    val body: String,
    val contentType: String = "text/plain",
    val attachments: List<Attachments>?
)

data class Attachments(
    val contentBase64: String,
    val type: String = "image/png",
    val filename: String,
)
