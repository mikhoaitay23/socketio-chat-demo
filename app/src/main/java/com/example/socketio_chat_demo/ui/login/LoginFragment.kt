package com.example.socketio_chat_demo.ui.login

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.socketio_chat_demo.R
import com.example.socketio_chat_demo.base.basefragment.BaseFragment
import com.example.socketio_chat_demo.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private lateinit var viewModel: LoginViewModel

    override fun getLayoutID() = R.layout.fragment_login

    override fun initView() {
        binding!!.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_global_signUpFragment)
        }
    }

    override fun initViewModel() {
        val factory = LoginViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }
}