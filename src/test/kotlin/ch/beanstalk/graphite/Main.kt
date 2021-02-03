package ch.beanstalk.graphite

import java.time.OffsetDateTime
import kotlin.random.Random

@Suppress("UNUSED_PARAMETER")
fun main(args: Array<String>) {
    val plainTextClient = PlainTextGraphiteClient(tcpConnectionFactory("127.0.0.1", 2003))
    val pickleClient = PickleGraphiteClient(tcpConnectionFactory("127.0.0.1", 2004))

    val rnd = Random(0)

    while (true) {

        val t1 = Metric("beanstalk.plain.random", rnd.nextDouble(0.0, 100.0), OffsetDateTime.now())
        val t2 = Metric("beanstalk.plain.random", rnd.nextDouble(0.0, 100.0), null)
        plainTextClient.send(listOf(t1, t2))

        val p1 = Metric("beanstalk.pickle.random", rnd.nextInt(-10, 10), OffsetDateTime.now())
        val p2 = Metric("beanstalk.pickle.random", rnd.nextInt(-10, 10), null)
        pickleClient.send(listOf(p1, p2))
        sleep(5_000)
    }
}

fun sleep(ms: Long) {
    Thread.sleep(ms)
}
