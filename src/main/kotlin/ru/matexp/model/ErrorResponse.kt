package ru.matexp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("error_text") val errorText: String,
    @SerialName("code") val code: Int? = null,
    @SerialName("description") val description: String? = null
)