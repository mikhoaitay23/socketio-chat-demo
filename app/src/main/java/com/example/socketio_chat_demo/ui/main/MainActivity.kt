package com.example.socketio_chat_demo.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.socketio_chat_demo.R
import com.example.socketio_chat_demo.utils.SocketHandler
import io.socket.client.Socket

class MainActivity : AppCompatActivity() {

    private var mSocket: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SocketHandler.setSocket()
        mSocket = SocketHandler.getSocket()
        mSocket!!.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket!!.disconnect()
    }
}