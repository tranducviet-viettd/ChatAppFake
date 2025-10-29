package com.example.chat_app.data.db.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.example.chat_app.data.Result
import com.example.chat_app.data.model.CreateUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.example.chat_app.data.model.Login
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthStateObserver {

    private var authListener : FirebaseAuth.AuthStateListener? = null
    private var instance :FirebaseAuth? = null

    fun start(authListener: FirebaseAuth.AuthStateListener, instance: FirebaseAuth) {
        Log.d("AuthRepository", "Starting auth listener with instance: $instance")
        this.authListener = authListener
        this.instance = instance
        Log.d("AuthRepository", "Adding auth listener to FirebaseAuth")
        this.instance!!.addAuthStateListener (this.authListener!!)
        Log.d("AuthRepository", "Auth listener added successfully")
    }

    fun clear() {
        Log.d("AuthRepository", "Clearing auth listener")
        authListener?.let { instance?.removeAuthStateListener(it) }
        Log.d("AuthRepository", "Auth listener cleared")
    }

}

class FirebaseAuthSource{

    companion object {
        val authInstance =FirebaseAuth.getInstance()
    }

    private fun attackAuthObserver(b: (Result<FirebaseUser>) -> Unit) : FirebaseAuth.AuthStateListener{
        Log.d("AuthRepository", "Initializing auth observer lambda, initial current user: ${FirebaseAuth.getInstance().currentUser}")
        val listener = FirebaseAuth.AuthStateListener {
            Log.d("AuthRepository", "Auth state changed, currentUser: ${it.currentUser}, thread: ${Thread.currentThread().name}")
            if (it.currentUser == null) {
                Log.d("AuthRepository", "No user, invoking Result.Error")
                b.invoke(Result.Error("No User"))
            } else {
                Log.d("AuthRepository", "User found, invoking Result.Success with UID: ${it.currentUser?.uid}")
                b.invoke(Result.Success(it.currentUser!!))
            }
        }
        Log.d("AuthRepository", "Auth observer lambda created")
        return listener
    }

    fun loginWithEmailAndPassword(login: Login): Task<AuthResult>{
        return authInstance.signInWithEmailAndPassword(login.email,login.password)
    }

    fun createUser(createUser: CreateUser): Task<AuthResult>{
        return authInstance.createUserWithEmailAndPassword(createUser.email,createUser.password)
    }

    fun logout(){
        authInstance.signOut()
    }

    fun attackAuthStateObserver(firebaseAuthStateObserver: FirebaseAuthStateObserver, b: (Result<FirebaseUser>) -> Unit){
        Log.d("AuthRepository", "Creating auth state listener")
        val listener = attackAuthObserver(b)
        Log.d("AuthRepository", "Auth listener created, starting observer")
        firebaseAuthStateObserver.start(listener, authInstance)
        Log.d("AuthRepository", "Auth state observer started") }
}