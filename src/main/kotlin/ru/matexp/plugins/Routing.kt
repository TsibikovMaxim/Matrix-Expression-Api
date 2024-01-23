package ru.matexp.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import ru.matexp.features.Manager
import ru.matexp.model.*

fun Application.configureRouting() {
    routing {
        get("/") {
            val result = withContext(Dispatchers.IO) {
                "Hello World!"
            }
            call.respondText(result)
        }

        get("/calculate") {
            val expressionJsonString: String? = call.request.queryParameters["expression"]
            if(expressionJsonString == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorText = "You have not passed any expression",
                    )
                )
                return@get
            }

            val expression: MatrixExpression
            try {
                val jsonFormat = Json { coerceInputValues = true }
                expression = jsonFormat.decodeFromString<MatrixExpression>(expressionJsonString)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorText = "Incorrect expression",
                    )
                )
                println(expressionJsonString)
                println(e.message)
                return@get
            }

            val coroutineScope = CoroutineScope(Dispatchers.Default)

            try {
                val resultMatrix = Manager.startCalculations(expression, coroutineScope).await()

                if (resultMatrix != null) {
                    println("resultMatrix: $resultMatrix")
                    call.respond(HttpStatusCode.OK, SuccessResponse(resultMatrix = resultMatrix.matrix))
                } else {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(errorText = "Unknown operation in the expression"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(errorText = e.message ?: "Unknown error"))
            }
        }

    }
}
