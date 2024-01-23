package ru.matexp.features

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import ru.matexp.model.*
import ru.matexp.model.Matrix.Companion.addMatrices
import ru.matexp.model.Matrix.Companion.multiplyMatrices
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray
import kotlin.coroutines.CoroutineContext

object Manager {
    lateinit var matrixOperation: MatrixExpression

    fun startCalculations(matrixOperation: MatrixExpression, coroutineScope: CoroutineScope): Deferred<Matrix?> {
        this.matrixOperation = matrixOperation
        return coroutineScope.async {
            processMatrixExpression(matrixOperation, coroutineScope)
        }
    }

    private fun CoroutineScope.launchWorker(
        tasksChannel: ReceiveChannel<Operation>,
        resultsChannel: SendChannel<Matrix>
    ): Job = launch {
        for (task in tasksChannel) {
            val result = when (task.operation) {
                Operation.ADD_OPERATION -> task.operands.reduce(::addMatrices)
                Operation.MULTIPLY_OPERATION -> task.operands.reduce(::multiplyMatrices)
                Operation.NONE_OPERATION -> task.operands.first()
                else -> {
                    throw IllegalArgumentException("Unknown operation: ${task.operation}")
                }
            }
            resultsChannel.send(result)
        }
    }

    private suspend fun processMatrixExpression(matrixExpression: MatrixExpression, coroutineScope: CoroutineScope): Matrix? {
            val tasksChannel = Channel<Operation>(Channel.UNLIMITED)
            val resultsChannel = Channel<Matrix>(Channel.UNLIMITED)

            // Launch workers
            val numberOfWorkers = Runtime.getRuntime().availableProcessors()
            val workers = List(numberOfWorkers) { workerIndex ->
                coroutineScope.launchWorker(tasksChannel, resultsChannel)
            }

            // Send tasks to the channel
            matrixExpression.operands.forEach { op ->
                tasksChannel.send(Operation(op.operation, op.operands))
            }
            tasksChannel.close()

            val results = mutableListOf<Matrix>()
            repeat(matrixExpression.operands.size) {
                results += resultsChannel.receive()
            }
            resultsChannel.close()

            // Wait for all workers to finish
            workers.forEach { it.join() }

            // Perform the final operation
            return  when (matrixExpression.operation) {
                Operation.ADD_OPERATION -> results.reduce(::addMatrices)
                Operation.MULTIPLY_OPERATION -> results.reduce(::multiplyMatrices)
                else -> null
            }
        }
}