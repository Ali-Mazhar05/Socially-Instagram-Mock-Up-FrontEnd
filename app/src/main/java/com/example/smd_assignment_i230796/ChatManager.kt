package com.example.smd_assignment_i230796

import Chat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatManager {

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("chats")

    fun createChatIfNotExists(chatId: String, user1: String, user2: String) {
        val chatRef = dbRef.child(chatId)
        chatRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                val chat = Chat(
                    chatId = chatId,
                    userId1 = user1,
                    userId2 = user2,
                    lastMessage = "",
                )
                chatRef.setValue(chat)
            }
        }
    }

    fun sendMessage(chatId: String, message: ChatMessage) {
        val msgKey = dbRef.child(chatId).child("messages").push().key ?: return
        val msgWithId = message.copy(messageId = msgKey)
        dbRef.child(chatId).child("messages").child(msgKey).setValue(msgWithId)
        // update chat metadata
        dbRef.child(chatId).child("lastMessage").setValue(msgWithId.text)
        dbRef.child(chatId).child("timestamp").setValue(msgWithId.timestamp)
    }

    fun editMessage(chatId: String, messageId: String, newText: String) {
        dbRef.child(chatId).child("messages").child(messageId).child("text").setValue(newText)
        dbRef.child(chatId).child("messages").child(messageId).child("edited").setValue(true)
        // optionally update chat lastMessage (if you want)
        dbRef.child(chatId).child("lastMessage").setValue(newText)
    }

    fun deleteMessage(chatId: String, messageId: String) {
        dbRef.child(chatId).child("messages").child(messageId).removeValue()
    }
}
