package ru.matexp

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.testing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.matexp.model.Matrix
import ru.matexp.model.MatrixExpression
import ru.matexp.model.Operation
import ru.matexp.model.SuccessResponse
import kotlin.test.*
import ru.matexp.plugins.*
import java.net.URLEncoder

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    @Test
    fun `test - (M1 + M2)`() = testApplication {
        val expressionJson = Json.encodeToString(
            MatrixExpression(
                operation = "add",
                operands = listOf(
                    Operation(
                        operation = "none",
                        operands = listOf(Matrix(matrix = listOf(listOf(1, 2), listOf(3, 4))))
                    ),
                    Operation(
                        operation = "none",
                        operands = listOf(Matrix(matrix = listOf(listOf(5, 6), listOf(7, 8))))
                    )
                )
            )
        )
        val encodedExpression = URLEncoder.encode(expressionJson, "UTF-8")

        client.get("/calculate?expression=$encodedExpression").apply {
            CoroutineScope(Dispatchers.Default).launch {
                val expectedResponse = Json.encodeToString(
                    SuccessResponse(resultMatrix = listOf(listOf(6, 8), listOf(10, 12)))
                )
                assertEquals(expectedResponse, bodyAsText())
            }
        }
    }

    @Test
    fun `test - (M1 multiply by M2)`() = testApplication {
        val expressionJson = Json.encodeToString(
            MatrixExpression(
                operation = "multiply",
                operands = listOf(
                    Operation(
                        operation = "none",
                        operands = listOf(Matrix(matrix = listOf(listOf(1, 2), listOf(3, 4))))
                    ),
                    Operation(
                        operation = "none",
                        operands = listOf(Matrix(matrix = listOf(listOf(5, 6), listOf(7, 8))))
                    )
                )
            )
        )
        val encodedExpression = URLEncoder.encode(expressionJson, "UTF-8")

        client.get("/calculate?expression=$encodedExpression").apply {
            CoroutineScope(Dispatchers.Default).launch {
                val expectedResponse = Json.encodeToString(
                    SuccessResponse(resultMatrix = listOf(listOf(19, 22), listOf(43, 50)))
                )
                assertEquals(expectedResponse, bodyAsText())
            }
        }
    }

    @Test
    fun `test - (M1 + M2 + M3)`() = testApplication {
        val expressionJson = Json.encodeToString(
            MatrixExpression(
                operation = "add",
                operands = listOf(
                    Operation(
                        operation = "none",
                        operands = listOf(Matrix(matrix = listOf(listOf(1, 2), listOf(3, 4))))
                    ),
                    Operation(
                        operation = "none",
                        operands = listOf(Matrix(matrix = listOf(listOf(5, 6), listOf(7, 8))))
                    ),
                    Operation(
                        operation = "none",
                        operands = listOf(Matrix(matrix = listOf(listOf(9, 10), listOf(11, 12))))
                    )
                )
            )
        )
        val encodedExpression = URLEncoder.encode(expressionJson, "UTF-8")

        client.get("/calculate?expression=$encodedExpression").apply {
            CoroutineScope(Dispatchers.Default).launch {
                val expectedResponse = Json.encodeToString(
                    SuccessResponse(resultMatrix = listOf(listOf(15, 18), listOf(21, 24)))
                )
                assertEquals(expectedResponse, bodyAsText())
            }
        }
    }

    @Test
    fun `test - ((M1 + M2) multiply by M3)`() = testApplication {
        val expressionJson = Json.encodeToString(
            MatrixExpression(
                operation = "multiply",
                operands = listOf(
                    Operation(
                        operation = "add",
                        operands = listOf(
                            Matrix(matrix = listOf(listOf(1, 2), listOf(3, 4))),
                            Matrix(matrix = listOf(listOf(5, 6), listOf(7, 8)))
                        )
                    ),
                    Operation(
                        operation = "none",
                        operands = listOf(Matrix(matrix = listOf(listOf(2, 2), listOf(2, 2))))
                    )
                )
            )
        )
        val encodedExpression = URLEncoder.encode(expressionJson, "UTF-8")

        client.get("/calculate?expression=$encodedExpression").apply {
            CoroutineScope(Dispatchers.Default).launch {
                val expectedResponse = Json.encodeToString(
                    SuccessResponse(resultMatrix = listOf(listOf(28, 28), listOf(44, 44)))
                )
                assertEquals(expectedResponse, bodyAsText())
            }
        }
    }
}
