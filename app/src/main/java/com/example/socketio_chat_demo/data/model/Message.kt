package com.example.socketio_chat_demo.data.model

data class Message(
    var sender: String,
    var receiver: String,
    var message: String,
    var seen: String,
    var createdAt: String,
    var type: String
)