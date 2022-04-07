package com.example.socketio_chat_demo.ui.signup

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.socketio_chat_demo.data.model.User
import com.example.socketio_chat_demo.utils.Constants
import com.example.socketio_chat_demo.utils.SocketHandler
import com.google.gson.Gson
import io.socket.client.Socket

class SignUpViewModel(private val application: Application) : ViewModel() {

    private var mSocket: Socket? = null
    var userNameLiveData = MutableLiveData<String>()
    private val gson = Gson()

    init {
        mSocket = SocketHandler.getSocket()
    }

    fun onSignUp() {
        val validateType = validate()
        if (validateType == Constants.ValidateType.ValidateDone){
            val data = User(userNameLiveData.value!!)
            val jsonData = gson.toJson(data)
            mSocket!!.emit(Constants.SIGN_UP, jsonData)
        }
    }

    fun validate(): Constants.ValidateType {
        return if (userNameLiveData.value.isNullOrEmpty() || userNameLiveData.value == "") {
            Constants.ValidateType.EmptyUserName
        } else {
            Constants.ValidateType.ValidateDone
        }
    }

    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                return SignUpViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}