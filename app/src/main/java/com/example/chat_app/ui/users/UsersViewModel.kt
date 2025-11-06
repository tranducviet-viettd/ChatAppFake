package com.example.chat_app.ui.users

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chat_app.data.Event
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.User
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.example.chat_app.ui.DefaultViewModel


class UsersViewModelFactory(private val myUserID:String): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UsersViewModel(myUserID) as T
    }
}


class UsersViewModel(private val myUsersID:String): DefaultViewModel() {

    private val dbRepository= DatabaseRepository()
    private val _selectUser = MutableLiveData<Event<User>>()
    var selectUser : LiveData<Event<User>> = _selectUser

    private val updateUsersList= MutableLiveData<MutableList<User>>()
    val usersList = MediatorLiveData<List<User>>()

    init {
        Log.d("CheckLifecycle", "UserViewModel: UserViewModel created")

        usersList.addSource(updateUsersList){ newUser ->
            usersList.value= updateUsersList.value?.filter { it.info.id != myUsersID }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CheckLifecycle", "UserViewModel: UserViewModel onCleared called")
    }

    fun searchUser(query : String){
        dbRepository.loadUsers { result: Result<MutableList<User>> ->
            if(result is Result.Success){
                updateUsersList.value = result.data?.asSequence()?.filter { user ->
                    user.info.displayName.contains(query,ignoreCase = true)
                }?.toMutableList()
            }
        }
    }

    fun clearUser(){
        usersList.value=emptyList()
    }
    fun selectUser(user: User){
        _selectUser.value=Event(user)
    }
}