package com.example.smd_assignment_i230796

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.hdodenhof.circleimageview.CircleImageView

class main_feed : AppCompatActivity() {

    // Bottom navigation
    lateinit var ivNavHome: ImageView
    lateinit var ivNavSearch: ImageView
    lateinit var ivNavAdd: ImageView
    lateinit var ivNavHeart: ImageView
    lateinit var ivNavProfile: CircleImageView

    lateinit var ivShare: ImageView
    // Example post caption
    lateinit var tvPostCaption: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_feed) // make sure your XML file name matches

        // Initialize bottom nav views
        ivNavHome = findViewById(R.id.iv_nav_home)
        ivNavSearch = findViewById(R.id.iv_nav_search)
        ivNavAdd = findViewById(R.id.iv_nav_add)
        ivNavHeart = findViewById(R.id.iv_nav_heart)
        ivNavProfile = findViewById(R.id.iv_your_profile) // fixed ID

        ivShare= findViewById(R.id.iv_share)
        // Initialize post caption
        tvPostCaption = findViewById(R.id.igpost_caption)

        // Example: Bold username + normal caption
        setPostCaption("joshua_l", "The game in Japan was amazing and I want to share some photos")

        // Example: Click listeners for nav icons
        ivNavHome.setOnClickListener {
            // TODO: Load Home fragment
        }

        ivShare.setOnClickListener {
            var intent=Intent(this, dm_feed::class.java)
            startActivity(intent)
            finish()
        }

        ivNavSearch.setOnClickListener {
            var intent=Intent(this, search_page::class.java)
            startActivity(intent)
            finish()
        }

        ivNavAdd.setOnClickListener {
            // TODO: Open Add Post screen
        }

        ivNavHeart.setOnClickListener {
            // TODO: Show Notifications
        }

        ivNavProfile.setOnClickListener {
            // TODO: Open Profile
        }
    }

    /**
     * Function to set a caption with bold username
     */
    fun setPostCaption(username: String, caption: String) {
        val fullText = "$username $caption"
        val spannable = SpannableString(fullText)

        // Bold only the username part
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            username.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvPostCaption.text = spannable
    }
}
