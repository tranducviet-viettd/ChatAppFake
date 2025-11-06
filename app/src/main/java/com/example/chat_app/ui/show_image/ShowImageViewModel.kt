package com.example.chat_app.ui.show_image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chat_app.data.Event
import com.example.chat_app.ui.DefaultViewModel
import com.example.chat_app.ui.chat.ChatViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale

class ShowImageViewModelFactory(private val imageUrl: String):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ShowImageViewModel(imageUrl) as T
    }
}


class ShowImageViewModel(private val imageUrl: String): DefaultViewModel() {

    val _imageUrl: String = this.imageUrl

    private val _downBtnEvent = MutableLiveData<Event<String>>()
    val downBtnEvent: LiveData<Event<String>> = _downBtnEvent

    private val _rotateBtnEvent = MutableLiveData<Float>()
    val rotateBtnEvent: LiveData<Float> = _rotateBtnEvent


    private val _backBtnEvent = MutableLiveData<Event<Unit>>()
    val backBtnEvent: LiveData<Event<Unit>> = _backBtnEvent

    init {
        _rotateBtnEvent.value= 0f
    }

    fun rotateBtnPressed(){
        _rotateBtnEvent.value=if((_rotateBtnEvent.value!! + 90f) == 360f) 360f else (_rotateBtnEvent.value!! + 90f) % 360f
    }
    fun downBtnPressed(){
        _downBtnEvent.value= Event(_imageUrl)
    }

    fun backBtnPressed(){

    }
}