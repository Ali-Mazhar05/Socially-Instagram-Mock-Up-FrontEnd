package com.example.smd_assignment_i230796

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserAdapter(
    private val userList: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.txtUsername)
        val lastMessage: TextView = itemView.findViewById(R.id.txtLastMessage)
        val timestamp: TextView = itemView.findViewById(R.id.txtTimestamp)
        val profileImage: ImageView = itemView.findViewById(R.id.imgProfile)
        val cameraButton: ImageButton = itemView.findViewById(R.id.btnCamera)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_preview, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        holder.username.text = user.username ?: "Unknown"

        // Load profile image
        Glide.with(holder.itemView.context)
            .load(user.profileImage ?: R.drawable.jack_profile)
            .into(holder.profileImage)

        // Fetch latest message
        loadLastMessage(user.uid, holder.lastMessage, holder.timestamp)

        // When clicking user item
        holder.itemView.setOnClickListener { onUserClick(user) }

        // Camera button opens chat with camera intent
        holder.cameraButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, chat_screen::class.java)
            intent.putExtra("receiverUserId", user.uid)
            intent.putExtra("openCamera", true)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = userList.size

    private fun loadLastMessage(receiverId: String?, lastMsgView: TextView, timeView: TextView) {
        if (receiverId == null) return

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("Chats")

        // Filter chats between current user and this receiver
        dbRef.orderByChild("timestamp").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var lastMsg: String? = null
                var lastTime: Long = 0L

                for (msgSnap in snapshot.children) {
                    val msg = msgSnap.getValue(ChatMessage::class.java)
                    if (msg != null) {
                        val isRelevant = (msg.senderId == currentUserId && msg.receiverId == receiverId) ||
                                (msg.senderId == receiverId && msg.receiverId == currentUserId)
                        if (isRelevant && msg.timestamp > lastTime) {
                            lastMsg = if (!msg.text.isNullOrEmpty()) msg.text else "📷 Image"
                            lastTime = msg.timestamp
                        }
                    }
                }

                if (lastMsg != null) {
                    lastMsgView.text = lastMsg
                    timeView.text = formatTime(lastTime)
                } else {
                    lastMsgView.text = "No messages yet"
                    timeView.text = ""
                }
            }

        })
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}

