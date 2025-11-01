package com.example.chat_app.ui.preview_image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.Message
import com.example.chat_app.data.db.repository.DatabaseRepository
import com.example.chat_app.data.db.repository.StorageRepository
import com.example.chat_app.ui.DefaultViewModel
import com.example.chat_app.ui.show_image.ShowImageViewModel
import com.example.chat_app.util.convertFileToByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PreviewImageViewModelFactory(private val uri: Uri,private val userID: String, private val otherID : String,private val chatID:String,private val context: Context):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PreviewImageViewModel(uri,userID,otherID,chatID,context) as T
    }
}



class PreviewImageViewModel(private val uri: Uri,private val userID: String, private val otherID : String,private val chatID:String,private val context: Context): DefaultViewModel() {
    val _uri: Uri = this.uri

    private val storageRepository= StorageRepository()
    private val dbRepository = DatabaseRepository()
    fun sendImage() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Chuyển đổi Uri thành ByteArray
                val byteArray = convertFileToByteArray(context, _uri)
                storageRepository.uploadUserImage(userID, byteArray) { result ->
                    when (result) {
                        is Result.Success -> {
                            val newMessage =
                                Message(senderID = userID, imageUrl = result.data.toString())
                            dbRepository.updateLastMessage(chatID, newMessage)
                            dbRepository.updateNewMessage(chatID, userID, otherID, newMessage)
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
}

