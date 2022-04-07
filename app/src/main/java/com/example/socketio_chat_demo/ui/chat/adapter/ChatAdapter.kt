package com.example.socketio_chat_demo.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socketio_chat_demo.data.model.Message
import com.example.socketio_chat_demo.databinding.ItemRcChatBinding

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    val listMessage = mutableListOf<Message>()

    fun addMessage(message: Message) {
        listMessage.add(message)
        notifyItemInserted(listMessage.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatViewHolder {
        val binding =
            ItemRcChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatAdapter.ChatViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = listMessage.size

    inner class ChatViewHolder(private val binding: ItemRcChatBinding) :
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