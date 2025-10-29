package com.example.chat_app.ui.start.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chat_app.data.Event
import com.example.chat_app.data.model.Login
import com.example.chat_app.ui.DefaultViewModel
import com.example.chat_app.data.db.repository.AuthRepository
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.User
import com.example.chat_app.data.db.entity.UserFriend
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.example.chat_app.util.isEmailValid
import com.example.chat_app.util.isTextValid
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : DefaultViewModel() {

    private val authRepository = AuthRepository()
    private val dbRepository= DatabaseRepository()
    private val _isLoggedInEvent = MutableLiveData<Event<FirebaseUser>>()

    val isLoggedInEvent : LiveData<Event<FirebaseUser>> = _isLoggedInEvent
    val isLoggingIn = MutableLiveData<Boolean>()
    val emailText = MutableLiveData<String>()
    val passwordText = MutableLiveData<String>()

    private val _switchEvent= MutableLiveData<Event<Unit>>()

    val switchEvent : LiveData<Event<Unit>> = _switchEvent

    val listUsers = MutableLiveData<MutableList<User>>()
    val listFriends = MutableLiveData<MutableList<UserFriend>>()
    private lateinit var userCheck : User


    init {
        Log.d("CheckLifecycle", "LoginViewModel: LoginViewModel created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CheckLifecycle", "LoginViewModel: LoginViewModel onCleared called")
    }


    fun login(){
        isLoggingIn.value=true
        val login = Login(emailText.value!!,passwordText.value!!)

        authRepository.loginUser(login){
            result ->
            onResult(null,result)
            if(result is Result.Success) _isLoggedInEvent.value=Event(result.data!!)
            if(result is Result.Error ){
                _switchEvent.value=Event(Unit)

            }
            if(result is Result.Error || result is Result.Success){
                isLoggingIn.value=false

            }
        }

    }


    fun loginPressed(){
        if(!isEmailValid(emailText.value.toString())){
            mSnackBarText.value = Event("Invalid Email Format")
            return
        }
        if(!isTextValid(6,passwordText.value.toString())){
            mSnackBarText.value = Event("Password too short")
            return
        }

        login()
    }

    fun removeHistory(){
        dbRepository.loadUsers { result ->
            onResult(listUsers,result)
            Log.d("loginViewModel","listUsers: ${listUsers.value?.size}")
            val user = listUsers.value?.find { it.info.email == emailText.value.toString() }
            Log.d("loginViewModel","user: $user")
            if(user!=null) {

                dbRepository.loadFriends(user.info.id) { result ->
                    onResult(listFriends, result)
                    Log.d("loginViewModel","listFriends: ${listFriends.value?.size}")
                    listFriends.value?.forEach {
                        dbRepository.removeFriend(user.info.id, it.userID)
                    }
                }

            }
        }


    }
}