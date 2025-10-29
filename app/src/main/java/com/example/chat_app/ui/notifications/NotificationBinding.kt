package com.example.chat_app.ui.notifications

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app.data.db.entity.UserInfo

@BindingAdapter("bind_notifications_list")
fun bindNotificationsList(listView: RecyclerView,item: List<UserInfo>?){
    item?.let { (listView.adapter as NotificationsListAdapter).submitList(item) }
}