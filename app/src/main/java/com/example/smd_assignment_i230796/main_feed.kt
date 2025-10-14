package com.example.smd_assignment_i230796

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smd_assignment_i230796.databinding.MainFeedBinding

class main_feed : AppCompatActivity() {

    private lateinit var binding: MainFeedBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>()

    // -------------------- Multiple Image Picker --------------------
    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (!uris.isNullOrEmpty()) openImagePreview(uris)
        }

    // -------------------- Result from Preview --------------------
    private val postPreviewLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val uris = result.data!!.getStringArrayListExtra("imageUris")?.map { Uri.parse(it) } ?: emptyList()
                val caption = result.data!!.getStringExtra("caption") ?: ""
                addNewPost(uris, caption)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStoriesRecycler()
        setupPostsRecycler()
        setupTopBarButtons()
        bottomNav()
    }

    // -------------------- 🔝 Top Bar Buttons --------------------
    private fun setupTopBarButtons() {
        val cameraBtn = findViewById<ImageView>(R.id.iv_camera)
        val messageBtn = findViewById<ImageView>(R.id.iv_share)
        val shareBtn = findViewById<ImageView>(R.id.iv_message)

        // 📸 Camera opens post picture screen
        cameraBtn.setOnClickListener {
            startActivity(Intent(this, post_picture_screen::class.java))
            overridePendingTransition(0, 0)
        }

        // 💬 Message opens chat screen
        messageBtn.setOnClickListener {
            startActivity(Intent(this, dm_feed::class.java))
            overridePendingTransition(0, 0)
        }

        // 📤 Share opens Android share sheet
        shareBtn.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out Socially App!")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }

    // -------------------- Bottom Navigation --------------------
    private fun bottomNav() {
        val ichome = findViewById<ImageView>(R.id.iv_nav_home)
        val icsearch = findViewById<ImageView>(R.id.iv_nav_search)
        val icadd = findViewById<ImageView>(R.id.iv_nav_add)
        val icheart = findViewById<ImageView>(R.id.iv_nav_heart)
        val icprofile = findViewById<ImageView>(R.id.iv_your_profile)

        ichome.setOnClickListener {
            startActivity(Intent(this, main_feed::class.java))
            overridePendingTransition(0, 0)
        }

        icsearch.setOnClickListener {
            startActivity(Intent(this, search_page::class.java))
            overridePendingTransition(0, 0)
        }

        icadd.setOnClickListener {
            pickImagesLauncher.launch("image/*")
        }

        icheart.setOnClickListener {
            startActivity(Intent(this, following_notif::class.java))
            overridePendingTransition(0, 0)
        }

        icprofile.setOnClickListener {
            startActivity(Intent(this, your_profile_screen::class.java))
            overridePendingTransition(0, 0)
        }
    }

    // -------------------- Stories --------------------
    private fun setupStoriesRecycler() {
        val stories = listOf(
            Story("Your Story", R.drawable.profile, true),
            Story("Ali", R.drawable.main_profile_circle, false),
            Story("Karenne", R.drawable.karenne_profile, true),
            Story("Jack", R.drawable.jack_profile, false),
            Story("Craig", R.drawable.craig_circle_profile, false)
        )

        storyAdapter = StoryAdapter(stories)
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@main_feed, LinearLayoutManager.HORIZONTAL, false)
            adapter = storyAdapter
        }
    }

    // -------------------- Posts --------------------
    private fun setupPostsRecycler() {
        posts.addAll(
            listOf(
                Post(
                    username = "Ali",
                    location = "Islamabad, Pakistan",
                    caption = "Having a great day at Margalla Hills!",
                    imageResIds = listOf(R.drawable.post_main, R.drawable.post_picture_screen_4),
                    profileResId = R.drawable.profile,
                    likedByProfileResId = R.drawable.craig_profile,
                    likedByName = "craig_love",
                    likeCount = 1520,
                    isVerified = true,
                    isLiked = true,
                    paginationIconResId = R.drawable.main_feed_pagination,
                    comments = mutableListOf(
                        comment(R.drawable.craig_profile, "craig_love", "Looks amazing!"),
                        comment(R.drawable.karenne_profile, "karenne_travels", "Love Margalla views ❤️")
                    )
                ),
                Post(
                    username = "Jack",
                    location = "Karachi, Pakistan",
                    caption = "Food festival fun at Port Grand!",
                    imageResIds = listOf(R.drawable.post_picture_screen_2),
                    profileResId = R.drawable.jack_profile,
                    likedByProfileResId = R.drawable.jack_profile,
                    likedByName = "jack_travels",
                    likeCount = 2398,
                    isVerified = true,
                    paginationIconResId = null,
                    isLiked = false,
                    comments = mutableListOf(
                        comment(R.drawable.craig_profile, "craig_love", "Yummy 😋"),
                        comment(R.drawable.profile, "you_user", "I went there too!")
                    )
                ),
                Post(
                    username = "Karenne",
                    location = "Hunza Valley, Gilgit-Baltistan",
                    caption = "Can’t believe how breathtaking Hunza is 😍",
                    imageResIds = listOf(R.drawable.post_picture_screen_3, R.drawable.post_picture_screen_4),
                    profileResId = R.drawable.karenne_profile,
                    likedByProfileResId = R.drawable.profile,
                    likedByName = "ali_travels",
                    likeCount = 1876,
                    isVerified = true,
                    paginationIconResId = R.drawable.main_feed_pagination,
                    isLiked = false,
                    comments = mutableListOf(
                        comment(R.drawable.jack_profile, "jack_travels", "Dream destination!"),
                        comment(R.drawable.craig_profile, "craig_love", "I want to go there too!")
                    )
                ),
                Post(
                    username = "Craig",
                    location = "Lahore, Pakistan",
                    caption = "Street food night! Nothing beats Lahore’s taste 🔥",
                    imageResIds = listOf(R.drawable.post_picture_screen_5),
                    profileResId = R.drawable.craig_profile,
                    likedByProfileResId = R.drawable.karenne_profile,
                    likedByName = "karenne_travels",
                    likeCount = 3240,
                    isVerified = false,
                    paginationIconResId = null,
                    isLiked = false,
                    comments = mutableListOf(
                        comment(R.drawable.profile, "you_user", "Now I’m hungry 😭"),
                        comment(R.drawable.jack_profile, "jack_travels", "Best food city ever!")
                    )
                ),
                Post(
                    username = "Zain",
                    location = "Gwadar, Balochistan",
                    caption = "Golden hour by the sea 🌅 — peace and serenity.",
                    imageResIds = listOf(R.drawable.post_picture_screen_6, R.drawable.post_picture_screen_2),
                    profileResId = R.drawable.main_profile_circle,
                    likedByProfileResId = R.drawable.profile,
                    likedByName = "ali_travels",
                    likeCount = 980,
                    isVerified = true,
                    paginationIconResId = R.drawable.main_feed_pagination,
                    isLiked = true,
                    comments = mutableListOf(
                        comment(R.drawable.craig_profile, "craig_love", "That sunset 😍"),
                        comment(R.drawable.karenne_profile, "karenne_travels", "So calm and beautiful!")
                    )
                )
            )
        )

        postAdapter = PostAdapter(posts)
        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(this@main_feed)
            adapter = postAdapter
        }
    }

    // -------------------- Open Image Preview --------------------
    private fun openImagePreview(imageUris: List<Uri>) {
        val intent = Intent(this, post_preview::class.java)
        intent.putStringArrayListExtra("imageUris", ArrayList(imageUris.map { it.toString() }))
        postPreviewLauncher.launch(intent)
    }

    // -------------------- Add New Post --------------------
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
}
