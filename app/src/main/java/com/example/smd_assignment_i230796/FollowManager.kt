package com.example.smd_assignment_i230796

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FollowManager {

    private val usersRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Users")

    // 🔹 Send a follow request
    fun sendFollowRequest(fromId: String, toId: String, onComplete: (Boolean) -> Unit) {
        if (fromId == toId) {
            onComplete(false)
            return
        }

        val updates = hashMapOf<String, Any>(
            "$fromId/followRequestsSent/$toId" to true,
            "$toId/followRequestsReceived/$fromId" to true
        )

        usersRef.updateChildren(updates).addOnCompleteListener {
            onComplete(it.isSuccessful)
        }
    }

    // 🔹 Cancel a follow request (withdraw before it's accepted)
    fun cancelFollowRequest(fromId: String, toId: String, onComplete: (Boolean) -> Unit) {
        val updates = hashMapOf<String, Any?>(
            "$fromId/followRequestsSent/$toId" to null,
            "$toId/followRequestsReceived/$fromId" to null
        )

        usersRef.updateChildren(updates).addOnCompleteListener {
            onComplete(it.isSuccessful)
        }
    }

    // 🔹 Accept a follow request
    fun acceptFollowRequest(fromId: String, toId: String, onComplete: (Boolean) -> Unit) {
        // fromId = requester, toId = receiver (the one accepting)
        val updates = hashMapOf<String, Any?>(
            "$fromId/followRequestsSent/$toId" to null,
            "$toId/followRequestsReceived/$fromId" to null,
            "$fromId/following/$toId" to true,
            "$toId/followers/$fromId" to true
        )

        usersRef.updateChildren(updates).addOnCompleteListener {
            onComplete(it.isSuccessful)
        }
    }

    // 🔹 Reject a follow request
    fun rejectFollowRequest(fromId: String, toId: String, onComplete: (Boolean) -> Unit) {
        val updates = hashMapOf<String, Any?>(
            "$fromId/followRequestsSent/$toId" to null,
            "$toId/followRequestsReceived/$fromId" to null
        )

        usersRef.updateChildren(updates).addOnCompleteListener {
            onComplete(it.isSuccessful)
        }
    }

    // 🔹 Unfollow someone
    fun unfollow(fromId: String, toId: String, onComplete: (Boolean) -> Unit) {
        val updates = hashMapOf<String, Any?>(
            "$fromId/following/$toId" to null,
            "$toId/followers/$fromId" to null
        )

        usersRef.updateChildren(updates).addOnCompleteListener {
            onComplete(it.isSuccessful)
        }
    }

    // 🔹 Add a user to close friends
    fun addCloseFriend(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {
        usersRef.child(userId).child("closeFriends").child(friendId)
            .setValue(true)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    // 🔹 Remove a user from close friends
    fun removeCloseFriend(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {
        usersRef.child(userId).child("closeFriends").child(friendId)
            .removeValue()
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    // 🔹 Check if current user is following another
    fun isFollowing(currentUserId: String, otherUserId: String, callback: (Boolean) -> Unit) {
        usersRef.child(currentUserId).child("following").child(otherUserId).get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.exists())
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // 🔹 Check if a follow request is pending
    fun isRequestPending(currentUserId: String, otherUserId: String, callback: (Boolean) -> Unit) {
        usersRef.child(currentUserId).child("followRequestsSent").child(otherUserId).get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.exists())
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // 🔹 Check if user is a close friend
    fun isCloseFriend(userId: String, friendId: String, callback: (Boolean) -> Unit) {
        usersRef.child(userId).child("closeFriends").child(friendId).get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.exists())
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}
