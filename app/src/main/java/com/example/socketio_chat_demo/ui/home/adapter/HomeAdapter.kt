package com.example.socketio_chat_demo.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socketio_chat_demo.data.model.User
import com.example.socketio_chat_demo.databinding.ItemRcMembersBinding

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private val listUser = mutableListOf<User>()
    private var mOnClickListener: OnClickListener? = null

    fun addUsers(users: MutableList<User>) {
        listUser.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding =
            ItemRcMembersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = listUser.size

    inner class HomeViewHolder(val binding: ItemRcMembersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val user = listUser[position]
            binding.tvUsername.text = user.username
            binding.root.setOnClickListener {
                mOnClickListener?.onClickUserListener(user)
            }
        }
    }

    fun setListener(onClickListener: OnClickListener){
        mOnClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClickUserListener(user: User)
    }
}