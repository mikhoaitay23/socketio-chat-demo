package com.example.socketio_chat_demo.ui.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.socketio_chat_demo.data.model.Room
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.utils.Constants
import kotlinx.coroutines.launch

class HomeViewModel(val application: Application) : ViewModel() {

    var userNameLiveData = MutableLiveData<String>()
    var nameRoomLiveData = MutableLiveData<String>()
    val loginLiveData = MutableLiveData<DataResponse<Room?>>()

    init {

    }

    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}