package com.example.chat_app.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app.data.db.entity.User
import com.example.chat_app.databinding.ListItemUserBinding

class UsersListAdapter internal constructor(private val viewModel: UsersViewModel): ListAdapter<(User), UsersListAdapter.ViewHolder>(UserDiffCallBack()){

    class ViewHolder(private val binding : ListItemUserBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(viewModel: UsersViewModel,user:User){
            binding.viewmodel=viewModel
            binding.user=user
            binding.executePendingBindings()
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val infalter= LayoutInflater.from(parent.context)
        val binding = ListItemUserBinding.inflate(infalter,parent,false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(viewModel,getItem(position))
    }

}

class UserDiffCallBack: DiffUtil.ItemCallback<User>(){
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