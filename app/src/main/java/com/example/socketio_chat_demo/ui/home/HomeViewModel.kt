package com.example.socketio_chat_demo.ui.home

import android.R.attr.data
import android.app.Application
import android.graphics.ColorSpace.Model
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.socketio_chat_demo.data.model.User
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.utils.Constants
import com.example.socketio_chat_demo.utils.SocketHandler
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.launch
import org.json.JSONArray


class HomeViewModel(val application: Application, private val username: String) : ViewModel() {

    private var mSocket: Socket? = null
    var membersLiveData = MutableLiveData<DataResponse<MutableList<User>>>()
    private val gson = Gson()

    private val onGetMembers = Emitter.Listener {
        viewModelScope.launch {
            val listUsername = mutableListOf<User>()
            val result = it[0] as JSONArray
            for (i in 0 until result.length()) {
                val user = gson.fromJson(result[i].toString(), User::class.java)
                if (user.username != username)
                    listUsername.add(user)
            }
            membersLiveData.value = DataResponse.DataSuccessResponse(listUsername)
        }
    }

    init {
        mSocket = SocketHandler.getSocket()
        mSocket!!.on(Constants.GET_MEMBERS, onGetMembers)
    }

    fun getMembers(){
        mSocket!!.emit(Constants.MEMBERS)
    }

    class Factory(private val application: Application, private val username: String) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(application, username) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}