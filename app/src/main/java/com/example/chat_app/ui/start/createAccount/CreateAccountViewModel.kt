package com.example.chat_app.ui.start.createAccount

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chat_app.data.Event
import com.example.chat_app.data.db.repository.AuthRepository
import com.example.chat_app.data.model.CreateUser
import com.example.chat_app.ui.DefaultViewModel
import com.example.chat_app.util.isEmailValid
import com.example.chat_app.util.isTextValid
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.User
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.google.firebase.auth.FirebaseUser

class CreateAccountViewModel : DefaultViewModel(){

    private val authRepository = AuthRepository()
    private val dbRepository = DatabaseRepository()

    val emailText = MutableLiveData<String>()
    val passwordText = MutableLiveData<String>()
    val displayNameText = MutableLiveData<String>()

    private val _isCreatedEvent = MutableLiveData<Event<FirebaseUser>>()
    val isCreateEvent : LiveData<Event<FirebaseUser>> =_isCreatedEvent
    val isCreatingAccount = MutableLiveData<Boolean>()


    init {
        Log.d("CheckLifecycle", "CreateViewModel: ChatViewModel created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CheckLifecycle", "CreateViewModel: ChatsViewModel onCleared called")
    }

    private fun createAccount(){
        val createAccount = CreateUser(displayNameText.value.toString(),emailText.value.toString(),passwordText.value.toString())

        authRepository.createUser(createAccount){
            result ->
            onResult(null,result)
            if(result is Result.Success){
                _isCreatedEvent.value = Event(result.data!!)
                dbRepository.updateNewUser(User().apply {
                    info.id=result.data.uid
                    info.displayName=createAccount.displayName
                    info.email=createAccount.email
                })


            }
            if(result is Result.Success || result is Result.Error){
                isCreatingAccount.value=false
            }


        }


    }

    fun createAccountPressed(){
        if(!isEmailValid(emailText.value.toString())){
            mSnackBarText.value= Event("Invalid Email Format")
        }
        if(!isTextValid(6,passwordText.value.toString())){
            mSnackBarText.value=Event("Password too short")
        }
        createAccount()

    }


}