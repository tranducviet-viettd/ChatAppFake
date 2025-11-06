package com.example.chat_app.ui.friends

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app.data.db.entity.User


@BindingAdapter("bind_friends_list")
fun bindFriendsList(recyclerView: RecyclerView,listFriends:List<User>?){
    listFriends?.let { (recyclerView.adapter as FriendsListAdapter).submitList(listFriends) }
}