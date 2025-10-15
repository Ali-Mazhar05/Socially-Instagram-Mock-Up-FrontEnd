package com.example.smd_assignment_i230796

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class your_profile_screen : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvFullName: TextView
    private lateinit var ivProfilePic: CircleImageView
    private lateinit var tvPostsCount: TextView
    private lateinit var tvFollowersCount: TextView
    private lateinit var tvFollowingCount: TextView
    private lateinit var recyclerProfilePosts: RecyclerView

    private lateinit var auth: FirebaseAuth
    private lateinit var usersRef: DatabaseReference
    private lateinit var postsRef: DatabaseReference

    private lateinit var postAdapter: ProfilePostAdapter
    private val postImages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.your_profile_screen)

        // Firebase setup
        auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser?.uid
        usersRef = FirebaseDatabase.getInstance().getReference("Users")
        postsRef = FirebaseDatabase.getInstance().getReference("Posts")

        // Initialize views
        ivProfilePic = findViewById(R.id.ivProfilePic)
        tvUsername = findViewById(R.id.tvUsername)
        tvFullName = findViewById(R.id.tvFullName)
        tvPostsCount = findViewById(R.id.tvPostsCount)
        tvFollowersCount = findViewById(R.id.tvFollowersCount)
        tvFollowingCount = findViewById(R.id.tvFollowingCount)
        val ivMenu = findViewById<ImageView>(R.id.ivMenu)

        // RecyclerView setup
        recyclerProfilePosts = findViewById(R.id.recyclerProfilePosts)
        recyclerProfilePosts.layoutManager = GridLayoutManager(this, 3)
        postAdapter = ProfilePostAdapter(postImages)
        recyclerProfilePosts.adapter = postAdapter

        // Load user data
        if (currentUserId != null) {
            loadCurrentUserInfo(currentUserId)
            loadUserPosts(currentUserId)
        }

        // --- Edit Profile ---
        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.edit_profile).setOnClickListener {
            startActivity(Intent(this, edit_profile_screen::class.java))
            overridePendingTransition(0, 0)
        }

        // --- Story Shortcuts ---
        findViewById<ImageView>(R.id.your_profile_addtostory).setOnClickListener {
            startActivity(Intent(this, story_preview::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<ImageView>(R.id.your_profile_friends).setOnClickListener {
            startActivity(Intent(this, story_screen::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<ImageView>(R.id.your_profile_sports).setOnClickListener {
            startActivity(Intent(this, story_other::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<ImageView>(R.id.your_profile_design).setOnClickListener {
            startActivity(Intent(this, story_other::class.java))
            overridePendingTransition(0, 0)
        }

        ivProfilePic.setOnClickListener {
            startActivity(Intent(this, gursky_studio_story::class.java))
            overridePendingTransition(0, 0)
        }

        // --- Bottom Navigation ---
        findViewById<ImageView>(R.id.iv_nav_home).setOnClickListener {
            startActivity(Intent(this, main_feed::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<ImageView>(R.id.iv_nav_search).setOnClickListener {
            startActivity(Intent(this, search_page::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<ImageView>(R.id.iv_nav_add).setOnClickListener {
            startActivity(Intent(this, AddChoiceActivity::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<ImageView>(R.id.iv_nav_heart).setOnClickListener {
            startActivity(Intent(this, following_notif::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<ImageView>(R.id.iv_your_profile).setOnClickListener {
            startActivity(Intent(this, your_profile_screen::class.java))
            overridePendingTransition(0, 0)
        }

        val icprofile = findViewById<ImageView>(R.id.iv_your_profile)

        val userRef = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: "demoUser123")
        userRef.child("profileImage").get()
            .addOnSuccessListener { snapshot ->
                val profileBase64 = snapshot.getValue(String::class.java)
                if (!profileBase64.isNullOrEmpty()) {
                    try {
                        val bytes = Base64.decode(profileBase64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        icprofile.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        icprofile.setImageResource(R.drawable.profile)
                    }
                } else {
                    icprofile.setImageResource(R.drawable.profile)
                }
            }
            .addOnFailureListener {
                icprofile.setImageResource(R.drawable.profile)
            }



    }

    // --- Load user info ---
    private fun loadCurrentUserInfo(userId: String) {
        usersRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    tvUsername.text = "@${user.username ?: "username"}"
                    tvFullName.text = "${user.firstName ?: ""} ${user.lastName ?: ""}"

                    tvFollowersCount.text = (user.followers?.size ?: 0).toString()
                    tvFollowingCount.text = (user.following?.size ?: 0).toString()

                    // Decode Base64 profile image if exists
                    if (!user.profileImage.isNullOrEmpty()) {
                        try {
                            val imageBytes = Base64.decode(user.profileImage, Base64.DEFAULT)
                            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            ivProfilePic.setImageBitmap(decodedImage)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // --- Load user posts dynamically into RecyclerView ---
    private fun loadUserPosts(userId: String) {
        postsRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postImages.clear()

                    for (postSnap in snapshot.children) {
                        // Get the first image from imageBase64List only
                        val firstImageSnap = postSnap.child("imageBase64List").children.firstOrNull()
                        val imageBase64 = firstImageSnap?.getValue(String::class.java)

                        if (!imageBase64.isNullOrEmpty()) {
                            try {
                                // Try decoding to check if image is valid
                                val imageBytes = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT)
                                val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                                if (bitmap != null && bitmap.width > 0 && bitmap.height > 0) {
                                    postImages.add(imageBase64) // Only add valid decoded image
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                // Skip invalid image
                            }
                        }
                    }

                    // Update adapter and post count
                    postAdapter.notifyDataSetChanged()
                    tvPostsCount.text = postImages.size.toString()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}
