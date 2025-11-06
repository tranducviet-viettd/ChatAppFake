package com.example.chat_app.ui.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app.data.db.entity.User
import com.example.chat_app.databinding.ListItemFriendsBinding
import com.example.chat_app.databinding.ListItemUserBinding

import com.example.chat_app.ui.users.UserDiffCallBack
import com.example.chat_app.ui.users.UsersListAdapter
import com.example.chat_app.ui.users.UsersListAdapter.ViewHolder


class FriendsListAdapter internal constructor(private val viewmodel: FriendsViewModel): ListAdapter<(User), FriendsListAdapter.ViewHolder>(FriendDiffCallBack()){

    class ViewHolder(private val binding : ListItemFriendsBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(viewModel: FriendsViewModel,user:User){
            binding.viewmodel=viewModel
            binding.user=user
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendsListAdapter.ViewHolder {
        val infalter= LayoutInflater.from(parent.context)
        val binding = ListItemFriendsBinding.inflate(infalter,parent,false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: FriendsListAdapter.ViewHolder,
        position: Int
    ) {
        holder.bind(viewmodel,getItem(position))
    }





}

class FriendDiffCallBack: DiffUtil.ItemCallback<User>(){
    override fun areItemsTheSame(
        oldItem: User,
        newItem: User
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: User,
        newItem: User
    ): Boolean {
        return oldItem.info.id==newItem.info.id
    }

}