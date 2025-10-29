package com.example.chat_app.data.db.remote

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.chat_app.data.Result
import com.example.chat_app.data.db.entity.Chat
import com.example.chat_app.data.db.entity.Key
import com.example.chat_app.data.db.entity.Message
import com.example.chat_app.data.db.entity.User
import com.example.chat_app.data.db.entity.UserFriend
import com.example.chat_app.data.db.entity.UserInfo
import com.example.chat_app.data.db.entity.UserNotification
import com.example.chat_app.data.db.entity.UserRequest
import com.example.chat_app.util.wrapSnapshotToArrayList
import com.example.chat_app.util.wrapSnapshotToClass
import com.google.firebase.database.ChildEventListener
import com.google.firebase.installations.FirebaseInstallations
import java.util.Date

class FirebaseInstallation{

    fun getID(b : (Result<String>) -> Unit) {
        FirebaseInstallations.getInstance().id.addOnCompleteListener{ task ->
            if(task.isSuccessful){
                b.invoke(Result.Success(task.result))
            }


        }
    }

}
class FirebaseDataSource {

    companion object{
        val dbInstance = FirebaseDatabase.getInstance()
    }

    private fun refToPath(path: String): DatabaseReference{
        return dbInstance.reference.child(path)
    }

    private fun attachValueListenerToTaskComplete(src: TaskCompletionSource<DataSnapshot>): ValueEventListener{
        return (
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    src.setResult(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    src.setException(Exception(error.message))
                }
            })
    }

    private fun <T> attachValueListenerToBlock(resultClassName: Class<T>,b : (Result<T>) -> Unit): ValueEventListener{
        return (object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(wrapSnapshotToClass(resultClassName,snapshot) == null){
                    b.invoke(Result.Error(snapshot.key))
                }
                b.invoke(Result.Success(wrapSnapshotToClass(resultClassName,snapshot)))
            }

            override fun onCancelled(error: DatabaseError) {
                b.invoke(Result.Error(error.message))
            }

        })
    }

    private fun <T> attachValueListenerToBlockWithList(resultClassName: Class<T>,b : (Result<MutableList<T>>) -> Unit):ValueEventListener{
        return (object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                b.invoke(Result.Success(wrapSnapshotToArrayList(resultClassName,snapshot)))
            }

            override fun onCancelled(error: DatabaseError) {
                b.invoke(Result.Error(error.message))
            }

        })
    }

    private fun <T> attachChildListenerToBlock(resultClassName: Class<T>, onAdded: (Result<T>) -> Unit, onChanged: (Result<T>) -> Unit, onRemove:(Result<T>) -> Unit) : ChildEventListener{
        Log.d("AddChats","Call attachChildListenerToBlock")
        return (object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("MessageListDebug", "onChildAdded snapshot: $snapshot") // Điểm 3
                onAdded.invoke(Result.Success(wrapSnapshotToClass(resultClassName,snapshot)))
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("MessageListDebug", "onChildChanged snapshot: $snapshot")
                onChanged.invoke(Result.Success(wrapSnapshotToClass(resultClassName,snapshot)))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                onRemove.invoke(Result.Success(wrapSnapshotToClass(resultClassName,snapshot)))
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
                onAdded.invoke(Result.Error(error.message))
            }

        })
    }

    fun updateNewUser(user:User){
        refToPath("users/${user.info.id}").setValue(user)
    }

    fun updateNewFriend(userFriend1: UserFriend,userFriend2: UserFriend){
        Log.d("AddChats","Call updateNewFriend")
        refToPath("users/${userFriend1.userID}/friends/${userFriend2.userID}").setValue(userFriend2)
        refToPath("users/${userFriend2.userID}/friends/${userFriend1.userID}").setValue(userFriend1)
    }

    fun updateNewChat(chat: Chat){
        Log.d("AddChats","Call updateNewChat")
        refToPath("chats/${chat.info.id}").setValue(chat)
    }
    fun updateNewSentRequest(myUserID: String,userRequest: UserRequest){
        refToPath("users/$myUserID/sentRequests/${userRequest.userID}").setValue(userRequest)
    }

    fun updateNewNotification(userID: String,userNotification: UserNotification){
        refToPath("users/$userID/notifications/${userNotification.userID}").setValue(userNotification)
    }

    fun updateNewStatus(userID: String,status:String){
        refToPath("users/$userID/info/status").setValue(status)
    }

    fun updateNewKey(deviceID: String,key: Key){
        refToPath("keys/$deviceID").setValue(key)
    }

    fun updateNotActiveKey(deviceID: String){
        refToPath("keys/$deviceID/active").setValue(false)
    }

    fun updateProfileImageUrl(userID: String,url: String){
        refToPath("users/$userID/info/profileImageUrl").setValue(url)
    }

    fun updateChatLastMessage(chatID: String,message: Message){
        refToPath("chats/$chatID/lastMessage").setValue(message)
    }

    fun updateNewMessage(chatID: String,userID: String,otherID:String,message: Message){
        val upMessage = message
        val generatedKey = refToPath("messages/$chatID/$userID").push().key
        upMessage.id=generatedKey!!

        refToPath("messages/$chatID/$userID/${upMessage.id}").setValue(upMessage)
        refToPath("messages/$chatID/$otherID/${upMessage.id}").setValue(upMessage)
    }
    fun removeFriend(myUserID: String,userID: String){
        refToPath("users/$myUserID/friends/$userID").setValue(null)
        refToPath("users/$userID/friends/$myUserID").setValue(null)
    }

    fun removeAllFriends(userID: String){
        refToPath("users/$userID/friends").setValue(null)
    }

    fun removeChat(chatID: String){
        refToPath("chats/$chatID").setValue(null)
    }

    fun removeKey(deviceID: String){
        refToPath("keys/$deviceID").setValue(null)
    }

    fun removeMessage(messageID:String){
        refToPath("messages/$messageID").setValue(null)
    }

    fun removeOneMessageOneUser(chatID: String,userID: String,messageID: String){
        refToPath("messages/$chatID/$userID/$messageID").setValue(null)
    }

    fun undoMessage(chatID: String,userID: String,otherID:String,messageID: String,message: Message){
        val messageRef = "messages/$chatID/$userID/$messageID"

        val updates = mapOf(
            "$messageRef/text" to "Tin nhắn đã được thu hồi!!",
            "$messageRef/imageUrl" to ""  // luôn xóa, dù có hay không
        )

        FirebaseDatabase.getInstance().reference.updateChildren(updates)

        refToPath("messages/$chatID/$otherID/$messageID").removeValue()
    }
    fun removeNotification(myUserID: String,userID: String){
        refToPath("users/$myUserID/notifications/$userID").setValue(null)
    }

    fun removeSentRequest(userID: String,myUserID: String){
        refToPath("users/$userID/sentRequests/$myUserID").setValue(null)
    }

    fun loadFriendsTask(myUserID:String):Task<DataSnapshot>{
        val src=TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskComplete(src)
        refToPath("users/$myUserID/friends").addListenerForSingleValueEvent(listener)
        return src.task

    }

    fun loadKeyTask(deviceID: String): Task<DataSnapshot>{
        val src= TaskCompletionSource<DataSnapshot>()
        val listener=attachValueListenerToTaskComplete(src)
        refToPath("keys/$deviceID").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun loadUserInfoTask(userId: String):Task<DataSnapshot>{
        val src = TaskCompletionSource<DataSnapshot>()
        val listener= attachValueListenerToTaskComplete(src)
        refToPath("users/$userId/info").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun loadUsersTask(): Task<DataSnapshot>{
        val src = TaskCompletionSource<DataSnapshot>()
        val listener= attachValueListenerToTaskComplete(src)
        refToPath("users").addListenerForSingleValueEvent(listener)
        return src.task

    }

    fun loadUserTask(userId: String):Task<DataSnapshot>{
        val src = TaskCompletionSource<DataSnapshot>()
        val listener= attachValueListenerToTaskComplete(src)
        refToPath("users/$userId").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun loadChatTask(chatID: String):Task<DataSnapshot>{
        val src= TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskComplete(src)
        refToPath("chats/$chatID").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun loadNotifications(myUserID: String): Task<DataSnapshot>{
        val src = TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskComplete(src)
        refToPath("users/$myUserID/notifications").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun <T> attachChatObserver(resultClassName: Class<T>,refObs: FirebaseReferenceValueObserver,chatID:String,b: (Result<T>)->Unit){
        val listener= attachValueListenerToBlock(resultClassName,b)
        refObs.start(listener,refToPath("chats/$chatID"))
    }

    fun <T> attachUserObserver(resultClassName: Class<T>,refObs: FirebaseReferenceValueObserver,userID:String,b: (Result<T>)->Unit){
        val listener= attachValueListenerToBlock(resultClassName,b)
        refObs.start(listener,refToPath("users/$userID"))
    }

    fun <T> attachUserInfoObserver(resultClassName: Class<T>,refObs: FirebaseReferenceValueObserver,userID:String,b: (Result<T>) -> Unit){
        val listener = attachValueListenerToBlock(resultClassName,b)
        refObs.start(listener,refToPath("users/$userID/info"))
    }

    fun <T> attachMessageObserver(resultClass: Class<T>, refObs: FirebaseReferenceChildObserver,messageID: String,userID: String,onAdded:(Result<T>) -> Unit,onChanged:(Result<T>) -> Unit,onRemove:(Result<T>) -> Unit){
        val listener = attachChildListenerToBlock(resultClass,onAdded,onChanged,onRemove)
        refObs.start(listener,refToPath("messages/$messageID/$userID"))
    }

    fun <T> attachNotificationObserver(resultClass: Class<T>,refObs: FirebaseReferenceValueObserver,myUserID: String,b: (Result<MutableList<T>>) -> Unit){
        val listener = attachValueListenerToBlockWithList(resultClass,b)
        refObs.start(listener,refToPath("users/$myUserID/notifications"))
    }

    fun <T> attachFriendObserver(resultClass: Class<T>,refObs: FirebaseReferenceChildObserver,myUserID: String,onAdded:(Result<T>) -> Unit,onChanged:(Result<T>) -> Unit,onRemove:(Result<T>) -> Unit){
        Log.d("AddChats","Call attachFriendObserver")
        val listener = attachChildListenerToBlock(resultClass,onAdded,onChanged,onRemove)
        Log.d("AddChats","listener : $listener")
        refObs.start(listener,refToPath("users/$myUserID/friends"))
        Log.d("AddChats","Observerd Friends")
    }
    fun <T> attachFriendObserver2(resultClass: Class<T>,refObs: FirebaseReferenceValueObserver,myUserID: String,b:(Result<MutableList<T>>) -> Unit){
        Log.d("AddChats","Call attachFriendObserver")
        val listener = attachValueListenerToBlockWithList(resultClass,b)
        Log.d("AddChats","listener : $listener")
        refObs.start(listener,refToPath("users/$myUserID/friends"))
        Log.d("AddChats","Observerd Friends")
    }

}

class FirebaseReferenceConnectedObserver {

    private var valueEventListener: ValueEventListener? = null
    private var dbRef: DatabaseReference? = null
    private var userRef: DatabaseReference? = null

    private fun getValueEventListener(userId: String): ValueEventListener {
        return (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    FirebaseDataSource.dbInstance.reference.child("users/$userId/info/online")
                        .setValue(true)
                    userRef?.onDisconnect()?.setValue(false)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun start(userId: String) {
        this.userRef = FirebaseDataSource.dbInstance.reference.child("users/$userId/info/online")
        this.valueEventListener = getValueEventListener(userId)
        dbRef = FirebaseDataSource.dbInstance.getReference(".info/connected").apply {
            addValueEventListener(
                valueEventListener!!
            )
        }
    }

    fun clear() {
        valueEventListener?.let { dbRef?.removeEventListener(it) }
        valueEventListener = null
        dbRef = null
        userRef?.setValue(false)
        userRef = null
    }
}

class FirebaseReferenceValueObserver{

    private var dbRef:DatabaseReference?=null
    private var eventListener:ValueEventListener?=null

    fun start(eventListener: ValueEventListener,reference: DatabaseReference){
        reference.addValueEventListener(eventListener)
        this.eventListener=eventListener
        this.dbRef=reference

    }
    fun clear(){
        eventListener?.let { dbRef?.removeEventListener(eventListener!!) }
        eventListener=null
        dbRef=null
    }

}

class FirebaseReferenceChildObserver{
    private var dbRef:DatabaseReference?=null
    private var eventListener:ChildEventListener?=null
    private var isObserving: Boolean = false

    fun start(eventListener: ChildEventListener,reference: DatabaseReference){
        Log.d("AddChats","Call FirebaseReferenceChildObserver")
        isObserving = true
        reference.addChildEventListener(eventListener)
        this.eventListener=eventListener
        this.dbRef=reference

    }
    fun clear(){
        eventListener?.let { dbRef?.removeEventListener(eventListener!!) }
        eventListener=null
        dbRef=null
    }
    fun isObserving(): Boolean{
        return this.isObserving
    }
}