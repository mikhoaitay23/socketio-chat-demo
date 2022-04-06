package com.example.socketio_chat_demo.ui.home

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.socketio_chat_demo.R
import com.example.socketio_chat_demo.base.basefragment.BaseFragment
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.databinding.FragmentHomeBinding
import com.example.socketio_chat_demo.utils.Constants

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var viewModel: HomeViewModel

    override fun getLayoutID() = R.layout.fragment_home

    override fun initView() {

        binding!!.viewModel = viewModel
    }

    override fun initViewModel() {
        val factory = HomeViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        viewModel.loginLiveData.observe(this) {
            val result = (it as DataResponse.DataSuccessResponse).body
            if (result != null) {
                val action = HomeFragmentDirections.actionGlobalChatFragment(result)
                findNavController().navigate(action)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.empty_input),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}