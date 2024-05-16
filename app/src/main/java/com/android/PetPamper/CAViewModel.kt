package com.android.PetPamper

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.android.PetPamper.data.COLLECTION_CHAT
import com.android.PetPamper.data.COLLECTION_MESSAGES
import com.android.PetPamper.data.COLLECTION_STATUS
import com.android.PetPamper.data.COLLECTION_USER
import com.android.PetPamper.data.ChatData
import com.android.PetPamper.data.ChatUser
import com.android.PetPamper.data.Event
import com.android.PetPamper.data.Message
import com.android.PetPamper.data.Status
import com.android.PetPamper.data.UserData
import com.android.PetPamper.database.FirebaseConnection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CAViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {

    val inProgress = mutableStateOf(false)
    val popupNotification = mutableStateOf<Event<String>?>(null)
    val signedIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)

    val chats = mutableStateOf<List<ChatData>>(listOf())
    val inProgressChats = mutableStateOf(false)

    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressChatMessages = mutableStateOf(false)
    var currentChatMessagesListener: ListenerRegistration? = null

    val status = mutableStateOf<List<Status>>(listOf())
    val inProgressStatus = mutableStateOf(false)

    init {
//        onLogout()
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        currentUser?.uid?.let { uid ->
            print("UUID $uid")
            getUserData(uid)
        }
    }


    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(COLLECTION_USER).document(uid)
            .addSnapshotListener { value, error ->
                if (error != null)
                    handleException(error, "Cannot retrieve user data")
                if (value != null) {
                    val user = value.toObject<UserData>()
                    userData.value = user
                    inProgress.value = false
                    populateChats()
                }
            }
    }

    private fun populateChats() {
        inProgressChats.value = true
        db.collection(COLLECTION_CHAT).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        )
            .addSnapshotListener { value, error ->
                if (error != null)
                    handleException(error)
                if (value != null)
                    chats.value = value.documents.mapNotNull { it.toObject<ChatData>() }
                inProgressChats.value = false
            }
    }


    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("ChatAppClone", "Chat app exception", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage: $errorMsg"
        popupNotification.value = Event(message)
        inProgress.value = false
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true

        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("image/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask
            .addOnSuccessListener {
                val result = it.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener(onSuccess)
                inProgress.value = false
            }
            .addOnFailureListener {
                handleException(it)
            }
    }



    fun onAddChat(number: String) {
        if (number.isEmpty() or !number.isDigitsOnly())
            handleException(customMessage = "Number must contain only digits")
        else {
            db.collection(COLLECTION_CHAT)
                .where(
                    Filter.or(
                        Filter.and(
                            Filter.equalTo("user1.phoneNumber", number),
                            Filter.equalTo("user2.phoneNumber", userData.value?.number)
                        ),
                        Filter.and(
                            Filter.equalTo("user1.phoneNumber", userData.value?.number),
                            Filter.equalTo("user2.phoneNumber", number)
                        )
                    )
                )
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        db.collection(COLLECTION_USER).whereEqualTo("phoneNumber", number)
                            .get()
                            .addOnSuccessListener {
                                if (it.isEmpty)
                                    handleException(customMessage = "Cannot retrieve user with number $number")
                                else {
                                    val chatPartner = it.toObjects<UserData>()[0]
                                    val id = db.collection(COLLECTION_CHAT).document().id
                                    val chat = ChatData(
                                        id,
                                        ChatUser(
                                            userData.value?.userId,
                                            userData.value?.name,
                                            userData.value?.imageUrl,
                                            userData.value?.number
                                        ),
                                        ChatUser(
                                            chatPartner.userId,
                                            chatPartner.name,
                                            chatPartner.imageUrl,
                                            chatPartner.number
                                        )
                                    )
                                    db.collection(COLLECTION_CHAT).document(id).set(chat)
                                }
                            }
                            .addOnFailureListener {
                                handleException(it)
                            }
                    } else {
                        handleException(customMessage = "Chat already exists")
                    }
                }
        }
    }



    fun onSendReply(chatId: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(userData.value?.userId, message, time)
        db.collection(COLLECTION_CHAT)
            .document(chatId)
            .collection(COLLECTION_MESSAGES)
            .document()
            .set(msg)
    }

    fun populateChat(chatId: String) {
        inProgressChatMessages.value = true
        currentChatMessagesListener = db.collection(COLLECTION_CHAT)
            .document(chatId)
            .collection(COLLECTION_MESSAGES)
            .addSnapshotListener { value, error ->
                if (error != null)
                    handleException(error)
                if (value != null)
                    chatMessages.value = value.documents
                        .mapNotNull { it.toObject<Message>() }
                        .sortedBy { it.timestamp }
                inProgressChatMessages.value = false
            }
    }

    fun depopulateChat() {
        chatMessages.value = listOf()
        currentChatMessagesListener = null
    }


}






