package ch.beanstalk.graphite

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket

fun tcpConnectionFactory(host: String, port: Int): ConnectionFactory {
    return TcpConnection(host, port)
}

fun udpConnectionFactory(host: String, port: Int): ConnectionFactory {
    return UdpConnection(host, port)
}

interface ConnectionFactory {
    fun write(data: ByteArray)
}

private class TcpConnection(private val host: String, private val port: Int) : ConnectionFactory {
    override fun write(data: ByteArray) {
        Socket(host, port).use { socket ->
            socket.getOutputStream().write(data)
        }
    }
}

private class UdpConnection(private val host: String, private val port: Int) : ConnectionFactory {
    override fun write(data: ByteArray) {
        DatagramSocket().use { socket ->
            socket.send(DatagramPacket(data, data.size, InetAddress.getByName(host), port))
        }
    }
}
