package com.example.chat_app.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chat_app.App
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.User
import com.example.chat_app.data.db.entity.UserNotification
import com.example.chat_app.data.db.remote.FirebaseAuthStateObserver
import com.example.chat_app.data.db.remote.FirebaseReferenceConnectedObserver
import com.example.chat_app.data.db.remote.FirebaseReferenceValueObserver
import com.example.chat_app.data.db.repository.AuthRepository
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.google.firebase.auth.FirebaseAuth

class MainViewModel: ViewModel() {
    private val dbRepository = DatabaseRepository()
    private val authRepository = AuthRepository()
    private val _userNotificationsList  = MutableLiveData<MutableList<UserNotification>?>()

    private val fbRefNotificationsObserver = FirebaseReferenceValueObserver()
    private val fbAuthObserver= FirebaseAuthStateObserver()
    private val fbConnectedObserver = FirebaseReferenceConnectedObserver()

    private var userID = App.myUserID

    var userNotificationsList: LiveData<MutableList<UserNotification>?> = _userNotificationsList

    init {
        Log.d("MainViewModel", "ViewModel initialized, current user: ${FirebaseAuth.getInstance().currentUser}")
        setupAuthObserver()
    }

    override fun onCleared() {
        super.onCleared()
        fbRefNotificationsObserver.clear()
        fbConnectedObserver.clear()
        fbAuthObserver.clear()

    }
    private fun setupAuthObserver(){
        Log.d("MainViewModel", "begin")
       authRepository.observeAuthState(fbAuthObserver){ result ->
           Log.d("MainViewModel", "Callback triggered with result: $result")
           if(result is Result.Success){
               Log.d("MainViewModel", "auth:${result.data}")
               userID = result.data!!.uid
               startObservingNotifications()
               fbConnectedObserver.start(userID)
           }
           else{
               Log.d("MainViewModel", "fail")
               fbConnectedObserver.clear()
               stopObservingNotifications()
           }
           
       }
        Log.d("MainViewModel", "Observer registered")
    }

    private fun startObservingNotifications(){
        dbRepository.loadAndObserveNotification(userID,fbRefNotificationsObserver){result ->
            if(result is Result.Success){
                // Log dữ liệu từ Firebase
                Log.d("MainViewModel", "Notifications updated: ${result.data}")
                _userNotificationsList.value = result.data
                // Log giá trị mới của LiveData
                Log.d("MainViewModel", "userNotificationsList new value: ${_userNotificationsList.value}")
            }
        }
    }
    private fun stopObservingNotifications(){
        fbRefNotificationsObserver.clear()
    }
}