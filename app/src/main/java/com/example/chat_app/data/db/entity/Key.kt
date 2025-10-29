package com.example.chat_app.data.db.entity

import com.google.firebase.database.PropertyName

data class Key(
    @get:PropertyName("key") @set:PropertyName("key") var key:String = "1",
    @get:PropertyName("active") @set:PropertyName("active") var active : Boolean= true
)