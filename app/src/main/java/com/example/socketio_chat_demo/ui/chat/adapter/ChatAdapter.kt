package com.example.socketio_chat_demo.ui.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socketio_chat_demo.R
import com.example.socketio_chat_demo.data.model.Message
import com.example.socketio_chat_demo.databinding.*
import com.example.socketio_chat_demo.utils.SharedPreferenceUtils
import com.google.android.exoplayer2.ui.PlayerView

class ChatAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listMessage = mutableListOf<Message>()
    private var mOnClickListener: OnClickListener? = null

    fun addMessages(messages: MutableList<Message>) {
        listMessage.addAll(messages)
        notifyItemInserted(listMessage.size)
    }

    fun addSingleMessage(message: Message) {
        listMessage.add(listMessage.size, message)
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
            VIEW_TYPE_USER_MY_VIDEO_MESSAGE -> {
                val binding = ItemRcMessageVideoMeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageVideoMeViewHolder(binding)
            }
            VIEW_TYPE_USER_OTHER_VIDEO_MESSAGE -> {
                val binding = ItemRcMessageVideoOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageVideoOtherViewHolder(binding)
            }
            VIEW_TYPE_USER_MY_AUDIO_MESSAGE -> {
                val binding = ItemRcMessageAudioMeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageAudioMeViewHolder(binding)
            }
            VIEW_TYPE_USER_OTHER_AUDIO_MESSAGE -> {
                val binding = ItemRcMessageAudioOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageAudioOtherViewHolder(binding)
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
            VIEW_TYPE_USER_MY_VIDEO_MESSAGE -> (holder as MessageVideoMeViewHolder).bind(position)
            VIEW_TYPE_USER_OTHER_VIDEO_MESSAGE -> (holder as MessageVideoOtherViewHolder).bind(
                position
            )
            VIEW_TYPE_USER_MY_AUDIO_MESSAGE -> (holder as MessageAudioMeViewHolder).bind(position)
            VIEW_TYPE_USER_OTHER_AUDIO_MESSAGE -> (holder as MessageAudioOtherViewHolder).bind(
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
        return if (message.type == context.getString(R.string.text)) {
            if (isMyMessage) {
                VIEW_TYPE_USER_MY_MESSAGE
            } else {
                VIEW_TYPE_USER_OTHER_MESSAGE
            }
        } else if (message.type == context.getString(R.string.image)) {
            if (isMyMessage) {
                VIEW_TYPE_USER_MY_IMAGE_MESSAGE
            } else {
                VIEW_TYPE_USER_OTHER_IMAGE_MESSAGE
            }
        } else if (message.type == context.getString(R.string.video)) {
            if (isMyMessage) {
                VIEW_TYPE_USER_MY_VIDEO_MESSAGE
            } else {
                VIEW_TYPE_USER_OTHER_VIDEO_MESSAGE
            }
        } else if (message.type == context.getString(R.string.audio)) {
            if (isMyMessage) {
                VIEW_TYPE_USER_MY_AUDIO_MESSAGE
            } else {
                VIEW_TYPE_USER_OTHER_AUDIO_MESSAGE
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

    inner class MessageVideoMeViewHolder(private val binding: ItemRcMessageVideoMeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val message = listMessage[position]
            binding.imgPlay.setOnClickListener {
                mOnClickListener?.onVideoClick(binding.imgSurfaceVideo, message)
                binding.imgPlay.visibility = View.GONE
            }
        }
    }

    inner class MessageVideoOtherViewHolder(private val binding: ItemRcMessageVideoOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val message = listMessage[position]
            binding.imgPlay.setOnClickListener {
                mOnClickListener?.onVideoClick(binding.imgSurfaceVideo, message)
                binding.imgPlay.visibility = View.GONE
            }
        }
    }

    inner class MessageAudioMeViewHolder(private val binding: ItemRcMessageAudioMeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

        }
    }

    inner class MessageAudioOtherViewHolder(private val binding: ItemRcMessageAudioOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

        }
    }

    inner class MessageOtherTypingViewHolder(private val binding: ItemRcMessageTypingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }

    fun setListener(onClickListener: OnClickListener) {
        mOnClickListener = onClickListener
    }

    interface OnClickListener {
        fun onVideoClick(playerView: PlayerView, message: Message)
        fun onAudioClick(message: Message)
    }

    companion object {
        private const val VIEW_TYPE_USER_MY_MESSAGE = 10
        private const val VIEW_TYPE_USER_OTHER_MESSAGE = 11
        private const val VIEW_TYPE_USER_MY_IMAGE_MESSAGE = 12
        private const val VIEW_TYPE_USER_OTHER_IMAGE_MESSAGE = 13
        private const val VIEW_TYPE_USER_MY_VIDEO_MESSAGE = 14
        private const val VIEW_TYPE_USER_OTHER_VIDEO_MESSAGE = 15
        private const val VIEW_TYPE_USER_MY_AUDIO_MESSAGE = 16
        private const val VIEW_TYPE_USER_OTHER_AUDIO_MESSAGE = 17
        private const val VIEW_TYPE_USER_OTHER_TYPING = 33
    }
}