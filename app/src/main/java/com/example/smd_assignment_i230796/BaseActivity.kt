package com.example.smd_assignment_i230796

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

abstract class BaseActivity : AppCompatActivity() {

    private val userRef by lazy {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) FirebaseDatabase.getInstance().getReference("Users").child(uid) else null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPresenceTracking()
    }

    private fun setupPresenceTracking() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseDatabase.getInstance()
        val connectedRef = db.getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    userRef?.child("onlineStatus")?.setValue("online")
                  //  userRef?.child("lastSeen")?.setValue(System.currentTimeMillis())


                    userRef?.child("onlineStatus")?.onDisconnect()?.setValue("offline")
                  //  userRef?.child("lastSeen")?.onDisconnect()?.setValue(System.currentTimeMillis())
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onResume() {
        super.onResume()
        userRef?.child("onlineStatus")?.setValue("online")
    }

    override fun onPause() {
        super.onPause()
        userRef?.child("onlineStatus")?.setValue("offline")
       // userRef?.child("lastSeen")?.setValue(System.currentTimeMillis())
    }
}
