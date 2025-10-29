package com.example.chat_app.data.db.entity


import com.google.firebase.database.PropertyName

data class User(
    @get:PropertyName("info") @set:PropertyName("info") var info:UserInfo = UserInfo(),
    @get:PropertyName("friends") @set:PropertyName("friends") var friends: HashMap<String, UserFriend> = HashMap(),
    @get:PropertyName("notifications") @set:PropertyName("notifications") var notifications: HashMap<String, UserNotification> = HashMap(),
    @get:PropertyName("sentRequests") @set:PropertyName("sentRequests") var sentRequests: HashMap<String, UserRequest> = HashMap()
)


data class UserInfo(
    @get:PropertyName("id") @set:PropertyName("id") var id:String="",
    @get:PropertyName("email") @set:PropertyName("email") var email:String="",
    @get:PropertyName("displayName") @set:PropertyName("displayName") var displayName:String="",
    @get:PropertyName("online") @set:PropertyName("online") var online: Boolean = false,
    @get:PropertyName("status") @set:PropertyName("status") var status: String = "a",
    @get:PropertyName("profileImageUrl") @set:PropertyName("profileImageUrl") var profileImageUrl : String = ""

    )

data class UserFriend(
    @get:PropertyName("userID") @set:PropertyName("userID") var userID : String =""
)

data class UserNotification(
    @get:PropertyName("userID") @set:PropertyName("userID") var userID: String = ""
)

data class UserRequest(
    @get:PropertyName("userID") @set:PropertyName("userID") var userID: String = ""
)