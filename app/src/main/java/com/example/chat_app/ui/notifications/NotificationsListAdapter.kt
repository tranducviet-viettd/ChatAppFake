package com.example.chat_app.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app.data.db.entity.UserInfo
import com.example.chat_app.databinding.ListItemNotificationBinding


class NotificationsListAdapter internal constructor(private val viewModel: NotificationsViewModel):
        ListAdapter<UserInfo, NotificationsListAdapter.ViewHolder>(UserInfoDiffCallBack()){

    class ViewHolder(private val binding: ListItemNotificationBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(viewModel: NotificationsViewModel,userInfo: UserInfo){
            binding.viewmodel=viewModel
            binding.userinfo=userInfo
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemNotificationBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(viewModel,getItem(position))
    }


        }

class UserInfoDiffCallBack : DiffUtil.ItemCallback<UserInfo>(){
    override fun areItemsTheSame(
        oldItem: UserInfo,
        newItem: UserInfo
    ): Boolean {
        return oldItem==newItem
    }

    override fun areContentsTheSame(
        oldItem: UserInfo,
        newItem: UserInfo
    ): Boolean {
        return oldItem.id==newItem.id
    }

}
