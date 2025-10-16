package com.example.smd_assignment_i230796

// User.kt
data class User(
    var uid: String? = null,
    var username: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var dob: String? = null,
    var profileImage: String? = null,


    // 🔹 Relationship fields
    var followers: MutableMap<String, Boolean>? = null,            // userId → true
    var following: MutableMap<String, Boolean>? = null,            // userId → true
    var followRequestsSent: MutableMap<String, Boolean>? = null,   // pending requests sent
    var followRequestsReceived: MutableMap<String, Boolean>? = null, // pending requests received
    var closeFriends: MutableMap<String, Boolean>? = null,     // close friends


    var onlineStatus: String? = null,
    var lastSeen: Long? = null
)

