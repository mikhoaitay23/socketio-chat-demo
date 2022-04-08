package com.example.socketio_chat_demo.ui.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.socketio_chat_demo.data.model.SendMessage
import com.example.socketio_chat_demo.data.model.User
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.utils.Constants
import com.example.socketio_chat_demo.utils.SocketHandler
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.launch

class LoginViewModel(private val application: Application) : ViewModel() {

    private var mSocket: Socket? = null
    private var gson = Gson()
    var usernameLiveData = MutableLiveData<String>()
    var mLoginLiveData = MutableLiveData<DataResponse<String>>()

    private val onLogin = Emitter.Listener {
        viewModelScope.launch {
            val username = it[0] as String
            mLoginLiveData.value = DataResponse.DataSuccessResponse(username)
        }
    }

    init {
        mSocket = SocketHandler.getSocket()
        mSocket!!.on(Constants.ON_LOGIN, onLogin)
    }

    fun onLogin() {
        viewModelScope.launch {
            val validateType = validate()
            if (validateType == Constants.ValidateType.ValidateDone) {
                val sendData = User(usernameLiveData.value!!)
                val jsonData = gson.toJson(sendData)
                mSocket!!.emit(Constants.LOGIN, jsonData)
            }
        }
    }

    private fun validate(): Constants.ValidateType {
        return if (usernameLiveData.value.isNullOrEmpty() || usernameLiveData.value == "") {
            Constants.ValidateType.EmptyUserName
        } else {
            Constants.ValidateType.ValidateDone
        }
    }

    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}