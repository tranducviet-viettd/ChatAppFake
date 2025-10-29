package com.example.chat_app.ui.users

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app.data.db.entity.User

@BindingAdapter("bind_users_list")
fun bindUsersList(recyclerView: RecyclerView,listUsers:List<User>?){
    listUsers?.let { (recyclerView.adapter as UsersListAdapter).submitList(listUsers) }
}