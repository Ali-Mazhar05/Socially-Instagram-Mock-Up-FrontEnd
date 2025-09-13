package com.example.smd_assignment_i230796

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.hdodenhof.circleimageview.CircleImageView

class main_feed : AppCompatActivity() {

    // Bottom navigation
    private lateinit var ivNavHome: ImageView
    private lateinit var ivNavSearch: ImageView
    private lateinit var ivNavAdd: ImageView
    private lateinit var ivNavHeart: ImageView
    private lateinit var ivNavProfile: CircleImageView

    // Stories
    private lateinit var ivYourStory: LinearLayout
    private lateinit var ivStory1: LinearLayout
    private lateinit var ivStory2: LinearLayout
    private lateinit var ivStory3: LinearLayout
    private lateinit var ivStory4: LinearLayout

    // Post UI
    private lateinit var ivShare: ImageView
    private lateinit var tvPostCaption: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_feed)

        // Initialize bottom nav
        ivNavHome = findViewById(R.id.iv_nav_home)
        ivNavSearch = findViewById(R.id.iv_nav_search)
        ivNavAdd = findViewById(R.id.iv_nav_add)
        ivNavHeart = findViewById(R.id.iv_nav_heart)
        ivNavProfile = findViewById(R.id.iv_your_profile)

        // Post UI
        ivShare = findViewById(R.id.iv_share)
        tvPostCaption = findViewById(R.id.igpost_caption)

        // Stories
        ivYourStory = findViewById(R.id.iv_your_story)
        ivStory1 = findViewById(R.id.iv_story_1)
        ivStory2 = findViewById(R.id.iv_story_2)
        ivStory3 = findViewById(R.id.iv_story_3)
        ivStory4 = findViewById(R.id.iv_story_4)

        // Set a sample caption
        setPostCaption("joshua_l", "The game in Japan was amazing and I want to share some photos")



        ivNavSearch.setOnClickListener {
            startActivity(Intent(this, search_page::class.java))
            overridePendingTransition(0,0)
        }

        ivNavAdd.setOnClickListener {
            startActivity(Intent(this, post_picture_screen::class.java))
                        overridePendingTransition(0,0)
        }

        ivNavHeart.setOnClickListener {
            startActivity(Intent(this, following_notif::class.java))
                        overridePendingTransition(0,0)
        }

        ivNavProfile.setOnClickListener {
            startActivity(Intent(this, your_profile_screen::class.java))
                        overridePendingTransition(0,0)
        }

        // Story clickers
        ivYourStory.setOnClickListener {
            startActivity(Intent(this, story_screen::class.java))
                        overridePendingTransition(0,0)
        }

        ivStory1.setOnClickListener { openStory("story1") }
        ivStory2.setOnClickListener { openStory("story2") }
        ivStory3.setOnClickListener { openStory("story3") }
        ivStory4.setOnClickListener { openStory("story4") }

        // Camera clicker
        findViewById<ImageView>(R.id.iv_camera).setOnClickListener {
            startActivity(Intent(this, add_to_story::class.java))
            overridePendingTransition(0,0)
        }

        // Share button
        ivShare.setOnClickListener {
            startActivity(Intent(this, dm_feed::class.java))
            overridePendingTransition(0,0)
        }
    }

    /**
     * Function to set a caption with bold username
     */
    private fun setPostCaption(username: String, caption: String) {
        val fullText = "$username $caption"
        val spannable = SpannableString(fullText)

        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            username.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvPostCaption.text = spannable
    }

    /**
     * Opens another user's story
     */
    private fun openStory(storyId: String) {
        val intent = Intent(this, story_other::class.java)
        intent.putExtra("storyId", storyId)
        startActivity(intent)
        overridePendingTransition(0,0)
    }
}
