package com.example.smd_assignment_i230796

import Story
import android.content.*
import android.graphics.Bitmap
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
        TopBar()

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


    private fun TopBar(){
        findViewById<ImageView>(R.id.iv_share).setOnClickListener {
            startActivity(Intent(this, dm_feed::class.java))
            overridePendingTransition(0,0)
        }
    }


    //----------bottom navigation-----------------------
    private fun bottomNav() {

        val icadd = findViewById<ImageView>(R.id.iv_nav_add)
        val icprofile = findViewById<ImageView>(R.id.iv_your_profile)

        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId)
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
                        } catch (_: Exception) {
                            0L
                        }

                        // Keep only valid stories (less than 24h)
                        if (now - storyTime < 86_400_000) {
                            stories.add(story)
                        } else st.ref.removeValue()
                    }

                    if (stories.isNotEmpty()) userStoryList.add(UserStory(uid, stories))
                }

                // Sort: unviewed first
                val sorted = userStoryList.sortedWith(compareByDescending<UserStory> {
                    it.stories.any { s -> s.viewedBy?.get(currentUser) != true }
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



    // -------------------- Posts --------------------

    private val addChoiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val addType = result.data!!.getStringExtra("addType")
                if (addType == "post") {

                    pickImagesLauncher.launch("image/*")
                }
            }
        }

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

    fun addNewPost(imageUris: List<Uri>, caption: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = user.uid
        val usersRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)

        usersRef.get().addOnSuccessListener { snapshot ->
            val username = snapshot.child("username").getValue(String::class.java) ?: "Unknown User"
            val location = snapshot.child("location").getValue(String::class.java) ?: " "
            val profileImageUrl = snapshot.child("profileImage").getValue(String::class.java) ?: ""

            // Convert URIs to Base64
            val imageBase64List = mutableListOf<String>()
            for (uri in imageUris) {
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                    val base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
                    imageBase64List.add(base64)
                    inputStream?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            //Create a new post reference and assign its postId
            val postRef = FirebaseDatabase.getInstance().getReference("Posts").push()
            val postId = postRef.key ?: return@addOnSuccessListener

            val newPost = Post(
                postId = postId,
                userId = uid,
                username = username,
                location = location,
                caption = caption,
                imageBase64List = imageBase64List,
                profileImageUrl = profileImageUrl,
                likedByName = null,
                likeCount = 0,
                isVerified = false,
                comments = mutableListOf()
            )

            // ✅ Save to Firebase
            postRef.setValue(newPost)
                .addOnSuccessListener {
                    Toast.makeText(this, "✅ Post uploaded successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "❌ Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadPostsFromFirebase() {
        val postsRef = FirebaseDatabase.getInstance().getReference("Posts")
        postsRef.get().addOnSuccessListener { snapshot ->
            posts.clear()
            for (postSnapshot in snapshot.children) {
                val post = postSnapshot.getValue(Post::class.java)
                if (post != null) posts.add(0, post)
            }
            postAdapter.notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(this, "❌ Failed to load posts: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setupPostsRecycler() {
        val postsRef = FirebaseDatabase.getInstance().getReference("Posts")
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid ?: ""

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                posts.clear()

                if (snapshot.exists() && snapshot.childrenCount > 0) {
                    //Load posts from Firebase
                    for (postSnapshot in snapshot.children) {
                        val post = postSnapshot.getValue(Post::class.java)
                        if (post != null) {
                            post.isLiked = post.likedBy?.get(currentUserId) == true
                            posts.add(0, post)
                        }
                    }
                } else {
                    Toast.makeText(this@main_feed,"No Posts in database", Toast.LENGTH_SHORT)
                }


                if (!::postAdapter.isInitialized) {
                    var currentUsername = "You"
                    var currentUserProfileBase64 = ""
                    val currentUserId = currentUser?.uid ?: ""
                    val usersRef = FirebaseDatabase.getInstance().getReference("Users")

                    fun setupAdapter() {
                        postAdapter = PostAdapter(
                            this@main_feed,
                            posts,
                            currentUsername,
                            currentUserProfileBase64
                        )
                        binding.rvPosts.layoutManager = LinearLayoutManager(this@main_feed)
                        binding.rvPosts.adapter = postAdapter
                    }

                    if (currentUserId.isNotEmpty()) {

                        usersRef.child(currentUserId).get()
                            .addOnSuccessListener { userSnapshot ->
                                if (userSnapshot.exists()) {
                                    currentUsername = userSnapshot.child("username").getValue(String::class.java) ?: "You"
                                    currentUserProfileBase64 =
                                        userSnapshot.child("profileImage").getValue(String::class.java) ?: ""
                                }
                                setupAdapter()
                            }
                            .addOnFailureListener {
                                setupAdapter()
                            }
                    } else {
                        setupAdapter()
                    }
                } else {
                    postAdapter.notifyDataSetChanged()
                }
            }


                override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@main_feed,
                    "Failed to load posts: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}
