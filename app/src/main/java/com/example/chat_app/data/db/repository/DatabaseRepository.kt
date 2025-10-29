package com.example.chat_app.data.db.repository

import android.app.Notification
import android.util.Log
import androidx.compose.ui.geometry.Rect
import com.example.chat_app.data.EventObserver
import com.example.chat_app.data.db.entity.User
import com.example.chat_app.data.db.entity.UserFriend
import com.example.chat_app.data.db.remote.FirebaseDataSource
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.Chat
import com.example.chat_app.data.db.entity.Key
import com.example.chat_app.data.db.entity.Message
import com.example.chat_app.data.db.entity.UserInfo
import com.example.chat_app.data.db.entity.UserNotification
import com.example.chat_app.data.db.entity.UserRequest
import com.example.chat_app.data.db.remote.FirebaseInstallation
import com.example.chat_app.data.db.remote.FirebaseReferenceChildObserver
import com.example.chat_app.data.db.remote.FirebaseReferenceValueObserver
import com.example.chat_app.util.wrapSnapshotToArrayList
import com.example.chat_app.util.wrapSnapshotToClass

class DatabaseRepository {
    private val firebaseDataSource = FirebaseDataSource()
    private val firebaseInstallation= FirebaseInstallation()

    fun getID(b : (Result<String>) -> Unit){
        firebaseInstallation.getID(b)
    }

    fun updateNewUser(user:User){
        firebaseDataSource.updateNewUser(user)
    }

    fun updateNewSentRequest(myUserID: String,userRequest: UserRequest){
        firebaseDataSource.updateNewSentRequest(myUserID,userRequest)

    }

    fun updateNewNotification(userID: String,userNotification: UserNotification){
        firebaseDataSource.updateNewNotification(userID,userNotification)
    }

    fun updateNewFriend(userFriend: UserFriend,userFriend2: UserFriend){
        Log.d("AddChats","Call updateNewFriend")
        firebaseDataSource.updateNewFriend(userFriend,userFriend2)
    }

    fun updateNewChat(chat: Chat){
        Log.d("AddChats","Call updateNewChat")
        firebaseDataSource.updateNewChat(chat)
    }

    fun updateNewStatus(userID: String,status: String){
        firebaseDataSource.updateNewStatus(userID,status)
    }

    fun updateNewKey(deviceID:String,key: Key){
        firebaseDataSource.updateNewKey(deviceID,key)
    }

    fun updateNotActiveKey(deviceID: String){
        firebaseDataSource.updateNotActiveKey(deviceID)
    }

    fun updateProfileImageUrl(userID: String,url: String){
        firebaseDataSource.updateProfileImageUrl(userID,url)
    }

    fun updateLastMessage(chatID: String,message: Message){
        firebaseDataSource.updateChatLastMessage(chatID,message)
    }

    fun updateNewMessage(chatID: String,userID: String,otherID:String,message: Message){
        firebaseDataSource.updateNewMessage(chatID,userID,otherID,message)
    }
    fun removeFriend(myUserID: String,userID: String){
        firebaseDataSource.removeFriend(myUserID,userID)
    }

    fun removeAllFriends(userID: String){
        firebaseDataSource.removeAllFriends(userID)
    }

    fun removeChat(chatID: String){
        firebaseDataSource.removeChat(chatID)
    }

    fun removeKey(deviceID: String){
        firebaseDataSource.removeKey(deviceID)
    }

    fun removeMessage(messageID:String){
        firebaseDataSource.removeMessage(messageID)
    }

    fun removeOneMessageOneUser(chatID: String,userID: String,messageID: String){
        firebaseDataSource.removeOneMessageOneUser(chatID,userID,messageID)
    }

    fun undoMessage(chatID: String,userID: String,otherID:String,messageID: String,message: Message){
        firebaseDataSource.undoMessage(chatID,userID,otherID,messageID,message)
    }
    fun removeNotification(myUserID: String,userID: String){
        firebaseDataSource.removeNotification(myUserID,userID)
    }

    fun removeSentRequest(userID: String,myUserID: String){
        firebaseDataSource.removeSentRequest(userID,myUserID)
    }

    fun loadFriends(myUserID:String, b : (Result<MutableList<UserFriend>>) -> Unit){
        b.invoke(Result.Loading)
        firebaseDataSource.loadFriendsTask(myUserID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToArrayList(UserFriend::class.java,it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadUserInfo(userID:String, b :(Result<UserInfo>) -> Unit){
        b.invoke(Result.Loading)
        firebaseDataSource.loadUserInfoTask(userID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(UserInfo::class.java,it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadUsers(b: (Result<MutableList<User>>) -> Unit){
        b.invoke(Result.Loading)
        firebaseDataSource.loadUsersTask().addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToArrayList(User::class.java,it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }

    }

    fun loadChat(chatID: String, b: (Result<Chat>) -> Unit){
        b.invoke(Result.Loading)
        firebaseDataSource.loadChatTask(chatID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(Chat::class.java,it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadUser(userID:String,b: (Result<User>) -> Unit){
        b.invoke(Result.Loading)
        firebaseDataSource.loadUserTask(userID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(User::class.java,it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadKey(deviceID: String,b: (Result<Key>) -> Unit){
        b.invoke(Result.Loading)
        firebaseDataSource.loadKeyTask(deviceID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(Key::class.java,it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadNotifications(myUserID: String,b: (Result<MutableList<UserNotification>>)-> Unit){
        b.invoke(Result.Loading)
        firebaseDataSource.loadNotifications(myUserID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToArrayList(UserNotification::class.java,it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadAndObservseChat(chatID: String,observer: FirebaseReferenceValueObserver,b : (Result<Chat>) -> Unit){
        firebaseDataSource.attachChatObserver(Chat::class.java,observer,chatID,b)
    }

    fun loadAndObserveUser(userID:String,observer: FirebaseReferenceValueObserver,b:(Result<User>) -> Unit){
        firebaseDataSource.attachUserObserver(User::class.java,observer,userID,b)
    }

    fun loadAndObserveUserInfo(userID: String,observer: FirebaseReferenceValueObserver,b: (Result<UserInfo>) -> Unit){
        firebaseDataSource.attachUserInfoObserver(UserInfo::class.java,observer,userID,b)
    }

    fun loadAndObserveMessage(messageID: String, userID: String, observer: FirebaseReferenceChildObserver, onAdded:(Result<Message>) -> Unit,onChanged:(Result<Message>) -> Unit, onRemove:(Result<Message>) -> Unit){
        firebaseDataSource.attachMessageObserver(Message::class.java,observer,messageID,userID,onAdded,onChanged,onRemove)
    }

    fun loadAndObserveNotification(myUserID: String, observer: FirebaseReferenceValueObserver, b: (Result<MutableList<UserNotification>>) -> Unit){
        firebaseDataSource.attachNotificationObserver(UserNotification::class.java,observer,myUserID,b)
    }

    fun loadAndObserverFriend(myUserID: String, observer: FirebaseReferenceChildObserver, onAdded:(Result<UserFriend>) -> Unit, onChanged:(Result<UserFriend>) -> Unit,onRemove:(Result<UserFriend>) -> Unit){
        Log.d("AddChats","Call loadAndObserveFriends")
        firebaseDataSource.attachFriendObserver(UserFriend::class.java,observer,myUserID,onAdded,onChanged,onRemove)
    }
}