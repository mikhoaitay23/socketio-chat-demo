package com.example.socketio_chat_demo.ui.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socketio_chat_demo.data.model.Message
import com.example.socketio_chat_demo.databinding.ItemRcMessageMeBinding
import com.example.socketio_chat_demo.databinding.ItemRcMessageOtherBinding
import com.example.socketio_chat_demo.utils.SharedPreferenceUtils

class ChatAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listMessage = mutableListOf<Message>()

    fun addMessages(messages: MutableList<Message>) {
        listMessage.addAll(messages)
        notifyItemInserted(listMessage.size)
    }

    fun addSingleMessage(message: Message) {
        listMessage.add(0, message)
        notifyItemInserted(listMessage.indexOf(message))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER_MY_MESSAGE -> {
                val binding =
                    ItemRcMessageMeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                MessageMeViewHolder(binding)
            }
            else -> {
                val binding =
                    ItemRcMessageOtherBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                MessageOtherViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MY_MESSAGE -> (holder as MessageMeViewHolder).bind(position)
            VIEW_TYPE_USER_OTHER_MESSAGE -> (holder as MessageOtherViewHolder).bind(position)
        }
    }

    override fun getItemCount() = listMessage.size

    override fun getItemViewType(position: Int): Int {
        val message = listMessage[position]
        var isMyMessage = false
        if (message.sender == SharedPreferenceUtils.getCurrentUserId(context)) {
            isMyMessage = true
        }
        return if (isMyMessage) {
            VIEW_TYPE_USER_MY_MESSAGE
        } else {
            VIEW_TYPE_USER_OTHER_MESSAGE
        }
    }

    inner class MessageMeViewHolder(private val binding: ItemRcMessageMeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.messageModel = listMessage[position]
        }
    }

    inner class MessageOtherViewHolder(private val binding: ItemRcMessageOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.messageModel = listMessage[position]
        }
    }

    companion object {
        private const val VIEW_TYPE_USER_MY_MESSAGE = 10
        private const val VIEW_TYPE_USER_OTHER_MESSAGE = 11
    }
}