package com.example.chat_app.ui.settings


import android.util.Log
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chat_app.data.Event
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.UserInfo
import com.example.chat_app.data.db.entity.Key
import com.example.chat_app.data.db.remote.FirebaseReferenceValueObserver
import com.example.chat_app.data.db.repository.AuthRepository
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.example.chat_app.data.db.repository.StorageRepository
import com.example.chat_app.ui.DefaultViewModel


class SettingsViewModelFactory(private val userID:String): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(userID) as T
    }

}

class SettingsViewModel(private val userID: String): DefaultViewModel(){

    private val dbRepository = DatabaseRepository()
    private val storageRepository = StorageRepository()
    private val authRepository = AuthRepository()

    private val _userInfo = MutableLiveData<UserInfo>()
    val userInfo : LiveData<UserInfo> = _userInfo

    private val _editStatusEvent = MutableLiveData<Event<Unit>>()
    val editStatusEvent : LiveData<Event<Unit>> = _editStatusEvent

    private val _editKeyEvent = MutableLiveData<Event<Unit>>()

    val editKeyEvent: LiveData<Event<Unit>> = _editKeyEvent
    private val _editImageEvent= MutableLiveData<Event<Unit>>()
    val editImageEvent: LiveData<Event<Unit>> = _editImageEvent

    private val _profileImageEvent = MutableLiveData<Event<String>>()
    val profileImageEvent: LiveData<Event<String>> = _profileImageEvent
    private val _logoutEvent = MutableLiveData<Event<Unit>>()
    val logoutEvent : LiveData<Event<Unit>> = _logoutEvent

    private val firebaseReferenceValueObserver = FirebaseReferenceValueObserver()

    init {
        Log.d("CheckLifecycle", "SettingViewModel: SettingViewModel created")
        loadAndObserveUserInfo()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CheckLifecycle", "SettingViewModel: SettingViewModel onCleared called")
    }

    private fun loadAndObserveUserInfo(){
        dbRepository.loadAndObserveUserInfo(userID,firebaseReferenceValueObserver){ result ->
            onResult(_userInfo,result)

        }
    }

    fun changeUserStatus(status:String){
        dbRepository.updateNewStatus(userID,status)
    }

    fun changeUserImage(byteArray: ByteArray){
        storageRepository.uploadUserImage(userID,byteArray){ result ->
                onResult(null,result)
                if (result is Result.Success){
                    dbRepository.updateProfileImageUrl(userID,result.data.toString())
                }
        }
    }

    fun changeUserKey(key: String){
        dbRepository.getID { result1 ->
            if(result1 is Result.Success) {
                result1.data?.let {  dbRepository.updateNewKey(it, Key(key))}

            }
        }
    }

    fun notActiveKey(){
        dbRepository.getID { result ->
            if (result is Result.Success){
                result.data?.let { dbRepository.updateNotActiveKey(it) }
            }
        }
    }
    fun changeUserImagePressed(){
        _editImageEvent.value= Event(Unit)
    }

    fun changeUserStatusPressed(){
        _editStatusEvent.value=Event(Unit)
    }

    fun profileImagePressed() {
        _profileImageEvent.value = Event(_userInfo.value?.profileImageUrl ?: "")
    }
    fun logoutPressed(){
        authRepository.logoutUser()
        _logoutEvent.value=Event(Unit)
    }

    fun addKeyPressed(){
        _editKeyEvent.value= Event(Unit)
    }

    fun removeKeyPressed(){
        notActiveKey()
    }




}