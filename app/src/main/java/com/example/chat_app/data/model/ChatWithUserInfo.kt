package com.example.chat_app.data.model

import com.example.chat_app.data.db.entity.Chat
import com.example.chat_app.data.db.entity.UserInfo


data class ChatWithUserInfo(
    var mChat: Chat,
    var mUserInfo: UserInfo
)