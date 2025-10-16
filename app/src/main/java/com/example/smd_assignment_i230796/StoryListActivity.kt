package com.example.smd_assignment_i230796

import Story
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class StoryListActivity : BaseActivity() {

    private lateinit var storyRecyclerView: RecyclerView
    private lateinit var storyAdapter: StoryAdapter
    private val userStories = mutableListOf<UserStory>()
    private lateinit var dbRef: DatabaseReference

    //fallback for testing rn, demoUser123 only for testing
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "demoUser123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_list)

        storyRecyclerView = findViewById(R.id.storyRecyclerView)
        storyRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        storyAdapter = StoryAdapter(this, userStories)
        storyRecyclerView.adapter = storyAdapter

        dbRef = FirebaseDatabase.getInstance().getReference("Stories")

        loadStories()
    }

    private fun loadStories() {
        dbRef.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                userStories.clear()
                val currentTime = System.currentTimeMillis()

                //Add your story placeholder first
                val yourStory = UserStory(userId = currentUserId, stories = mutableListOf())
                userStories.add(yourStory)

                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key ?: continue
                    val storyList = mutableListOf<Story>()

                    for (storySnapshot in userSnapshot.children) {
                        val story = storySnapshot.getValue(Story::class.java)
                        if (story != null) {
                            val storyTime = try {
                                java.time.Instant.parse(story.timestamp).toEpochMilli()
                            } catch (e: Exception) {
                                0L
                            }

                            // Keep only last 24-hour stories
                            if (currentTime - storyTime < 24 * 60 * 60 * 1000)
                                storyList.add(story)
                            else
                                storySnapshot.ref.removeValue()
                        }
                    }

                    if (storyList.isNotEmpty()) {
                        // If current user has stories, replace placeholder
                        if (userId == currentUserId) {
                            userStories[0] = UserStory(userId, storyList)
                        } else {
                            userStories.add(UserStory(userId, storyList))
                        }
                    }
                }

                storyAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StoryListActivity, "Failed to load stories", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
