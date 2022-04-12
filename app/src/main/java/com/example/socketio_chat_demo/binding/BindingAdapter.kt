package com.example.socketio_chat_demo.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.socketio_chat_demo.utils.Utils

@BindingAdapter("android:setTime")
fun setTime(textView: TextView, time: String?) {
    if (!time.isNullOrEmpty()) {
        textView.text = Utils.formatTime(time.toLong())
    }
}