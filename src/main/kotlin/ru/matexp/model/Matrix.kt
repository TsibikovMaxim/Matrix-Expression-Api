package ru.matexp.model

import kotlinx.serialization.Serializable

@Serializable
data class Matrix(
    val matrix: List<List<Int>>
) {
    override fun toString(): String {
        return matrix.joinToString(separator = "\n") { row ->
            row.joinToString(prefix = "[", postfix = "]", separator = ", ") { element ->
                element.toString()
            }
        }
    }

    companion object {
        fun addMatrices(a: Matrix, b: Matrix): Matrix {
            val result = a.matrix.indices.map { i ->
                a.matrix[i].indices.map { j ->
                    a.matrix[i][j] + b.matrix[i][j]
                }
            }
            return Matrix(result)
        }

        fun multiplyMatrices(a: Matrix, b: Matrix): Matrix {
            val result = Array(a.matrix.size) { IntArray(b.matrix[0].size) { 0 } }
            for (i in a.matrix.indices) {
                for (j in b.matrix[0].indices) {
                    for (k in a.matrix[0].indices) {
                        result[i][j] += a.matrix[i][k] * b.matrix[k][j]
                    }
                }
            }
            return Matrix(result.map { it.toList() })
        }
    }
}