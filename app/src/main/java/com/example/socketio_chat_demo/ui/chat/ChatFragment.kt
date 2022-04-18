package com.example.socketio_chat_demo.ui.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.socketio_chat_demo.R
import com.example.socketio_chat_demo.base.basefragment.BasePermissionRequestFragment
import com.example.socketio_chat_demo.data.model.Message
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.databinding.FragmentChatBinding
import com.example.socketio_chat_demo.ui.chat.adapter.ChatAdapter
import com.example.socketio_chat_demo.utils.ExoPlayerUtils
import com.example.socketio_chat_demo.utils.MediaPlayerUtils
import com.example.socketio_chat_demo.utils.Utils
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File

class ChatFragment : BasePermissionRequestFragment<FragmentChatBinding>(), View.OnClickListener {

    private val args: ChatFragmentArgs by navArgs()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    private val mExoPlayerUtils: ExoPlayerUtils by lazy {
        ExoPlayerUtils()
    }

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
            binding!!.rcChat.smoothScrollToPosition(chatAdapter.itemCount)
        }

        viewModel.messageLiveData.observe(this) {
            val result = (it as DataResponse.DataSuccessResponse).body
            chatAdapter.addSingleMessage(result)
            binding!!.rcChat.smoothScrollToPosition(chatAdapter.itemCount)
        }
    }

    private fun initRecycler() {
        chatAdapter = ChatAdapter(requireContext())
        binding!!.rcChat.adapter = chatAdapter
        chatAdapter.setListener(object : ChatAdapter.OnClickListener {
            override fun onVideoClick(playerView: PlayerView, message: Message) {
                val file = Utils.getMedia(requireContext(), message.messageContent, getString(R.string.video))
                mExoPlayerUtils.initPlayer(requireContext(), playerView, Uri.fromFile(file).toString())
            }

        })
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
                    val file = Utils.getFileFromUri(requireContext(), intent.data!!)!!
                    val mimeType = when {
                        Utils.getMimeType(
                            requireContext(),
                            intent.data!!
                        ) == getString(R.string.image_mimetype) -> {
                            getString(R.string.image)
                        }
                        Utils.getMimeType(
                            requireContext(),
                            intent.data!!
                        ) == getString(R.string.video_mimetype) -> {
                            getString(R.string.video)
                        }
                        else -> {
                            getString(R.string.audio)
                        }
                    }
                    viewModel.onUploadMedia(file, mimeType)
                }
            }
        }
}