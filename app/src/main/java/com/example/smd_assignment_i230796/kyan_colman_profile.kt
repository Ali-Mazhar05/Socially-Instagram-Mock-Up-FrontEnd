package com.example.smd_assignment_i230796

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class kyan_colman_profile : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvFullName: TextView
    private lateinit var ivProfilePic: CircleImageView
    private lateinit var tvPostsCount: TextView
    private lateinit var tvFollowersCount: TextView
    private lateinit var tvFollowingCount: TextView
    private lateinit var recyclerProfilePosts: RecyclerView
    private lateinit var btnFollow: androidx.appcompat.widget.AppCompatButton

    private lateinit var auth: FirebaseAuth
    private lateinit var usersRef: DatabaseReference
    private lateinit var postsRef: DatabaseReference

    private lateinit var postAdapter: ProfilePostAdapter
    private val postImages = mutableListOf<String>()

    private var visitedUserId: String? = null
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kyan_colman_profile)

        visitedUserId = intent.getStringExtra("visitedUserId")
        if (visitedUserId == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid
        usersRef = FirebaseDatabase.getInstance().getReference("Users")
        postsRef = FirebaseDatabase.getInstance().getReference("Posts")

        ivProfilePic = findViewById(R.id.ivProfilePic)
        tvUsername = findViewById(R.id.tvUsername)
        tvFullName = findViewById(R.id.tvFullName)
        tvPostsCount = findViewById(R.id.tvPostsCount)
        tvFollowersCount = findViewById(R.id.tvFollowersCount)
        tvFollowingCount = findViewById(R.id.tvFollowingCount)
        btnFollow = findViewById(R.id.stateButton)
        recyclerProfilePosts = findViewById(R.id.recyclerProfilePosts)

        recyclerProfilePosts.layoutManager = GridLayoutManager(this, 3)
        postAdapter = ProfilePostAdapter(postImages)
        recyclerProfilePosts.adapter = postAdapter

        loadVisitedUserInfo()
        loadVisitedUserPosts()
        setupFollowButton()
        setupBottomNav()
    }

    private fun loadVisitedUserInfo() {
        usersRef.child(visitedUserId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    tvUsername.text = "@${user.username ?: "username"}"
                    tvFullName.text = "${user.firstName ?: ""} ${user.lastName ?: ""}"

                    tvFollowersCount.text = (user.followers?.size ?: 0).toString()
                    tvFollowingCount.text = (user.following?.size ?: 0).toString()

                    if (!user.profileImage.isNullOrEmpty()) {
                        try {
                            val bytes = Base64.decode(user.profileImage, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            ivProfilePic.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadVisitedUserPosts() {
        postsRef.orderByChild("userId").equalTo(visitedUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postImages.clear()
                    for (postSnap in snapshot.children) {
                        val firstImageSnap = postSnap.child("imageBase64List").children.firstOrNull()
                        val imageBase64 = firstImageSnap?.getValue(String::class.java)

                        if (!imageBase64.isNullOrEmpty()) {
                            try {
                                val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                if (bitmap != null && bitmap.width > 0 && bitmap.height > 0) {
                                    postImages.add(imageBase64)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    postAdapter.notifyDataSetChanged()
                    tvPostsCount.text = postImages.size.toString()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun setupFollowButton() {
        val fm = FollowManager

        fm.isFollowing(currentUserId!!, visitedUserId!!) { isFollowing ->
            if (isFollowing) {
                btnFollow.text = "Following"
                btnFollow.setBackgroundResource(R.drawable.btn_following_bg)
                btnFollow.setOnClickListener {
                    fm.unfollow(currentUserId!!, visitedUserId!!) {
                        if (it) {
                            btnFollow.text = "Follow"
                            btnFollow.setBackgroundResource(R.drawable.btn_follow_bg)
                            updateCounts()
                        }
                    }
                }
            } else {
                fm.isRequestPending(currentUserId!!, visitedUserId!!) { iSentRequest ->
                    if (iSentRequest) {
                        btnFollow.text = "Requested"
                        btnFollow.setBackgroundResource(R.drawable.btn_requested_bg)
                        btnFollow.setOnClickListener {
                            fm.cancelFollowRequest(currentUserId!!, visitedUserId!!) {
                                if (it) {
                                    btnFollow.text = "Follow"
                                    btnFollow.setBackgroundResource(R.drawable.btn_follow_bg)
                                }
                            }
                        }
                    } else {
                        fm.isRequestPending(visitedUserId!!, currentUserId!!) { theySentRequest ->
                            if (theySentRequest) {
                                btnFollow.text = "Accept Request"
                                btnFollow.setBackgroundResource(R.drawable.btn_follow_bg)
                                btnFollow.setOnClickListener {
                                    fm.acceptFollowRequest(visitedUserId!!, currentUserId!!) {
                                        if (it) {
                                            btnFollow.text = "Following"
                                            btnFollow.setBackgroundResource(R.drawable.btn_following_bg)
                                            updateCounts()
                                            Toast.makeText(
                                                this@kyan_colman_profile,
                                                "Follow request accepted",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            } else {
                                btnFollow.text = "Follow"
                                btnFollow.setBackgroundResource(R.drawable.btn_follow_bg)
                                btnFollow.setOnClickListener {
                                    fm.sendFollowRequest(currentUserId!!, visitedUserId!!) {
                                        if (it) {
                                            btnFollow.text = "Requested"
                                            btnFollow.setBackgroundResource(R.drawable.btn_requested_bg)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateCounts() {
        usersRef.child(visitedUserId!!).child("followers").get().addOnSuccessListener {
            tvFollowersCount.text = (it.childrenCount).toString()
        }
        usersRef.child(currentUserId!!).child("following").get().addOnSuccessListener {
            tvFollowingCount.text = (it.childrenCount).toString()
        }
    }

    private fun setupBottomNav() {
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
                        icprofile.setImageResource(R.drawable.profile)
                    }
                } else {
                    icprofile.setImageResource(R.drawable.profile)
                }
            }.addOnFailureListener {
                icprofile.setImageResource(R.drawable.profile)
            }

        icprofile.setOnClickListener {
            startActivity(Intent(this, your_profile_screen::class.java))
            overridePendingTransition(0, 0)
        }
    }
}
