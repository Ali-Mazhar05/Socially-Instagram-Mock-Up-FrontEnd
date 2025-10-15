package com.example.smd_assignment_i230796

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView

class StoryAdapter(private val context: Context, private val stories: MutableList<UserStory>) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImg: CircleImageView = view.findViewById(R.id.storyProfileImage)
        val username: TextView = view.findViewById(R.id.storyUsername)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val userStory = stories[position]
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "demoUser123"
        val firstStory = userStory.stories.firstOrNull()

        var username: String? = null
        var profileImageBase64: String? = null

        //CASE 1: Current user's story
        if (userStory.userId == currentUserId) {
            holder.username.text = "Your Story"

            // 🔹 Load current user's profileImageBase64 and username from Firebase Users node
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId)
            userRef.get()
                .addOnSuccessListener { snapshot ->
                    val userMap = snapshot.value as? Map<*, *>
                    username = userMap?.get("username") as? String ?: "Your Story"
                    profileImageBase64 = userMap?.get("profileImage") as? String

                    holder.username.text = username

                    if (!profileImageBase64.isNullOrEmpty()) {
                        try {
                            val bytes = Base64.decode(profileImageBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            holder.profileImg.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            holder.profileImg.setImageResource(R.drawable.profile)
                        }
                    } else {
                        holder.profileImg.setImageResource(R.drawable.profile)
                    }

                    holder.profileImg.borderColor = getBorderColor(userStory, currentUserId)

                    //add_to_story if empty, else open StoryViewer
                    holder.itemView.setOnClickListener {
                        val intent = if (userStory.stories.isEmpty())
                            Intent(context, add_to_story::class.java)
                        else
                            Intent(context, StoryViewerActivity::class.java).apply {
                                putExtra("userId", userStory.userId)
                                putExtra("isMine", true)
                                putExtra("username", username)
                            }
                        context.startActivity(intent)
                    }
                }
                .addOnFailureListener {
                    holder.profileImg.setImageResource(R.drawable.profile)
                    holder.username.text = "Your Story"
                }

            return
        }

        // CASE 2: Other users’ stories
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userStory.userId)
        userRef.get()
            .addOnSuccessListener { snapshot ->
                val userMap = snapshot.value as? Map<*, *>
                username = userMap?.get("username") as? String ?: "User ${position}"
                profileImageBase64 = userMap?.get("profileImage") as? String

                holder.username.text = username

                if (!profileImageBase64.isNullOrEmpty()) {
                    try {
                        val bytes = Base64.decode(profileImageBase64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        holder.profileImg.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        holder.profileImg.setImageResource(R.drawable.profile)
                    }
                } else if (!firstStory?.profileImage.isNullOrEmpty()) {
                    try {
                        val bytes = Base64.decode(firstStory.profileImage, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        holder.profileImg.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        holder.profileImg.setImageResource(R.drawable.profile)
                    }
                } else {
                    holder.profileImg.setImageResource(R.drawable.profile)
                }

                holder.profileImg.borderColor = getBorderColor(userStory, currentUserId)

                // 👁 Click to open Story Viewer
                holder.itemView.setOnClickListener {
                    markViewedInFirebase(userStory, currentUserId)

                    // Instantly update viewed status in memory
                    userStory.stories.forEach { story ->
                        story.viewedBy = story.viewedBy?.plus(currentUserId to true)
                    }
                    notifyItemChanged(position)

                    val intent = Intent(context, StoryViewerActivity::class.java).apply {
                        putExtra("userId", userStory.userId)
                        putExtra("isMine", false)
                        putExtra("username", username)
                    }
                    context.startActivity(intent)
                }
            }
            .addOnFailureListener {
                holder.username.text = "User ${position}"
                holder.profileImg.setImageResource(R.drawable.profile)
            }
    }

    private fun getBorderColor(userStory: UserStory, currentUser: String): Int {
        val stories = userStory.stories
        if (stories.isEmpty()) return Color.GRAY

        val anyUnviewedCloseFriend = stories.any { it.closeFriends && (it.viewedBy == null || it.viewedBy?.get(currentUser) != true) }
        val anyUnviewedNormal = stories.any { !it.closeFriends && (it.viewedBy == null || it.viewedBy?.get(currentUser) != true) }

        return when {
            anyUnviewedCloseFriend -> Color.parseColor("#34C759") //green for close friends
            anyUnviewedNormal -> Color.parseColor("#DD2A7B") //pinkish-magenta
            else -> Color.GRAY //viewed
        }
    }


    private fun markViewedInFirebase(userStory: UserStory, currentUser: String?) {
        if (currentUser == null) return

        val baseRef = FirebaseDatabase.getInstance().getReference("Stories")
        val userRef = baseRef.child(userStory.userId)

        userStory.stories.forEach { story ->
            userRef.orderByChild("timestamp").equalTo(story.timestamp)
                .addListenerForSingleValueEvent(object :
                    com.google.firebase.database.ValueEventListener {
                    override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                        for (child in snapshot.children) {
                            child.ref.child("viewedBy").child(currentUser).setValue(true)
                        }
                    }

                    override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
                })
        }
    }

    override fun getItemCount() = stories.size
}
