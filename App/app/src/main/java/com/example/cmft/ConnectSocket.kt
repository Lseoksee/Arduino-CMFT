package com.example.cmft

import com.example.cmft.wapper.SocketCallBack
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


class ConnectSocket(private val addr: String, private val port: Int) {
    companion object {
        const val CONNECT_MEG = "connect";
    }

    fun recvMessege(callBack: SocketCallBack) {
        Thread {
            val serverAddr = InetAddress.getByName(addr)
            val socket = DatagramSocket()
            var buffer = ByteArray(512)

            val messageByte = CONNECT_MEG.toByteArray()

            val sendPacket = DatagramPacket(
                messageByte,
                messageByte.size,
                serverAddr,
                port
            )
            socket.send(sendPacket)

            while (true) {
                val packet = DatagramPacket(buffer, buffer.size, serverAddr, port)
                socket.receive(packet)
                callBack.recv(packet.data)
                buffer = ByteArray(512)
            }

        }.start()

    }
}