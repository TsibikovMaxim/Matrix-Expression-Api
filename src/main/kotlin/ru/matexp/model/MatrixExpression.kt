package ru.matexp.model

import kotlinx.serialization.Serializable

@Serializable
data class Operation(
    val operation: String,
    val operands: List<Matrix>
) {
    companion object {
        const val ADD_OPERATION = "add"
        const val MULTIPLY_OPERATION = "multiply"
        const val NONE_OPERATION = "none"
    }
}

@Serializable
data class MatrixExpression(
    val operation: String,
    val operands: List<Operation>
)