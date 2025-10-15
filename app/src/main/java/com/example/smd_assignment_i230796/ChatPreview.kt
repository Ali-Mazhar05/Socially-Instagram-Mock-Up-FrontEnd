package com.example.smd_assignment_i230796

data class ChatPreview(
    val chatId: String = "",
    val otherUserId: String = "",
    val username: String = "",
    val profileImage: String? = null,
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L
)

