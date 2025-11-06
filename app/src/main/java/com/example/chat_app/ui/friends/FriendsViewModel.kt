package com.example.chat_app.ui.friends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chat_app.data.Event
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.Message
import com.example.chat_app.data.db.entity.User
import com.example.chat_app.data.db.entity.UserFriend
import com.example.chat_app.data.db.remote.FirebaseReferenceChildObserver
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.example.chat_app.ui.DefaultViewModel
import com.example.chat_app.ui.users.UsersViewModel
import com.example.chat_app.util.addNewItem


class FriendsViewModelFactory(private val myUserID:String): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FriendsViewModel(myUserID) as T
    }
}


class FriendsViewModel(private val myUserID:String): DefaultViewModel(){
    private val dbRepository= DatabaseRepository()
    private val firebaseReferenceChildObserver = FirebaseReferenceChildObserver()

    private val _selectUser = MutableLiveData<Event<User>>()
    var selectUser : LiveData<Event<User>> = _selectUser
    private val updateFriend= MutableLiveData<User>()
    val friendsList = MediatorLiveData<MutableList<User>>()

    init {
        Log.d("CheckLifecycle", "UserViewModel: UserViewModel created")

        friendsList.addSource(updateFriend){
            friendsList.addNewItem(it)
        }
        loadAndObserverFriends()
    }

    private fun loadAndObserverFriends(){
        dbRepository.loadAndObserverFriend(myUserID,firebaseReferenceChildObserver,
            onAdded = {result: Result<UserFriend> ->
                onResult(null, result)
                if (result is Result.Success) {
                    Log.d("AddChats", "result UserFriend: ${result.data}")
                    result.data?.let { loadUser(it) }
                }
            }, onChanged = {}, onRemove = {}
        )
    }

    private fun loadUser(userFriend: UserFriend){
        dbRepository.loadUser(userFriend.userID){ result :Result<User> ->
            if (result is Result.Success) {
                Log.d("AddChats", "result UserFriend: ${result.data}")
                result.data?.let { updateFriend.value= it }
            }

        }
    }

    fun selectUser(user: User){
        _selectUser.value=Event(user)
    }


}