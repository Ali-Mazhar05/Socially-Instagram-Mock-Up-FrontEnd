package com.example.smd_assignment_i230796

// ChatMessage.kt
data class ChatMessage(
    val messageId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: String? = null, // base64 string (or URL if you move to storage)
    val edited: Boolean = false
)

