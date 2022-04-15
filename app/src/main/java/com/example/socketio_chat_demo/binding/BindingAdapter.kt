package com.example.socketio_chat_demo.binding

import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.socketio_chat_demo.utils.Utils
import io.socket.engineio.parser.Base64


@BindingAdapter("android:setTime")
fun setTime(textView: TextView, time: String?) {
    if (!time.isNullOrEmpty()) {
        textView.text = Utils.formatTime(time.toLong())
    }
}

@BindingAdapter("android:setImage")
fun setImage(imageView: ImageView, base64: String?) {
    if (!base64.isNullOrEmpty()) {
        val b: ByteArray = Base64.decode(base64, Base64.URL_SAFE)
        val bmp = BitmapFactory.decodeByteArray(b, 0, b.size)
        Glide.with(imageView.context).load(bmp).into(imageView)
    }
}