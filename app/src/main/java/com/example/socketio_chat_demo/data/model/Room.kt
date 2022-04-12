package com.example.socketio_chat_demo.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Room (var sender: String, var receiver: String): Parcelable