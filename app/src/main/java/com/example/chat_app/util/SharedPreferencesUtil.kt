package com.example.chat_app.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SharedPreferencesUtil {
    private const val PACKAGE_NAME="com.example.chat_app"
    private const val KEY_USER_ID="user_info"

    private fun getPrefs(context : Context) : SharedPreferences {
        return context.getSharedPreferences(PACKAGE_NAME,Context.MODE_PRIVATE)
    }
    fun getUserID(context: Context) : String? {
        return getPrefs(context).getString(KEY_USER_ID,null)
    }
    fun saveUserID(context: Context,userID : String) {
        return getPrefs(context).edit { putString(KEY_USER_ID, userID) }
    }
    fun removeUserID(context: Context){
        return  getPrefs(context).edit { remove(KEY_USER_ID) }
    }


}