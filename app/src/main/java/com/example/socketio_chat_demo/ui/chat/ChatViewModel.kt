package com.example.socketio_chat_demo.ui.chat

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.socketio_chat_demo.data.model.Message
import com.example.socketio_chat_demo.data.model.Room
import com.example.socketio_chat_demo.data.model.User
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.utils.Constants
import com.example.socketio_chat_demo.utils.SharedPreferenceUtils
import com.example.socketio_chat_demo.utils.SocketHandler
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.*

class ChatViewModel(val application: Application, private val user: User) : ViewModel() {

    private var mSocket: Socket? = null
    private val gson = Gson()
    var messagesLiveData = MutableLiveData<DataResponse<MutableList<Message>>>()
    var typeMessageLiveData = MutableLiveData<String>()

    private val onConnect = Emitter.Listener {
        viewModelScope.launch {
            val data = Room(SharedPreferenceUtils.getCurrentUserId(application)!!, user.username)
            val jsonData = gson.toJson(data)
            mSocket!!.emit(Constants.SUBSCRIBE, jsonData)
        }
    }

    private val onUserWithChatHistory = Emitter.Listener {
        viewModelScope.launch {
            val listMessage = mutableListOf<Message>()
            val result = it[0] as JSONArray
            for (i in 0 until result.length()) {
                val message = gson.fromJson(result[i].toString(), Message::class.java)
                listMessage.add(message)
            }
            messagesLiveData.value = DataResponse.DataSuccessResponse(listMessage)
        }
    }

    private val onUpdateChat = Emitter.Listener {
        viewModelScope.launch {
            val message: Message = gson.fromJson(it[0].toString(), Message::class.java)
        }
    }

    private val onTyping = Emitter.Listener {
        viewModelScope.launch {

        }
    }

    private val onStopTyping = Emitter.Listener {
        viewModelScope.launch {

        }
    }

    init {
        SocketHandler.setSocket()
        mSocket = SocketHandler.getSocket()
        mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
        mSocket!!.on(Constants.USER_CHAT_HISTORY, onUserWithChatHistory)
        mSocket!!.on(Constants.UPDATE_CHAT, onUpdateChat)
        mSocket!!.on(Constants.TYPING, onTyping)
        mSocket!!.on(Constants.STOP_TYPING, onStopTyping)
        mSocket!!.connect()
    }

    fun onSendMessage() {
        viewModelScope.launch {
            val validateType = validate()
            if (validateType == Constants.ValidateType.ValidateDone) {
                val sendData =
                    Message(
                        SharedPreferenceUtils.getCurrentUserId(application)!!,
                        user.username,
                        typeMessageLiveData.value!!,
                        "false",
                        Calendar.getInstance().timeInMillis.toString(),
                        "text"
                    )
                val jsonData = gson.toJson(sendData)
                mSocket!!.emit(Constants.NEW_MESSAGE, jsonData)
                typeMessageLiveData.value = ""
            }
        }
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count > 0)
            mSocket!!.emit(Constants.TYPING, user.username)
        else
            mSocket!!.emit(Constants.STOP_TYPING, user.username)
    }

    fun onDisconnectSocket() {
//        val jsonData = Gson().toJson(room)
//        mSocket!!.emit(Constants.UNSUBSCRIBE, jsonData)
//        mSocket!!.disconnect()
    }

    private fun validate(): Constants.ValidateType {
        return if (typeMessageLiveData.value.isNullOrEmpty() || typeMessageLiveData.value == "") {
            Constants.ValidateType.EmptyMessage
        } else {
            Constants.ValidateType.ValidateDone
        }
    }

    class Factory(private val application: Application, private val user: User) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                return ChatViewModel(application, user) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}