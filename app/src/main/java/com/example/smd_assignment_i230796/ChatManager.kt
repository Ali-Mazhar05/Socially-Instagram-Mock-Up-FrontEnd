package com.example.smd_assignment_i230796

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatManager {

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("chats")

    fun sendMessage(chatId: String, message: ChatMessage) {
        val msgKey = dbRef.child(chatId).push().key ?: return
        dbRef.child(chatId).child(msgKey).setValue(message.copy(messageId = msgKey))
    }

    fun editMessage(chatId: String, messageId: String, newText: String) {
        dbRef.child(chatId).child(messageId).child("text").setValue(newText)
        dbRef.child(chatId).child(messageId).child("edited").setValue(true)
    }

    fun deleteMessage(chatId: String, messageId: String) {
        dbRef.child(chatId).child(messageId).removeValue()
    }
}
