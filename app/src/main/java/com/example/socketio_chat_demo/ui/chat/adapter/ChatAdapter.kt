package com.example.socketio_chat_demo.ui.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socketio_chat_demo.data.model.Message
import com.example.socketio_chat_demo.databinding.*
import com.example.socketio_chat_demo.utils.Constants
import com.example.socketio_chat_demo.utils.SharedPreferenceUtils

class ChatAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listMessage = mutableListOf<Message>()

    fun addMessages(messages: MutableList<Message>) {
        listMessage.addAll(messages)
        notifyItemInserted(listMessage.size)
    }

    fun addSingleMessage(message: Message) {
        listMessage.add(listMessage.size, message)
        notifyItemInserted(listMessage.indexOf(message))
    }

    fun removeMessageTyping() {
        listMessage.removeAt(listMessage.size - 2)
        notifyItemRemoved(listMessage.size - 2)
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
            VIEW_TYPE_USER_OTHER_MESSAGE -> {
                val binding =
                    ItemRcMessageOtherBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                MessageOtherViewHolder(binding)
            }
            VIEW_TYPE_USER_MY_IMAGE_MESSAGE -> {
                val binding = ItemRcMessageImageMeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageImageMeViewHolder(binding)
            }
            VIEW_TYPE_USER_OTHER_IMAGE_MESSAGE -> {
                val binding = ItemRcMessageImageOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageImageOtherViewHolder(binding)
            }
            else -> {
                val binding = ItemRcMessageTypingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageOtherTypingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MY_MESSAGE -> (holder as MessageMeViewHolder).bind(position)
            VIEW_TYPE_USER_OTHER_MESSAGE -> (holder as MessageOtherViewHolder).bind(position)
            VIEW_TYPE_USER_MY_IMAGE_MESSAGE -> (holder as MessageImageMeViewHolder).bind(position)
            VIEW_TYPE_USER_OTHER_IMAGE_MESSAGE -> (holder as MessageImageOtherViewHolder).bind(
                position
            )
            VIEW_TYPE_USER_OTHER_TYPING -> (holder as MessageOtherTypingViewHolder).bind()
        }
    }

    override fun getItemCount() = listMessage.size

    override fun getItemViewType(position: Int): Int {
        val message = listMessage[position]
        var isMyMessage = false
        if (message.userName == SharedPreferenceUtils.getCurrentUserId(context)) {
            isMyMessage = true
        }
        return if (message.type == Constants.TEXT_TYPE) {
            if (isMyMessage) {
                VIEW_TYPE_USER_MY_MESSAGE
            } else {
                VIEW_TYPE_USER_OTHER_MESSAGE
            }
        } else if (message.type == Constants.IMAGE_TYPE) {
            if (isMyMessage) {
                VIEW_TYPE_USER_MY_IMAGE_MESSAGE
            } else {
                VIEW_TYPE_USER_OTHER_IMAGE_MESSAGE
            }
        } else {
            VIEW_TYPE_USER_OTHER_TYPING
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

    inner class MessageImageMeViewHolder(private val binding: ItemRcMessageImageMeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.messageModel = listMessage[position]
        }
    }

    inner class MessageImageOtherViewHolder(private val binding: ItemRcMessageImageOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.messageModel = listMessage[position]
        }
    }

    inner class MessageOtherTypingViewHolder(private val binding: ItemRcMessageTypingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }

    companion object {
        private const val VIEW_TYPE_USER_MY_MESSAGE = 10
        private const val VIEW_TYPE_USER_OTHER_MESSAGE = 11
        private const val VIEW_TYPE_USER_MY_IMAGE_MESSAGE = 12
        private const val VIEW_TYPE_USER_OTHER_IMAGE_MESSAGE = 13
        private const val VIEW_TYPE_USER_OTHER_TYPING = 33
    }
}