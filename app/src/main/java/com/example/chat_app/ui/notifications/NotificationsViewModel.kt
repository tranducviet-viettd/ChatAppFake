package com.example.chat_app.ui.notifications

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.Chat
import com.example.chat_app.data.db.entity.Message
import com.example.chat_app.data.db.entity.UserFriend
import com.example.chat_app.data.db.entity.UserInfo
import com.example.chat_app.data.db.entity.UserNotification
import com.example.chat_app.data.db.remote.FirebaseReferenceValueObserver
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.example.chat_app.ui.DefaultViewModel
import com.example.chat_app.util.addNewItem
import com.example.chat_app.util.addNewItem2
import com.example.chat_app.util.convertTwoUserIDs
import com.example.chat_app.util.removeItem


class NotificationsViewModelFactory(private val myUserID: String): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotificationsViewModel(myUserID) as T
    }
}
class NotificationsViewModel(private val myUserID: String): DefaultViewModel() {

    private val dbRepository = DatabaseRepository()
    private val updateUserInfo = MutableLiveData<UserInfo>()
    private val userNotificationList = MutableLiveData<MutableList<UserNotification>>()

    private val firebaseReferenceValueObserver= FirebaseReferenceValueObserver()
    val usersInfoList = MediatorLiveData<MutableList<UserInfo>>()

    init {
        usersInfoList.addSource(updateUserInfo){
            usersInfoList.addNewItem2(it, {a -> a.id})
        }
        loadNotifications()
    }

    private fun loadNotifications(){
        dbRepository.loadAndObserveNotification(myUserID,firebaseReferenceValueObserver) { result ->

            onResult(userNotificationList,result)
            if(result is Result.Success){
                    result.data?.forEach { loadUserInfo(it) }
            }

        }
    }

    private fun loadUserInfo(userNotification: UserNotification){
        dbRepository.loadUserInfo(userNotification.userID){ result ->
            onResult(updateUserInfo,result)


        }
    }

    private fun updateNotification(otherInfo: UserInfo,removeOnly: Boolean){
        val userNotification = userNotificationList.value?.find { it.userID ==otherInfo.id}

        if(userNotification!=null){
            if(removeOnly) {
                Log.d("AddChats","Find notification")
                dbRepository.updateNewFriend(UserFriend(myUserID), UserFriend(otherInfo.id))
                Log.d("AddChats","updateFriend Success")
                val newChat = Chat().apply {
                    info.id = convertTwoUserIDs(myUserID, otherInfo.id)
                    lastMessage = Message(seen = true, text = "Say Helo")
                }
                Log.d("AddChats","Create newChat:$newChat")
                dbRepository.updateNewChat(newChat)
            }
                dbRepository.removeNotification(myUserID,otherInfo.id)
                dbRepository.removeSentRequest(otherInfo.id,myUserID)
                usersInfoList.removeItem(otherInfo)
                userNotificationList.removeItem(userNotification)


        }


    }

    fun acceptNotificationPressed(otherInfo: UserInfo){
        Log.d("AddChats","Call Success")
        updateNotification(otherInfo,true)
    }

    fun declineNotificationPressed(otherInfo: UserInfo){
        updateNotification(otherInfo,false)
    }
}