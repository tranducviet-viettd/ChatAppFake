package com.example.chat_app.ui.chats

import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.chat_app.data.db.entity.Message
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app.R
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.remote.FirebaseDataSource
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.example.chat_app.data.model.ChatWithUserInfo

@BindingAdapter("bind_chats_list")
fun bindChatsList(listView: RecyclerView,chatsList:List<ChatWithUserInfo>?){
    Log.d("AddChats","Call bindChatsList")
    chatsList?.let {    (listView.adapter as ChatsListAdapter ).submitList(chatsList)}
}

@BindingAdapter("bind_chat_message_text","bind_chat_message_text_viewModel")
fun TextView.bindMessageYouToText(message:Message, viewModel: ChatsViewModel){
    if(message.senderID == viewModel.myUserID){
        if (message.imageUrl.isEmpty()){ this.text =  "You: "+message.text}
        else {
            this.text = "Bạn đã gửi 1 ảnh."
        }
    } else{
        if (message.imageUrl.isEmpty()){
            this.text = message.text
        }
        else {
            val db = DatabaseRepository()
            db.loadUser(message.senderID){result ->
                if(result is Result.Success){
                    this.text = result.data?.info?.displayName + " đã gửi 1 ảnh"
                }
            }

        }
    }
}

@BindingAdapter("bind_message","bind_message_textView","bind_message_view","bind_myUserID")
fun View.bindMessageSeen(message: Message, textView: TextView, view: View,myUserID:String){
    if(message.senderID != myUserID && !message.seen){
        view.visibility = View.VISIBLE
        textView.setTextAppearance(R.style.MessageNotSeen)
    }
    else{
        view.visibility=View.GONE
        textView.setTextAppearance(R.style.MessageSeen)
    }
}