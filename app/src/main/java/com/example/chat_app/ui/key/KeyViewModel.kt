package com.example.chat_app.ui.key

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chat_app.data.Event
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.example.chat_app.ui.DefaultViewModel

class KeyViewModel : DefaultViewModel(){

    val keyEditText = MutableLiveData<String>()

    private val databaseRepository = DatabaseRepository()

    private val _passKeyEvent = MutableLiveData<Event<Unit>>()
    var passKeyEvent : LiveData<Event<Unit>> = _passKeyEvent

    private val _failKeyEvent = MutableLiveData<Event<Unit>>()
    var failKeyEvent : LiveData<Event<Unit>> = _failKeyEvent
    private val _sendEvent = MutableLiveData<Event<Unit>>()
    var sendEvent : LiveData<Event<Unit>> = _sendEvent

    fun checkKey(){
        databaseRepository.getID { result ->
            if (result is Result.Success){
                result.data?.let { it ->
                    databaseRepository.loadKey(it){ result1 ->
                        if(result1 is Result.Success){
                            result1.data?.let{
                                if(keyEditText.value!! == it.key ) {
                                    passKey()
                                }
                                else{
                                    _failKeyEvent.value=Event(Unit)
                                }

                            }

                        }
                    }
                }
            }
        }
    }

    fun checkActiveKey(b : (Result<Boolean>) -> Unit){
        onResult(null, Result.Loading)
        databaseRepository.getID { result ->
            if (result is Result.Success){
                result.data?.let { it ->
                    databaseRepository.loadKey(it){ result1 ->
                        if(result1 is Result.Success){
                            onResult(null,result1)
                            result1.data?.let{
                                if (it.active){
                                    b.invoke(Result.Success(true))
                                }
                                else{
                                    b.invoke(Result.Success(false))
                                }
                            }

                        }
                    }
                }
            }
        }
    }
    private fun passKey(){
        _passKeyEvent.value= Event(Unit)
    }

    fun sendBtnPressed(){
        _sendEvent.value=Event(Unit)
    }

}