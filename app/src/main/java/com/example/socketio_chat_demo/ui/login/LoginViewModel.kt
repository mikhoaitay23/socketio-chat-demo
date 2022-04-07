package com.example.socketio_chat_demo.ui.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoginViewModel(private val application: Application): ViewModel() {


    init {

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