package com.example.socketio_chat_demo.ui.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.socketio_chat_demo.data.model.Room
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.utils.Constants
import com.example.socketio_chat_demo.utils.SharedPreferenceUtils
import kotlinx.coroutines.launch

class LoginViewModel(private val application: Application) : ViewModel() {

    var usernameLiveData = MutableLiveData<String>()
    var nameRoomLiveData = MutableLiveData<String>()
    var mLoginLiveData = MutableLiveData<DataResponse<Room?>>()

    init {

    }

    fun onLogin() {
        viewModelScope.launch {
            val validateType = validate()
            if (validateType == Constants.ValidateType.ValidateDone) {
                mLoginLiveData.value = DataResponse.DataSuccessResponse(
                    Room(
                        usernameLiveData.value!!,
                        nameRoomLiveData.value!!
                    )
                )
                SharedPreferenceUtils.setCurrentUserId(application, usernameLiveData.value!!)
            } else {
                mLoginLiveData.value = DataResponse.DataSuccessResponse(
                    null
                )
            }
        }
    }

    private fun validate(): Constants.ValidateType {
        return if (usernameLiveData.value.isNullOrEmpty() || usernameLiveData.value == "") {
            Constants.ValidateType.EmptyUserName
        } else if (nameRoomLiveData.value.isNullOrEmpty() || nameRoomLiveData.value == "") {
            Constants.ValidateType.EmptyNameRoom
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