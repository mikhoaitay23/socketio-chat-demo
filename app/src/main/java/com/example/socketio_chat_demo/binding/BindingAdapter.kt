package com.example.socketio_chat_demo.binding

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.socketio_chat_demo.utils.Utils

@BindingAdapter("android:setTime")
fun setTime(textView: TextView, time: String?) {
    if (!time.isNullOrEmpty()) {
        textView.text = Utils.formatTime(time.toLong())
    }
}

@BindingAdapter("android:setImage")
fun setImage(imageView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(imageView.context).load(url).into(imageView)
    }
}