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
import androidx.activity.result.contract.ActivityResultContracts
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


    private val addChoiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val addType = result.data!!.getStringExtra("addType")
                if (addType == "post") {

                    pickImagesLauncher.launch("image/*")
                }
            }
        }




    //----------bottom navigation-----------------------
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
            val intent = Intent(this, AddChoiceActivity::class.java)
            addChoiceLauncher.launch(intent)
            overridePendingTransition(0, 0)
        }

        icprofile.setOnClickListener {
            startActivity(Intent(this, your_profile_screen::class.java))
            overridePendingTransition(0,0)
        }
    }



    //--------------------stories---------------------------
    private fun refreshStories() {
        userStoryList.clear()
        setupStoriesRecycler()
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
                                timestamp = "2025-10-14T0${5 + u}:00:00Z",
                                caption = "User$u public story",
                                closeFriends = false,
                                viewedBy = mapOf("viewer1" to false)
                            )
                            tempStories.add(st)
                            publicStories["user$u"] = tempStories
                        }

                        //4 close friends users
                        val closeStories = mutableMapOf<String, List<Story>>()
                        for (u in 6..9) {
                            val tempStories = mutableListOf<Story>()
                            val bmp = BitmapFactory.decodeResource(context.resources, demoImages.random())
                            val baos = ByteArrayOutputStream()
                            bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos)
                            val b64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

                            val st = Story(
                                imageBase64 = b64,
                                timestamp = "2025-10-14T0${u}:00:00Z",
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

    // -------------------- Posts --------------------

    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (!uris.isNullOrEmpty()) openImagePreview(uris)
        }

    private fun openImagePreview(imageUris: List<Uri>) {
        val intent = Intent(this, post_preview::class.java)
        intent.putStringArrayListExtra("imageUris", ArrayList(imageUris.map { it.toString() }))
        postPreviewLauncher.launch(intent)
    }

    private val postPreviewLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val uris = result.data!!.getStringArrayListExtra("imageUris")?.map { Uri.parse(it) } ?: emptyList()
                val caption = result.data!!.getStringExtra("caption") ?: ""
                addNewPost(uris, caption)
            }
        }

    private fun addNewPost(imageUris: List<Uri>, caption: String) {
        val newPost = Post(
            username = "You",
            location = "Pakistan",
            caption = caption,
            imageUris = imageUris,
            profileResId = R.drawable.profile,
            likedByProfileResId = 0,
            likedByName = null,
            likeCount = 0,
            isVerified = true,
            paginationIconResId = null,
            isLiked = false,
            comments = mutableListOf()
        )
        posts.add(0, newPost)
        postAdapter.notifyItemInserted(0)
        binding.rvPosts.scrollToPosition(0)
    }

    private fun setupPostsRecycler() {
        posts.clear()
        posts.addAll(
            listOf(
            Post(
                username = "alex_jones",
                location = "New York, USA",
                caption = "Morning coffee vibes ☕🌅",
                imageResIds = listOf(R.drawable.post_picture_screen_1),
                profileResId = R.drawable.craig_profile,
                likedByProfileResId = R.drawable.jack_profile,
                likedByName = "emma_watson",
                likeCount = 321,
                isVerified = true,
                paginationIconResId = null,
                isLiked = false,
                comments = mutableListOf(
                    comment(R.drawable.profile, "john_doe", "That looks so peaceful!"),
                    comment(R.drawable.profile_karenne, "sarah_k", "Love this shot 🔥")
                )
            ),

            Post(
                username = "sophia_lee",
                location = "Maldives",
                caption = "Paradise found 🏝️✨",
                imageResIds = listOf(
                    R.drawable.post_picture_screen_2,
                    R.drawable.post_picture_screen_3
                ),
                profileResId = R.drawable.jack_profile,
                likedByProfileResId = R.drawable.craig_profile,
                likedByName = "alex_jones",
                likeCount = 452,
                isVerified = true,
                paginationIconResId = R.drawable.main_feed_pagination,
                isLiked = true
            ),

            // 🐾 Post 3 — 3 images, a few comments
            Post(
                username = "petlover_mia",
                location = "London, UK",
                caption = "Playtime with these cuties 🐶🐾",
                imageResIds = listOf(
                    R.drawable.post_picture_screen_4,
                    R.drawable.post_picture_screen_5,
                    R.drawable.post_picture_screen_6
                ),
                profileResId = R.drawable.craig_profile,
                likedByProfileResId = R.drawable.post_profile,
                likedByName = "lucas_park",
                likeCount = 198,
                isVerified = false,
                paginationIconResId = R.drawable.main_feed_pagination,
                isLiked = false,
                comments = mutableListOf(
                    comment(R.drawable.profile_karenne, "sophia_lee", "Omg they’re adorable 😍"),
                    comment(R.drawable.jack_profile, "emma_watson", "Cutest post today! 🐕❤️")
                )
            ),

            // 🚴 Post 4 — Single image
            Post(
                username = "lucas_park",
                location = "Amsterdam, Netherlands",
                caption = "City rides and clear skies 🚴‍♂️☀️",
                imageResIds = listOf(R.drawable.post_picture_screen_7),
                profileResId = R.drawable.post_profile,
                likedByProfileResId = R.drawable.profile_karenne,
                likedByName = "petlover_mia",
                likeCount = 265,
                isVerified = true,
                paginationIconResId = null,
                isLiked = true
            ),
            Post(
                username = "emma_watson",
                location = "Paris, France",
                caption = "Lost in the rhythm 🎧💫",
                imageResIds = listOf(R.drawable.post_picture_screen_8),
                profileResId = R.drawable.kieron_profile,
                likedByProfileResId = R.drawable.jack_profile,
                likedByName = "alex_jones",
                likeCount = 589,
                isVerified = true,
                paginationIconResId = null,
                isLiked = false,
                comments = mutableListOf(
                    comment(R.drawable.craig_profile, "lucas_park", "That vibe tho 🔥🔥"),
                    comment(R.drawable.profile_karenne, "sophia_lee", "Paris fits your style 💕")
                )
            )
        )
        )

        postAdapter = PostAdapter(posts)
        binding.rvPosts.layoutManager = LinearLayoutManager(this)
        binding.rvPosts.adapter = postAdapter
    }

}
