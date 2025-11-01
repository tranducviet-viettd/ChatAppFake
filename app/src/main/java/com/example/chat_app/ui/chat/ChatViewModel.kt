package com.example.chat_app.ui.chat

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chat_app.data.Event
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.Message
import com.example.chat_app.data.db.entity.UserInfo
import com.example.chat_app.data.db.remote.FirebaseReferenceChildObserver
import com.example.chat_app.data.db.remote.FirebaseReferenceConnectedObserver
import com.example.chat_app.data.db.remote.FirebaseReferenceValueObserver
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.example.chat_app.ui.DefaultViewModel
import com.example.chat_app.util.addNewItem
import com.example.chat_app.util.convertFileToByteArray
import com.example.chat_app.data.db.repository.StorageRepository
import com.example.chat_app.util.removeItem
import com.example.chat_app.util.updateItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModelFactory(private val userID: String, private val otherID : String,private val chatID:String,private val context: Context):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(userID,otherID,chatID,context) as T
    }
    }

class ChatViewModel(private val userID: String,
                    private val otherID: String,
                    private val chatID: String,
                    private val context: Context): DefaultViewModel(){

    private val dbRepository = DatabaseRepository()
    private val _otherUser = MutableLiveData<UserInfo>()
    val otherUser : LiveData<UserInfo> = _otherUser

    private val _addNewMessage = MutableLiveData<Message>()
    private val _removeMessage = MutableLiveData<Message>()
    private val _changeMessage = MutableLiveData<Message>()
    private val firebaseReferenceValueObserver = FirebaseReferenceValueObserver()
    private val firebaseReferenceChildObserver = FirebaseReferenceChildObserver()

    private val storageRepository= StorageRepository()
    val messageList = MediatorLiveData<MutableList<Message>>()

    val newMessageText= MutableLiveData<String?>()

    private val _copyMessageEvent = MutableLiveData<Event<Message>>() // Thêm để xử lý lỗi
    val copyMessageEvent: LiveData<Event<Message>> = _copyMessageEvent

    private val _imagePressEvent = MutableLiveData<Event<Message>>()
    val imagePressEvent : LiveData<Event<Message>> = _imagePressEvent

    private val longClickMessage = MutableLiveData<Message>()
    val showSentBottomToolbar = MutableLiveData<Boolean>()
    val showReceivedBottomToolbar= MutableLiveData<Boolean>()

    private val _shouldScrollToBottom = MutableLiveData<Boolean>()
    val shouldScrollToBottom: LiveData<Boolean> = _shouldScrollToBottom

    private val _isSendingImage = MutableLiveData<Boolean>(false)
    val isSendingImage: LiveData<Boolean> = _isSendingImage  // Observe trong Fragment

    // Gọi khi có tin nhắn mới
    fun triggerScrollToBottom() {
        _shouldScrollToBottom.value = true
    }

    // Reset flag sau khi cuộn xong
    fun clearScrollFlag() {
        Log.d("testScroll","ccc")
        _shouldScrollToBottom.value = false
    }

    init {
        Log.d("MessageListDebug", "Init: ${messageList.value ?: "null"}")
        Log.d("CheckLifecycle", "ChatViewModel: ChatViewModel created")
        showSentBottomToolbar.value=false
        setupChat()
        checkAndUpdateLastMessageSeen()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CheckLifecycle", "ChatViewModel: ChatViewModel onCleared called")
        firebaseReferenceChildObserver.clear()
        firebaseReferenceValueObserver.clear()
    }

    fun onSentBottomToolbar(message: Message): Boolean{
        showSentBottomToolbar.value=true
        longClickMessage.value=message
        return true
    }

    fun onReceivedBottomToolbar(message: Message): Boolean{
        showReceivedBottomToolbar.value=true
        longClickMessage.value=message
        return true
    }


    fun offBottomToolbar(){
        showSentBottomToolbar.value=false
        showReceivedBottomToolbar.value=false
    }

    fun deleteMessageBtnPressed(){
        dbRepository.removeOneMessageOneUser(chatID,userID, longClickMessage.value!!.id)
        offBottomToolbar()
    }

    fun undoMessageBtnPressed(){
        dbRepository.undoMessage(chatID,userID,otherID,longClickMessage.value!!.id,longClickMessage.value!!)
        offBottomToolbar()
    }

    fun copyMessageBtnPressed(){
        _copyMessageEvent.value=Event(longClickMessage.value!!)
        offBottomToolbar()
    }

    private fun setupChat(){
        Log.d("MessageListDebug", "setupChat before loadUserInfo: ${messageList.value ?: "null"}") // Điểm 2: Trước khi load user info
        dbRepository.loadAndObserveUserInfo(otherID,firebaseReferenceValueObserver){ result ->
            onResult(_otherUser, result)
            if(result is Result.Success && !firebaseReferenceChildObserver.isObserving()){
                loadAndObserveNewMessage()
            }
        }
        Log.d("MessageListDebug", "setupChat after loadUserInfo: ${messageList.value ?: "null"}") // Điểm 3: Sau khi load user info
    }
    private fun loadAndObserveNewMessage(){
        Log.d("MessageListDebug", "loadAndObserveNewMessage start: ${messageList.value ?: "null"}") // Điểm 4: Bắt đầu thiết lập observer
        messageList.addSource(_addNewMessage) {
           messageList.addNewItem(it)
        }

        messageList.addSource(_changeMessage) {
            messageList.updateItem(longClickMessage.value!!,it)
        }

        messageList.addSource(_removeMessage) { removedMessage ->
             messageList.removeItem(removedMessage)
        }


        dbRepository.loadAndObserveMessage(
            chatID, userID, firebaseReferenceChildObserver,
            onAdded = { result ->

                onResult(_addNewMessage,result)
                triggerScrollToBottom()

                      },
            onChanged = {
                result ->
                clearScrollFlag()
                onResult(_changeMessage,result)
               },
            onRemove = { result ->
                clearScrollFlag()
                 onResult(_removeMessage, result)
                }
        )
    }

    private fun checkAndUpdateLastMessageSeen(){
        dbRepository.loadChat(chatID){ result ->
            if(result is Result.Success && result.data!=null){
                result.data.lastMessage.let {
                    if(!it.seen && it.senderID != userID){
                        it.seen=true
                        dbRepository.updateLastMessage(chatID,it)
                    }

                }
            }
        }
    }

    fun sendImage(uri: Uri) {
        onResult(null, Result.Loading)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Chuyển đổi Uri thành ByteArray
                val byteArray = convertFileToByteArray(context, uri)
                storageRepository.uploadUserImage(userID, byteArray) { result ->
                    when (result) {
                        is Result.Success -> {
                            val newMessage =
                                Message(senderID = userID, imageUrl = result.data.toString())
                            dbRepository.updateLastMessage(chatID, newMessage)
                            dbRepository.updateNewMessage(chatID, userID, otherID, newMessage)
                            onResult(null, Result.Success(null))

                        }

                        is Result.Error -> {

                        }

                        is Result.Loading -> {
                            // Có thể cập nhật UI để hiển thị trạng thái loading
                        }
                    }
                }
            } catch (e: Exception) {

            } finally {

            }
        }
    }
    fun sendMessagePressed(){
        if(!newMessageText.value.isNullOrBlank()){
            val newMessage = Message(senderID = userID, text = newMessageText.value!!)
            dbRepository.updateLastMessage(chatID,newMessage)
            dbRepository.updateNewMessage(chatID,userID,otherID,newMessage)
            newMessageText.value=null
        }

    }

    fun imageMessagePressed(message: Message){
        _imagePressEvent.value=Event(message)
    }


}