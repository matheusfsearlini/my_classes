package br.com.commons.sb.schema

data class Authorization(
    val establishmentId: String,
    val companyName: String,
    val companyDocument: String,
    val userName: String,
    val userDocument: String,
    val mobilePhone: String,
    val email: String,
    val password: String,
)