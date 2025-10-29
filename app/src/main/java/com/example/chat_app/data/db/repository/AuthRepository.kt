package com.example.chat_app.data.db.repository

import android.util.Log
import com.example.chat_app.data.db.remote.FirebaseAuthSource
import com.example.chat_app.data.db.remote.FirebaseAuthStateObserver
import com.example.chat_app.data.Result
import com.example.chat_app.data.model.CreateUser
import com.example.chat_app.data.model.Login
import com.google.firebase.auth.FirebaseUser

class AuthRepository {

    private val firebaseAuthService= FirebaseAuthSource()

    fun observeAuthState(firebaseAuthStateObserver: FirebaseAuthStateObserver, b: ((Result<FirebaseUser>) -> Unit)){
        Log.d("AuthRepository", "Starting to observe auth state")
        firebaseAuthService.attackAuthStateObserver(firebaseAuthStateObserver, b)
        Log.d("AuthRepository", "Auth state observer setup completed")
    }

    fun loginUser(login: Login,b : ((Result<FirebaseUser>) -> Unit)){
        b.invoke(Result.Loading)
        firebaseAuthService.loginWithEmailAndPassword(login).addOnSuccessListener {
            Log.d("AuthRepository", "Test login success, user: ${it.user}")
            b.invoke(Result.Success(it.user))
        }.addOnFailureListener {
            b.invoke(Result.Error(it.message))
        }

    }

    fun createUser(createUser: CreateUser,b : ((Result<FirebaseUser>) -> Unit)){
        b.invoke(Result.Loading)
        firebaseAuthService.createUser(createUser).addOnSuccessListener {
            b.invoke(Result.Success(it.user))
        }.addOnFailureListener {
            b.invoke(Result.Error(it.message))
        }

    }

    fun logoutUser(){
        firebaseAuthService.logout()
    }


}