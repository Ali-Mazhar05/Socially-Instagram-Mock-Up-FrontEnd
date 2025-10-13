package com.example.smd_assignment_i230796

import Story
import android.content.*
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.*
import android.util.Base64
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smd_assignment_i230796.databinding.MainFeedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream

class main_feed : AppCompatActivity() {

    private lateinit var binding: MainFeedBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>()
    private val userStoryList = mutableListOf<UserStory>()
    //fallback on demoUser123 for testing
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "demoUser123"

    //Enhanced receiver: instantly adds new story locally
    private val storyUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val uid = intent?.getStringExtra("uid") ?: return
            val closeFriends = intent.getBooleanExtra("closeFriends", false)
            val imageBase64 = intent.getStringExtra("imageBase64") ?: ""
            val timestamp = intent.getStringExtra("timestamp") ?: ""

            // Find or create the user's story group
            val existingUserStory = userStoryList.find { it.userId == uid }
                ?: UserStory(uid, mutableListOf()).also { userStoryList.add(0, it) }

            // Add the new story entry
            val newStory = Story(
                imageBase64 = imageBase64,
                timestamp = timestamp,
                caption = "No caption",
                closeFriends = closeFriends,
                isViewed = false,
                viewedBy = mapOf()
            )

            existingUserStory.stories =
                existingUserStory.stories.toMutableList().apply { add(newStory) }

