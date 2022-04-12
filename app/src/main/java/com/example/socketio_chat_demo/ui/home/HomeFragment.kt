package com.example.socketio_chat_demo.ui.home

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.socketio_chat_demo.R
import com.example.socketio_chat_demo.base.basefragment.BaseFragment
import com.example.socketio_chat_demo.data.model.User
import com.example.socketio_chat_demo.data.response.DataResponse
import com.example.socketio_chat_demo.databinding.FragmentHomeBinding
import com.example.socketio_chat_demo.ui.home.adapter.HomeAdapter
import com.example.socketio_chat_demo.utils.SharedPreferenceUtils

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var mHomeAdapter: HomeAdapter

    override fun getLayoutID() = R.layout.fragment_home

    override fun initView() {
        initRecycler()
        binding!!.viewModel = viewModel
    }

    override fun initViewModel() {
        val factory = HomeViewModel.Factory(
            requireActivity().application,
            SharedPreferenceUtils.getCurrentUserId(requireContext())!!
        )
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        viewModel.membersLiveData.observe(this) {
            val result = (it as DataResponse.DataSuccessResponse).body
            if (!result.isNullOrEmpty()) {
                mHomeAdapter.addUsers(result)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMembers()
    }

    private fun initRecycler() {
        mHomeAdapter = HomeAdapter()
        binding!!.rcMembers.adapter = mHomeAdapter
        mHomeAdapter.setListener(object : HomeAdapter.OnClickListener {
            override fun onClickUserListener(user: User) {
                val action = HomeFragmentDirections.actionGlobalChatFragment(user)
                findNavController().navigate(action)
            }

        })
    }
}