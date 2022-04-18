package com.example.socketio_chat_demo.ui.chat

import android.app.Application
import android.util.Base64
import androidx.lifecycle.*
import com.example.socketio_chat_demo.data.model.LoadDataStatus
import com.example.socketio_chat_demo.data.model.Message
import com.example.socketio_chat_demo.data.model.Room
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.utils.Constants
import com.example.socketio_chat_demo.utils.SocketHandler
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.File
import java.io.FileInputStream

class ChatViewModel(private val application: Application, private val room: Room) : ViewModel() {

    private var mSocket: Socket? = null
    private val gson = Gson()
    var messagesLiveData = MutableLiveData<DataResponse<MutableList<Message>>>()
    var messageLiveData = MutableLiveData<DataResponse<Message>>()
    var messageTypingLiveData = MutableLiveData<DataResponse<Message>>()
    var typeMessageLiveData = MutableLiveData<String>()

    private val onConnect = Emitter.Listener {
        viewModelScope.launch {
            val data = room
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
            messageLiveData.value = DataResponse.DataSuccessResponse(message)
        }
    }

    private val onTyping = Emitter.Listener {
        viewModelScope.launch {
            val message = Message(room.userName, "on typing", room.roomName, "typing")
            messageTypingLiveData.value = DataResponse.DataSuccessResponse(message)
        }
    }

    private val onStopTyping = Emitter.Listener {
        viewModelScope.launch {
            messageTypingLiveData.value = DataResponse.DataErrorResponse()
        }
    }

    init {
        SocketHandler.setSocket()
        mSocket = SocketHandler.getSocket()
        mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
        mSocket!!.once(Constants.USER_CHAT_HISTORY, onUserWithChatHistory)
        mSocket!!.on(Constants.TYPING, onTyping)
        mSocket!!.on(Constants.STOP_TYPING, onStopTyping)
        mSocket!!.on(Constants.UPDATE_CHAT, onUpdateChat)
        mSocket!!.connect()
        messageTypingLiveData.value = DataResponse.DataEmptyResponse()
    }

    fun onSendMessage() {
        viewModelScope.launch {
            val validateType = validate()
            if (validateType == Constants.ValidateType.ValidateDone) {
                val sendData =
                    Message(
                        room.userName,
                        typeMessageLiveData.value!!,
                        room.roomName,
                        "text"
                    )
                val jsonData = gson.toJson(sendData)
                typeMessageLiveData.value = ""
                mSocket!!.emit(Constants.NEW_MESSAGE, jsonData)
            }
        }
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count > 0)
            mSocket!!.emit(Constants.TYPING, room.roomName)
        else
            mSocket!!.emit(Constants.STOP_TYPING, room.roomName)
    }

    fun onUploadMedia(file: File, type: String) {
        val fis = FileInputStream(file)
        val imgByte = ByteArray(file.length().toInt())
        fis.read(imgByte)
        val encodedString = Base64.encodeToString(imgByte, Base64.URL_SAFE)

        val sendData =
            Message(
                room.userName,
                encodedString,
                room.roomName,
                type
            )
        val jsonData = gson.toJson(sendData)
        mSocket!!.emit(Constants.UPLOAD_MEDIA, jsonData)
    }

    fun onDisconnectSocket() {
//        val jsonData = Gson().toJson(room)
//        mSocket!!.emit(Constants.UNSUBSCRIBE, jsonData)
//        mSocket!!.disconnect()
    }

    val isTyping: LiveData<Boolean> = Transformations.map(messageTypingLiveData){
        it.loadDataStatus == LoadDataStatus.SUCCESS
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