package com.example.chat_app.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chat_app.data.Event
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.Chat
import com.example.chat_app.data.db.entity.Message
import com.example.chat_app.data.db.entity.User
import com.example.chat_app.data.db.entity.UserFriend
import com.example.chat_app.data.db.entity.UserInfo
import com.example.chat_app.data.db.entity.UserNotification
import com.example.chat_app.data.db.entity.UserRequest
import com.example.chat_app.data.db.remote.FirebaseReferenceValueObserver
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.example.chat_app.data.model.ChatWithUserInfo
import com.example.chat_app.ui.DefaultViewModel
import com.example.chat_app.util.convertTwoUserIDs


class ProfileViewModelFactory(private val myUserID: String,private val userID:String): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(myUserID,userID) as T
    }
}

enum class LayoutState{
    IS_FRIEND,NOT_FRIEND,ACCEPT_DECLINE,REQUEST_SENT
}

class ProfileViewModel(private val myUserID: String,private val userID: String): DefaultViewModel() {

    private val dbRepository= DatabaseRepository()
    private val firebaseReferenceValueObserver= FirebaseReferenceValueObserver()
    private val _myUser= MutableLiveData<User>()
    private val _otherUser= MutableLiveData<User>()

    val otherUser: LiveData<User> = _otherUser
    val layoutState = MediatorLiveData<LayoutState>()

    private val _selectChat = MutableLiveData<Event<UserInfo>>()
    var selectChat: LiveData<Event<UserInfo>> = _selectChat


    init{
        Log.d("CheckLifecycle", "ProfileViewModel: ProfileViewModel created")

        layoutState.addSource(_myUser){
            updateLayoutState(it,_otherUser.value)
        }
        setupProfile()
    }
    private fun updateLayoutState(myUser : User?,otherUser:User?){
        if(myUser!=null && otherUser != null){
            layoutState.value = when{
                myUser.friends[otherUser.info.id]!=null -> LayoutState.IS_FRIEND
                myUser.notifications[otherUser.info.id]!=null -> LayoutState.ACCEPT_DECLINE
                myUser.sentRequests[otherUser.info.id]!=null -> LayoutState.REQUEST_SENT
                else -> LayoutState.NOT_FRIEND
            }
        }
    }
    private fun setupProfile(){
        dbRepository.loadUser(userID){ result ->
            onResult(_otherUser,result)
            if(result is Result.Success){
                dbRepository.loadAndObserveUser(myUserID,firebaseReferenceValueObserver){result2 ->
                    onResult(_myUser,result2)
                }
            }

        }
    }

    fun addFriendPressed(){
        dbRepository.updateNewSentRequest(myUserID, UserRequest(_otherUser.value!!.info.id))
        dbRepository.updateNewNotification(_otherUser.value!!.info.id, UserNotification(myUserID))
    }

    fun removeFriendPressed(){
        dbRepository.removeFriend(myUserID,_otherUser.value!!.info.id)
        dbRepository.removeChat(convertTwoUserIDs(myUserID,_otherUser.value!!.info.id))
        dbRepository.removeMessage(convertTwoUserIDs(myUserID,_otherUser.value!!.info.id))
    }

    fun chatPressed(){
            _selectChat.value = Event(UserInfo(id = userID))

    }

    fun acceptFriendRequestPressed(){
        dbRepository.updateNewFriend(UserFriend(myUserID), UserFriend(_otherUser.value!!.info.id))

        val newChat = Chat().apply {
            info.id=convertTwoUserIDs(myUserID,_otherUser.value!!.info.id)
            lastMessage= Message(seen = true, text = "Try say hi!")
        }

        dbRepository.updateNewChat(newChat)
        dbRepository.removeNotification(myUserID,_otherUser.value!!.info.id)
        dbRepository.removeSentRequest(_otherUser.value!!.info.id,myUserID)
    }

    fun declineFriendRequestPressed(){
        dbRepository.removeNotification(myUserID,_otherUser.value!!.info.id)
        dbRepository.removeSentRequest(_otherUser.value!!.info.id,myUserID)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CheckLifecycle", "ProfileViewModel: ProfileViewModel onCleared called")
    }

}