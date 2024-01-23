package ru.matexp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuccessResponse(
    @SerialName("result_matrix") val resultMatrix: List<List<Int>>
)