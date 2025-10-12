package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var userList: MutableList<User>

    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_chat)

        recyclerView = findViewById(R.id.recyclerUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        userList = mutableListOf()
        adapter = UserAdapter(userList) { selectedUser ->
            val receiverId = selectedUser.uid
            if (receiverId != null && receiverId.isNotEmpty()) {
                val intent = Intent(this, chat_screen::class.java)
                intent.putExtra("receiverUserId", receiverId)
                startActivity(intent)
                this.finish()
            } else {
                Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        fetchUsers()
    }

    private fun fetchUsers() {
        val currentUserId = auth.currentUser?.uid
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (userSnap in snapshot.children) {
                    val user = userSnap.getValue(User::class.java)
                    if (user != null && user.uid != currentUserId) {
                        userList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddChatActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
