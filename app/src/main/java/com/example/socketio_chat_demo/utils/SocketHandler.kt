package com.example.socketio_chat_demo.utils

import io.socket.client.IO
import io.socket.client.Socket
import java.lang.Exception

object SocketHandler {

    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
            mSocket = IO.socket(Constants.SOCKET_SERVER_URL)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSocket(): Socket {
        return mSocket
    }

}