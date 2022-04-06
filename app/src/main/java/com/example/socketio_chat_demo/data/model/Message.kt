package com.example.socketio_chat_demo.data.model

data class Message(var userName: String, var messageContent: String, var roomName: String, var type: Int)