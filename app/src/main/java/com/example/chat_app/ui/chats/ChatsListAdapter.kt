package com.example.chat_app.ui.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app.data.model.ChatWithUserInfo
import com.example.chat_app.databinding.ListItemChatBinding
import com.example.chat_app.data.db.entity.Chat

class ChatsListAdapter internal constructor(private val viewModel: ChatsViewModel,private val lifecycleOwner: LifecycleOwner) : ListAdapter<(ChatWithUserInfo),ChatsListAdapter.ViewHolder>(ChatDiffCallback()){

    class ViewHolder(private val binding : ListItemChatBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(viewModel: ChatsViewModel , item : ChatWithUserInfo,lifecycleOwner: LifecycleOwner){
            binding.viewmodel=viewModel
            binding.chatwithuserinfo=item
            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemChatBinding.inflate(layoutInflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(viewModel,getItem(position),lifecycleOwner)
    }

}

class ChatDiffCallback: DiffUtil.ItemCallback<ChatWithUserInfo>(){
    override fun areItemsTheSame(oldItem: ChatWithUserInfo, newItem: ChatWithUserInfo): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ChatWithUserInfo, newItem: ChatWithUserInfo): Boolean {
        return oldItem.mChat.info.id==newItem.mChat.info.id
    }


}