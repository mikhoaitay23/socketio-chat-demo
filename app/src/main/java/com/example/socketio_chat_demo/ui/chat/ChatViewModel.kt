package com.example.socketio_chat_demo.ui.chat

import android.app.Application
import android.text.Editable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.socketio_chat_demo.data.model.Message
import com.example.socketio_chat_demo.data.model.Room
import com.example.socketio_chat_demo.data.model.SendMessage
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.utils.Constants
import com.example.socketio_chat_demo.utils.SocketHandler
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.launch

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

    private val onUpdateChat = Emitter.Listener {
        viewModelScope.launch {
            val message: Message = gson.fromJson(it[0].toString(), Message::class.java)
            message.type = 1
            messageLiveData.value = DataResponse.DataSuccessResponse(message)
        }
    }

    private val onUserLeft = Emitter.Listener {
        viewModelScope.launch {
            val leftUserName = it[0] as String
            val message = Message(leftUserName, "", "", 3)
            messageLiveData.value = DataResponse.DataSuccessResponse(message)
        }
    }

    private val onTyping = Emitter.Listener {
        viewModelScope.launch {
            Log.d("TAG", ": haha")
        }
    }

    private val onStopTyping = Emitter.Listener {
        viewModelScope.launch {
            Log.d("TAG", ": hoho")
        }
    }

    init {
        SocketHandler.setSocket()
        mSocket = SocketHandler.getSocket()
        mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
        mSocket!!.on(Constants.NEW_USER, onNewUser)
        mSocket!!.on(Constants.UPDATE_CHAT, onUpdateChat)
        mSocket!!.on(Constants.USER_LEFT, onUserLeft)
        mSocket!!.on(Constants.TYPING, onTyping)
        mSocket!!.on(Constants.STOP_TYPING, onStopTyping)
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

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count > 0)
            mSocket!!.emit(Constants.TYPING, room.roomName)
        else
            mSocket!!.emit(Constants.STOP_TYPING, room.roomName)
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