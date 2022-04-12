package com.example.socketio_chat_demo.ui.splash

import android.os.Handler
import android.os.Looper
import androidx.navigation.fragment.findNavController
import com.example.socketio_chat_demo.R
import com.example.socketio_chat_demo.base.basefragment.BaseFragment
import com.example.socketio_chat_demo.databinding.FragmentSplashBinding
import com.example.socketio_chat_demo.utils.SharedPreferenceUtils

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    override fun getLayoutID() = R.layout.fragment_splash

    override fun initView() {

    }

    override fun initViewModel() {
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_global_loginFragment)
        }, SPLASH_DELAY)
    }

    companion object {
        const val SPLASH_DELAY = 2000L
    }
}