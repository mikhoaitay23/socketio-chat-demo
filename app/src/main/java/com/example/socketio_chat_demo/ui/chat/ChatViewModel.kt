package com.example.socketio_chat_demo.ui.chat

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.socketio_chat_demo.data.model.Message
import com.example.socketio_chat_demo.data.model.Room
import com.example.socketio_chat_demo.data.model.SendMessage
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.utils.Constants
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.launch
import java.lang.Exception

class ChatViewModel(val application: Application, private val room: Room) : ViewModel() {

    private var mSocket: Socket? = null
    private val gson = Gson()
    var messageLiveData = MutableLiveData<DataResponse<Message>>()
    var typeMessageLiveData = MutableLiveData<String>()

    private val onConnect = Emitter.Listener {
        viewModelScope.launch {
            val data = room
            val jsonData = gson.toJson(data)
            mSocket!!.emit(Constants.SUBSCRIBE, jsonData)
        }
    }

    private val onNewUser = Emitter.Listener {
        viewModelScope.launch {
            val name = it[0] as String
            val message = Message(name, "", room.roomName, 2)
            messageLiveData.value = DataResponse.DataSuccessResponse(message)
        }
    }

    private var onUpdateChat = Emitter.Listener {
        viewModelScope.launch {
            val message: Message = gson.fromJson(it[0].toString(), Message::class.java)
            message.type = 1
            messageLiveData.value = DataResponse.DataSuccessResponse(message)
        }
    }

    private var onUserLeft = Emitter.Listener {
        viewModelScope.launch {
            val leftUserName = it[0] as String
            val message = Message(leftUserName, "", "", 3)
            messageLiveData.value = DataResponse.DataSuccessResponse(message)
        }
    }

    init {
        try {
            mSocket = IO.socket(Constants.SOCKET_SERVER_URL)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
        mSocket!!.on(Constants.NEW_USER, onNewUser)
        mSocket!!.on(Constants.UPDATE_CHAT, onUpdateChat)
        mSocket!!.on(Constants.USER_LEFT, onUserLeft)
        mSocket!!.connect()
    }

    fun onSendMessage() {
        viewModelScope.launch {
            val validateType = validate()
            if (validateType == Constants.ValidateType.ValidateDone) {
                val sendData =
                    SendMessage(room.userName, typeMessageLiveData.value!!, room.roomName)
                val jsonData = gson.toJson(sendData)
                mSocket!!.emit(Constants.NEW_MESSAGE, jsonData)

                val message = Message(room.userName, typeMessageLiveData.value!!, room.roomName, 0)
                messageLiveData.value = DataResponse.DataSuccessResponse(message)
                typeMessageLiveData.value = ""
            }
        }
    }

    fun onDisconnectSocket() {
        val jsonData = Gson().toJson(room)
        mSocket!!.emit(Constants.UNSUBSCRIBE, jsonData)
        mSocket!!.disconnect()
    }

    private fun validate(): Constants.ValidateType {
        return if (typeMessageLiveData.value.isNullOrEmpty() || typeMessageLiveData.value == "") {
            Constants.ValidateType.EmptyMessage
        } else {
            Constants.ValidateType.ValidateDone
        }
    }

    class Factory(private val application: Application, private val room: Room) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                return ChatViewModel(application, room) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}