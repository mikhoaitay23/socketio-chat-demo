package com.example.socketio_chat_demo.ui.login

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.socketio_chat_demo.R
import com.example.socketio_chat_demo.base.basefragment.BaseFragment
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private lateinit var viewModel: LoginViewModel

    override fun getLayoutID() = R.layout.fragment_login

    override fun initView() {
        binding!!.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_global_signUpFragment)
        }

        binding!!.viewModel = viewModel
    }

    override fun initViewModel() {
        val factory = LoginViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        viewModel.mLoginLiveData.observe(this) {
            val result = (it as DataResponse.DataSuccessResponse).body
            if (result != "") {
                val action = LoginFragmentDirections.actionGlobalHomeFragment()
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