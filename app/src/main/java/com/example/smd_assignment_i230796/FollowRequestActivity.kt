package com.example.smd_assignment_i230796

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FollowRequestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FollowRequestAdapter
    private val requestList = mutableListOf<User>()

    private val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_requests)

        recyclerView = findViewById(R.id.recyclerFollowRequests)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FollowRequestAdapter(this, requestList)
        recyclerView.adapter = adapter

        loadIncomingRequests()
    }

    private fun loadIncomingRequests() {
        if (currentUserId == null) return

        val requestsRef = databaseRef.child(currentUserId).child("followRequestsReceived")

        requestsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                requestList.clear()

                val totalRequests = snapshot.childrenCount
                if (totalRequests == 0L) {
                    adapter.notifyDataSetChanged()
                    return
                }

                for (child in snapshot.children) {
                    val senderId = child.key ?: continue

                    databaseRef.child(senderId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(userSnap: DataSnapshot) {
                            val user = userSnap.getValue(User::class.java)
                            if (user != null && !requestList.any { it.uid == user.uid }) {
                                requestList.add(user)
                                adapter.notifyDataSetChanged()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
