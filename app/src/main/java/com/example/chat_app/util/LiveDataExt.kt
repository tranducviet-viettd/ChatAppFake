package com.example.chat_app.util

import android.util.Log
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<MutableList<T>>.addNewItem(item: T){
    val newList = mutableListOf<T>()
    this.value?.let{newList.addAll(it)}
    newList.add(item)
    this.value= newList
}
fun <T> MutableLiveData<MutableList<T>>.addNewItem2(item: T, idSelector: (T) -> String) {
    val newList = mutableListOf<T>()
    this.value?.let { newList.addAll(it) }
    if (newList.none { idSelector(it) == idSelector(item) }) {
        newList.add(item)
        this.value = newList
    }
}

fun <T> MutableLiveData<MutableList<T>>.updateItem(oldItem: T, newItem: T) {
    val newList = mutableListOf<T>()
    this.value?.let { newList.addAll(it) }
    val index = newList.indexOf(oldItem)
    Log.d("Testindex","$index")
    if (index != -1) { // Kiểm tra xem oldItem có trong danh sách không
        newList[index] = newItem
        this.value = newList
    }
}

fun <T> MutableLiveData<MutableList<T>>.removeItem(item: T){
    val newList= mutableListOf<T>()
    this.value?.let{newList.addAll(it)}
    newList.remove(item)
    this.value=newList
}