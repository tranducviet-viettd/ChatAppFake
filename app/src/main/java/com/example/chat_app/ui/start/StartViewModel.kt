package com.example.chat_app.ui.start


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chat_app.data.Event
class StartViewModel : ViewModel() {

    private val _loginEvent = MutableLiveData<Event<Unit>>()
    private val _createAccountEvent =  MutableLiveData<Event<Unit>>()

    val loginEvent:LiveData<Event<Unit>> = _loginEvent
    val createAccountEvent : LiveData<Event<Unit>> = _createAccountEvent

    init {
        Log.d("CheckLifecycle", "StartViewModel: StartViewModel created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CheckLifecycle", "StartViewModel: StartViewModel onCleared called")
    }

    fun goToLoginPressed(){
        _loginEvent.value = Event(Unit)
    }

    fun goToCreateAccountPressed(){
        _createAccountEvent.value = Event(Unit)
    }
}