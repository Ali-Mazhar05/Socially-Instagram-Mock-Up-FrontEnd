package com.example.smd_assignment_i230796

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import Story
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class StoryViewerActivity : AppCompatActivity() {

    private lateinit var storyImage: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var username:TextView
    private var storyDate: TextView? = null
    private var progressBar: ProgressBar? = null

    private val storyList = mutableListOf<Story>()
    private var currentIndex = 0
    private lateinit var userId: String
    private var isMine = false

    private val handler = Handler(Looper.getMainLooper())
    private var progress = 0

    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "demoUser123" // fallback if no auth user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isMine = intent.getBooleanExtra("isMine", false)
        setContentView(if (isMine) R.layout.gursky_studio_story else R.layout.story_other)

        storyImage = findViewById(R.id.storyImage)
        storyDate = findViewById(R.id.storyDate)
        progressBar = findViewById(R.id.storyProgressBar)
        profileImage = findViewById(R.id.profileImage)
        username = findViewById(R.id.username)

        // 🔹 Get the userId whose stories we’re viewing
        userId = intent.getStringExtra("userId") ?: currentUserId

        // 🔹 Try to get username passed via Intent
        val passedUsername = intent.getStringExtra("username")

        // 🔹 Set username (fallback to "Your Story" or "Unknown User")
        username.text = when {
            !passedUsername.isNullOrEmpty() -> passedUsername
            isMine -> "Your Story"
            else -> "Unknown User"
        }

        // 🔹 Fetch profile image from Firebase if not your own
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileBase64 = snapshot.child("profileImage").getValue(String::class.java)
                if (!profileBase64.isNullOrEmpty()) {
                    try {
                        val bytes = Base64.decode(profileBase64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        profileImage.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        profileImage.setImageResource(R.drawable.profile)
                    }
                } else {
                    // Fallback if user doesn’t exist or has no profile
                    profileImage.setImageResource(R.drawable.profile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                profileImage.setImageResource(R.drawable.profile)
            }
        })

        // 🔹 Close button
        findViewById<ImageView>(R.id.closeBtn)?.setOnClickListener { finish() }

        // 🔹 Load stories for this user
        loadStories()
    }
    private fun loadStories() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Stories").child(userId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                storyList.clear()
                for (s in snapshot.children) {
                    val story = s.getValue(Story::class.java)
                    if (story != null) {
                        // Only show close-friends stories to the owner or close friends
                        //add logic to extend to show cf story to only real cfs
                        //rn everyone can view it
                        storyList.add(story)
                    }
                }

                if (storyList.isNotEmpty()) {
                    currentIndex = 0
                    showStory(currentIndex)
                } else {
                    Toast.makeText(this@StoryViewerActivity, "No stories available", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StoryViewerActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showStory(index: Int) {
        if (index >= storyList.size) {
            finish()
            return
        }

        val story = storyList[index]
        try {
            val bytes = Base64.decode(story.imageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            storyImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(story.timestamp)

            val relativeTime = DateUtils.getRelativeTimeSpanString(
                date?.time ?: 0L,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
            )

            storyDate?.text = relativeTime
        } catch (e: Exception) {
            storyDate?.text = ""
        }


        markViewed(story)
        startProgress(index)
    }

    private fun markViewed(story: Story) {
        val viewerId = currentUserId // actual Firebase user or demo fallback
        val dbRef = FirebaseDatabase.getInstance().getReference("Stories").child(userId)

        dbRef.orderByChild("timestamp").equalTo(story.timestamp)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        child.ref.child("viewedBy").child(viewerId).setValue(true)
                        child.ref.child("isViewed").setValue(true)
                    }

                    // Send broadcast to refresh feed or UI
                    val intent = Intent("com.example.smd_assignment_i230796.STORY_VIEWED")
                    intent.putExtra("userId", userId)
                    LocalBroadcastManager.getInstance(this@StoryViewerActivity).sendBroadcast(intent)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun startProgress(index: Int) {
        progress = 0
        handler.post(object : Runnable {
            override fun run() {
                progress += 2
                progressBar?.progress = progress
                if (progress >= 100) {
                    currentIndex++
                    showStory(currentIndex)
                } else handler.postDelayed(this, 100)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

//testing purpose

/*
package com.example.smd_assignment_i230796

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.database.*
import Story

class StoryViewerActivity : AppCompatActivity() {

    private lateinit var storyImage: ImageView
    private lateinit var storyTitle: TextView
    private var storyDate: TextView? = null
    private var progressBar: ProgressBar? = null

    private val storyList = mutableListOf<Story>()
    private var currentIndex = 0
    private lateinit var userId: String
    private var isMine = false

    private val handler = Handler(Looper.getMainLooper())
    private var progress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isMine = intent.getBooleanExtra("isMine", false)
        setContentView(if (isMine) R.layout.gursky_studio_story else R.layout.story_other)

        storyImage = findViewById(R.id.storyImage)
        storyTitle = findViewById(R.id.storyTitle)
        storyDate = findViewById(R.id.storyDate)
        progressBar = findViewById(R.id.storyProgressBar)

        userId = intent.getStringExtra("userId") ?: "demoUser123"

        findViewById<ImageView>(R.id.closeBtn)?.setOnClickListener { finish() }

        loadStories()
    }

    private fun loadStories() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Stories").child(userId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                storyList.clear()
                for (s in snapshot.children) {
                    val story = s.getValue(Story::class.java)
                    if (story != null) {
                        // Only show close friends stories to yourself
                        if (story.closeFriends && !isMine) continue
                        storyList.add(story)
                    }
                }
                if (storyList.isNotEmpty()) {
                    currentIndex = 0
                    showStory(currentIndex)
                } else {
                    Toast.makeText(this@StoryViewerActivity, "No stories available", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StoryViewerActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showStory(index: Int) {
        if (index >= storyList.size) {
            finish()
            return
        }

        val story = storyList[index]
        try {
            val bytes = Base64.decode(story.imageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            storyImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        storyTitle.text = if (isMine) "Your Story" else userId.take(6)
        storyDate?.text = story.timestamp ?: "Unknown"

        markViewed(story)
        startProgress(index)
    }

    private fun markViewed(story: Story) {
        // testing w demo user
        val viewerId = "demoUser123"

        val dbRef = FirebaseDatabase.getInstance().getReference("Stories").child(userId)
        dbRef.orderByChild("timestamp").equalTo(story.timestamp)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        child.ref.child("viewedBy").child(viewerId).setValue(true)
                        child.ref.child("isViewed").setValue(true)
                    }

                    // Local broadcast to update feed or UI
                    val intent = Intent("com.example.smd_assignment_i230796.STORY_VIEWED")
                    intent.putExtra("userId", userId)
                    LocalBroadcastManager.getInstance(this@StoryViewerActivity).sendBroadcast(intent)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun startProgress(index: Int) {
        progress = 0
        handler.post(object : Runnable {
            override fun run() {
                progress += 2
                progressBar?.progress = progress
                if (progress >= 100) {
                    currentIndex++
                    showStory(currentIndex)
                } else handler.postDelayed(this, 100)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
*/


