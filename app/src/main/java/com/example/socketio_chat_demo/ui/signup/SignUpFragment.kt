package com.example.socketio_chat_demo.ui.signup

import androidx.lifecycle.ViewModelProvider
import com.example.socketio_chat_demo.R
import com.example.socketio_chat_demo.base.basefragment.BaseFragment
import com.example.socketio_chat_demo.databinding.FragmentSignUpBinding

class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    private lateinit var viewModel: SignUpViewModel

    override fun getLayoutID() = R.layout.fragment_sign_up

    override fun initView() {

        binding!!.viewModel = viewModel
    }

    override fun initViewModel() {
        val factory = SignUpViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]
    }
}