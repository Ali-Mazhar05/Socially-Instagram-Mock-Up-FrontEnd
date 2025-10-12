package com.example.smd_assignment_i230796

data class Chat(
    val chatId: String = "",
    val userId1: String = "",
    val userId2: String = "",
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
