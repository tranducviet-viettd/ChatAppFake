package com.example.chat_app.ui.chats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chat_app.data.Event
import com.example.chat_app.data.db.remote.FirebaseReferenceValueObserver
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.example.chat_app.data.model.ChatWithUserInfo
import com.example.chat_app.ui.DefaultViewModel
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.UserFriend
import com.example.chat_app.data.db.entity.UserInfo
import com.example.chat_app.data.db.remote.FirebaseReferenceChildObserver
import com.example.chat_app.util.convertTwoUserIDs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class ChatsViewModelFactory(private val myUserID: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatsViewModel(myUserID) as T
    }
}

class ChatsViewModel(val myUserID: String) : DefaultViewModel() {
    private val dbRepository = DatabaseRepository()
    private val firebaseReferenceObserverList = ArrayList<FirebaseReferenceValueObserver>()
    private val _updateChatWithUserInfo = MutableSharedFlow<ChatWithUserInfo>()
    private val _selectChat = MutableLiveData<Event<ChatWithUserInfo>>()
    private val firebaseReferenceChildObserver = FirebaseReferenceChildObserver()
    private val tempChatList = mutableListOf<ChatWithUserInfo>() // Danh sách tạm để tích lũy

    var selectChat: LiveData<Event<ChatWithUserInfo>> = _selectChat
    val chatsList = MutableLiveData<MutableList<ChatWithUserInfo>>().apply {
        value = mutableListOf() // Khởi tạo danh sách rỗng
    }

    init {
        Log.d("CheckLifecycle", "ChatsViewModel: ChatsViewModel created")
        Log.d("AddChats", "ChatsViewModel initialized for user: $myUserID")
        viewModelScope.launch {
            _updateChatWithUserInfo.collect { newChat ->
                synchronized(tempChatList) { // Đồng bộ hóa để tránh race condition
                    val existingChat = tempChatList.find { it.mChat.info.id == newChat.mChat.info.id }
                    if (existingChat == null) {
                        tempChatList.add(newChat)
                        Log.d("AddChats", "Added new chat for ${newChat.mUserInfo.id}, size: ${tempChatList.size}")
                    } else {
                        tempChatList[tempChatList.indexOf(existingChat)] = newChat
                        Log.d("AddChats", "Updated chat for ${newChat.mUserInfo.id}, size: ${tempChatList.size}")
                    }
                    chatsList.postValue(tempChatList.toMutableList())
                    Log.d("AddChats", "chatsList.Size: ${tempChatList.size}")
                }
            }
        }
        setupChats()
    }

    private fun setupChats() {
        Log.d("AddChats", "Call setupChats")
        loadAndObserveFriends()
    }

    private fun loadAndObserveFriends() {
        Log.d("AddChats", "Call loadAndObserveFriends")
        dbRepository.loadAndObserverFriend(myUserID, firebaseReferenceChildObserver,
        onAdded = { result: Result<UserFriend> ->
            onResult(null, result)
            if (result is Result.Success) {
                Log.d("AddChats", "result UserFriend: ${result.data}")
                result.data?.let { loadUserInfo(it) }
            }
        }, onChanged = {}, onRemove = {})
    }

    private fun loadUserInfo(userFriend: UserFriend) {
        Log.d("AddChats", "Call loadUserInfo for user: ${userFriend.userID}")
        dbRepository.loadUserInfo(userFriend.userID) { result ->
            onResult(null, result)
            if (result is Result.Success) {
                Log.d("AddChats", "result UserInfo: ${result.data}")
                result.data?.let { loadAndObserverChat(it) }
            }
        }
    }

    private fun loadAndObserverChat(userInfo: UserInfo) {
        Log.d("AddChats", "Start observing chat for user: ${userInfo.id}")
        val observer = FirebaseReferenceValueObserver()
        firebaseReferenceObserverList.add(observer)
        dbRepository.loadAndObservseChat(convertTwoUserIDs(myUserID, userInfo.id), observer) { result ->
            if (result is Result.Success) {
                Log.d("AddChats", "Chat loaded for ${userInfo.id}, type: ${result.data?.javaClass?.name}")
                result.data?.let { chat ->
                    viewModelScope.launch {
                        try {
                            _updateChatWithUserInfo.emit(ChatWithUserInfo(chat, userInfo))
                            Log.d("AddChats", "Emitted _updateChatWithUserInfo for ${userInfo.id}")
                        } catch (e: Exception) {
                            Log.e("AddChats", "Error emitting for ${userInfo.id}: ${e.message}")
                        }
                    }
                } ?: Log.e("AddChats", "Chat data is null for ${userInfo.id}")
            } else if (result is Result.Error) {
                Log.e("AddChats", "Error loading chat for ${userInfo.id}: ${result.msg}")
                synchronized(tempChatList) {
                    tempChatList.removeIf { it.mChat.info.id == convertTwoUserIDs(myUserID, userInfo.id) }
                    chatsList.postValue(tempChatList.toMutableList())
                }
            }
        }
    }

    fun selectedChatWithUserInfoPressed(chat: ChatWithUserInfo) {
        _selectChat.value = Event(chat)
    }

    override fun onCleared() {
        super.onCleared()
        firebaseReferenceObserverList.forEach { it.clear() }
        firebaseReferenceChildObserver.clear()
        Log.d("AddChats", "ChatsViewModel cleared")
        Log.d("CheckLifecycle", "ChatsViewModel: ChatsViewModel onCleared called")

    }
}