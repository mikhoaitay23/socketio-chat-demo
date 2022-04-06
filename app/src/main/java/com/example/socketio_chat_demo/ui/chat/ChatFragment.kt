package com.example.socketio_chat_demo.ui.chat

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.socketio_chat_demo.R
import com.example.socketio_chat_demo.base.basefragment.BasePermissionRequestFragment
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.databinding.FragmentChatBinding
import com.example.socketio_chat_demo.ui.chat.adapter.ChatAdapter

class ChatFragment : BasePermissionRequestFragment<FragmentChatBinding>() {

    private val args: ChatFragmentArgs by navArgs()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter

    override fun getLayoutID() = R.layout.fragment_chat

    override fun initView() {

        initRecycler()
        binding!!.viewModel = viewModel
    }

    override fun initViewModel() {
        val factory = ChatViewModel.Factory(requireActivity().application, args.currentRoom)
        viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]

        viewModel.messageLiveData.observe(this) {
            val result = (it as DataResponse.DataSuccessResponse).body
            chatAdapter.addMessage(result)
        }
    }

    private fun initRecycler() {
        chatAdapter = ChatAdapter()
        binding!!.rcChat.adapter = chatAdapter
    }

    override fun setupWhenPermissionGranted() {

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDisconnectSocket()
    }
}