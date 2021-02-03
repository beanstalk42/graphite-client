package ch.beanstalk.graphite

import mu.KotlinLogging
import net.razorvine.pickle.Pickler
import java.nio.ByteBuffer
import java.time.OffsetDateTime

private val logger = KotlinLogging.logger {}

interface GraphiteClient {

    fun send(metrics: List<Metric>)

    fun send(metric: Metric) {
        send(listOf(metric))
    }

    fun send(path: String, value: Number, timestamp: OffsetDateTime?) {
        send(Metric(path, value, timestamp))
    }
}

data class Metric(val path: String, val value: Number, val timestamp: OffsetDateTime?)


class PickleGraphiteClient(private val connectionFactory: ConnectionFactory/*private val host: String, private val port: Int = 2004*/) : GraphiteClient {
    override fun send(metrics: List<Metric>) {
        logger.debug { "sending ${metrics.size} records" }
        val elements = metrics.map { convertElement(it) }
        val payload = Pickler().dumps(elements)
        val data = header(payload.size) + payload
        send(data)
    }

    private fun convertElement(metric: Metric): Array<Any> {
        val value: Array<Any> = arrayOf(metric.timestamp?.toEpochSecond() ?: -1, metric.value)
        return arrayOf(metric.path, value)
    }

    private fun header(size: Int): ByteArray {
        val buffer = ByteBuffer.allocate(4)
                .putInt(size)
                .flip()
        val result = ByteArray(4)
        buffer.get(result)
        return result
    }

    private fun send(data: ByteArray) {
        logger.debug { "Writing ${data.toHexString()}" }
        connectionFactory.write(data)
    }
}

class PlainTextGraphiteClient(private val connectionFactory: ConnectionFactory) : GraphiteClient {

    override fun send(metrics: List<Metric>) {
        logger.debug { "sending ${metrics.size} records" }
        val data = metrics.map { convertMetric(it) }
        sendTcp(data)
    }

    private fun convertMetric(metric: Metric): ByteArray {
        return "${metric.path} ${metric.value} ${metric.timestamp?.toEpochSecond() ?: -1}\n".toByteArray()
    }

    private fun sendTcp(data: List<ByteArray>) {
        connectionFactory.write(concat(data))
    }

    private fun concat(data: List<ByteArray>): ByteArray {
        if (data.size == 1) return data[0]

        val n = data.sumOf { b -> b.size }
        val result = ByteArray(n)
        var count = 0
        data.forEachIndexed { i, current ->
            System.arraycopy(current, 0, result, count, current.size)
            count += data[i].size
        }
        return result
    }
}

private fun ByteArray.toHexString(): String {
    if (this.isEmpty()) return ""
    return this.joinToString("", "x") { "%02X".format(it) }
}
