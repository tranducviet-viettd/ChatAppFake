package com.example.chat_app.data.db.entity

import com.google.firebase.database.PropertyName
import java.util.Date

data class Message(
    @get:PropertyName("id") @set:PropertyName("id") var id: String ="",
    @get:PropertyName("senderID") @set:PropertyName("senderID") var senderID: String ="",
    @get:PropertyName("epochTimeMs") @set:PropertyName("epochTimeMs") var epochTimeMs: Long = Date().time,
    @get:PropertyName("text") @set:PropertyName("text") var text: String= "",
    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl") var imageUrl: String = "",
    @get:PropertyName("seen") @set:PropertyName("seen") var seen: Boolean=false
    )
