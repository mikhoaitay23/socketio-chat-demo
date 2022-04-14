package com.example.socketio_chat_demo.ui.chat

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.socketio_chat_demo.R
import com.example.socketio_chat_demo.base.basefragment.BasePermissionRequestFragment
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.databinding.FragmentChatBinding
import com.example.socketio_chat_demo.ui.chat.adapter.ChatAdapter
import com.example.socketio_chat_demo.utils.Utils
import java.io.File

class ChatFragment : BasePermissionRequestFragment<FragmentChatBinding>(), View.OnClickListener {

    private val args: ChatFragmentArgs by navArgs()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter

    override fun getLayoutID() = R.layout.fragment_chat

    override fun initView() {

        binding!!.btnUpload.setOnClickListener(this)

        initRecycler()
        binding!!.viewModel = viewModel
    }

    override fun initViewModel() {
        val factory = ChatViewModel.Factory(requireActivity().application, args.roomModel)
        viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]

        viewModel.messagesLiveData.observe(this) {
            val result = (it as DataResponse.DataSuccessResponse).body
            chatAdapter.addMessages(result)
        }

        viewModel.messageLiveData.observe(this) {
            val result = (it as DataResponse.DataSuccessResponse).body
            chatAdapter.addSingleMessage(result)
            binding!!.rcChat.smoothScrollToPosition(chatAdapter.itemCount)
        }

        viewModel.messageTypingLiveData.observe(this) {
            val result = (it as DataResponse.DataSuccessResponse).body
            if (result.messageContent == "stop typing") {
                chatAdapter.removeMessageTyping()
            } else {
                chatAdapter.addSingleMessage(result)
            }
            binding!!.rcChat.smoothScrollToPosition(chatAdapter.itemCount)
        }
    }

    private fun initRecycler() {
        chatAdapter = ChatAdapter(requireContext())
        binding!!.rcChat.adapter = chatAdapter
    }

    override fun setupWhenPermissionGranted() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        getMedia.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDisconnectSocket()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding!!.btnUpload) {
            if (Utils.storagePermissionGrant(requireContext())) {
                val intent = Intent()
                intent.type = "*/*"
                intent.action = Intent.ACTION_GET_CONTENT
                getMedia.launch(intent)
            } else {
                requestPermission()
            }
        }
    }

    private var getMedia =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val file = File(Utils.getRealPathFromURI(requireContext(), intent.data!!))
                    viewModel.onUploadImage(file)
                }
            }
        }
}