            // 🔹 Update instantly in RecyclerView
            updateStoryAdapter()
        }
    }

    private val storyViewedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val userId = intent?.getStringExtra("userId") ?: return
            val currentUser = currentUserId

            userStoryList.find { it.userId == userId }?.let { userStory ->
                userStory.stories.forEach { story ->
                    story.viewedBy = story.viewedBy?.plus(currentUser to true)
                }
            }

            // Resort: unviewed first
            userStoryList.sortByDescending { us ->
                us.stories.any { s -> s.viewedBy?.get(currentUser) != true }
            }

            updateStoryAdapter()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStoriesRecycler()
        setupPostsRecycler()
        bottomNav()
        addDemoDataOnce(this)

        //Register STORY_UPDATED receiver
        val updateFilter = IntentFilter("com.example.smd_assignment_i230796.STORY_UPDATED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(storyUpdateReceiver, updateFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(storyUpdateReceiver, updateFilter)
        }

        // Register STORY_VIEWED receiver
        val viewedFilter = IntentFilter("com.example.smd_assignment_i230796.STORY_VIEWED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(storyViewedReceiver, viewedFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(storyViewedReceiver, viewedFilter)
        }
    }



    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("STORY_UPDATED")
        LocalBroadcastManager.getInstance(this).registerReceiver(storyUpdateReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(storyUpdateReceiver)
        } catch (_: Exception) { }
    }



    private fun refreshStories() {
        userStoryList.clear()
        setupStoriesRecycler()
    }

    private fun bottomNav() {
        val icadd = findViewById<ImageView>(R.id.iv_nav_add)
        val icprofile = findViewById<ImageView>(R.id.iv_your_profile)
        findViewById<ImageView>(R.id.iv_nav_search).setOnClickListener {
            startActivity(Intent(this, search_page::class.java))
            overridePendingTransition(0,0)
        }
        findViewById<ImageView>(R.id.iv_nav_heart).setOnClickListener {
            startActivity(Intent(this, following_notif::class.java))
            overridePendingTransition(0,0)
        }

        icadd.setOnClickListener {
            startActivity(Intent(this, AddChoiceActivity::class.java))
            overridePendingTransition(0,0)
        }
        icprofile.setOnClickListener {
            startActivity(Intent(this, your_profile_screen::class.java))
            overridePendingTransition(0,0)
        }
    }

    //Load stories (public & close friends)
    private fun setupStoriesRecycler() {
        userStoryList.clear()
        val storiesRef = FirebaseDatabase.getInstance().getReference("Stories")

        storiesRef.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                userStoryList.clear()
                val now = System.currentTimeMillis()
                val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: "demoUser123"

                for (userNode in snapshot.children) {
                    val uid = userNode.key ?: continue
                    val stories = mutableListOf<Story>()

                    for (st in userNode.children) {
                        val story = st.getValue(Story::class.java) ?: continue
                        val storyTime = try {
                            java.time.Instant.parse(story.timestamp).toEpochMilli()
                        } catch (_: Exception) { 0L }

                        if (now - storyTime < 86_400_000) {
                            stories.add(story)
                        } else st.ref.removeValue() // delete old
                    }

                    if (stories.isNotEmpty()) userStoryList.add(UserStory(uid, stories))
                }

                //sort: unviewed first (normal/close), viewed last
                val sorted = userStoryList.sortedWith(compareByDescending<UserStory> {
                    it.stories.any { s ->
                        s.viewedBy?.get(currentUser) != true
                    }
                })

                userStoryList.clear()
                userStoryList.addAll(sorted)
                updateStoryAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@main_feed, "❌ Error loading stories: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateStoryAdapter() {
        val combined = mutableListOf<UserStory>()

        // Always show "Your Story" placeholder
        val yourStory = userStoryList.find { it.userId == currentUserId }
            ?: UserStory(currentUserId, mutableListOf())
        combined.add(yourStory)

        combined.addAll(userStoryList.filter { it.userId != currentUserId })

        storyAdapter = StoryAdapter(this, combined)
        binding.rvStories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvStories.adapter = storyAdapter
    }

    private fun setupPostsRecycler() {
        posts.clear()
        posts.addAll(
            listOf(
                Post("Ali", "Karachi", "Beach coffee ☕🌊",
                    listOf(Uri.parse("https://picsum.photos/seed/1/800/1000"),Uri.parse("https://picsum.photos/seed/1/800/1000")), null,
                    R.drawable.profile, R.drawable.craig_profile, "craig_love", 121, true,
                    R.drawable.main_feed_pagination, false),
                Post("Hina", "Lahore", "Skyline 🌇",
                    listOf(Uri.parse("https://picsum.photos/seed/2/800/1000")), null,
                    R.drawable.jack_profile, R.drawable.craig_profile, "fatima_khan", 93, false,
                    R.drawable.main_feed_pagination, false)
            )
        )
        postAdapter = PostAdapter(posts)
        binding.rvPosts.layoutManager = LinearLayoutManager(this)
        binding.rvPosts.adapter = postAdapter
    }

    //Adds demo data ONCE (5 normal + 4 close friends)
    private fun addDemoDataOnce(context: Context) {
        val storiesRef = FirebaseDatabase.getInstance().getReference("Stories")
        storiesRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                Thread {
                    try {
                        val demoImages = listOf(
                            R.drawable.post_picture_screen_1,
                            R.drawable.post_picture_screen_2,
                            R.drawable.post_picture_screen_3,
                            R.drawable.post_picture_screen_4
                        )

                        //5 normal users
                        val publicStories = mutableMapOf<String, List<Story>>()
                        for (u in 1..5) {
                            val tempStories = mutableListOf<Story>()
                            val bmp = BitmapFactory.decodeResource(context.resources, demoImages.random())
                            val baos = ByteArrayOutputStream()
                            bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos)
                            val b64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

                            val st = Story(
                                imageBase64 = b64,
                                timestamp = "2025-10-13T0${5 + u}:00:00Z",
                                caption = "User$u public story",
                                closeFriends = false,
                                viewedBy = mapOf("viewer1" to false)
                            )
                            tempStories.add(st)
                            publicStories["user$u"] = tempStories
                        }

                        // 🟡 4 close friends users
                        val closeStories = mutableMapOf<String, List<Story>>()
                        for (u in 6..9) {
                            val tempStories = mutableListOf<Story>()
                            val bmp = BitmapFactory.decodeResource(context.resources, demoImages.random())
                            val baos = ByteArrayOutputStream()
                            bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos)
                            val b64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

                            val st = Story(
                                imageBase64 = b64,
                                timestamp = "2025-10-13T0${u}:00:00Z",
                                caption = "User$u close story",
                                closeFriends = true,
                                viewedBy = mapOf("viewer1" to false)
                            )
                            tempStories.add(st)
                            publicStories["user$u"] = tempStories
                        }

                        storiesRef.setValue(publicStories)


                        runOnUiThread {
                            Toast.makeText(context, "✅ Demo stories added", Toast.LENGTH_SHORT).show()
                            refreshStories()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            } else {
                refreshStories()
            }
        }
    }
}
