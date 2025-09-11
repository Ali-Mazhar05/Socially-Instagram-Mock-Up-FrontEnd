package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.hdodenhof.circleimageview.CircleImageView

class your_profile_screen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.your_profile_screen) // replace with your XML filename


        val ivMenu = findViewById<ImageView>(R.id.ivMenu)
        val tvUsername = findViewById<TextView>(R.id.tvUsername)


        val addStory = findViewById<ImageView>(R.id.your_profile_addtostory)
        val friends = findViewById<ImageView>(R.id.your_profile_friends)
        val sports = findViewById<ImageView>(R.id.your_profile_sports)
        val design = findViewById<ImageView>(R.id.your_profile_design)





        // ----------------- Bottom Navigation -----------------
        val ivHome = findViewById<ImageView>(R.id.iv_nav_home)
        val ivSearch = findViewById<ImageView>(R.id.iv_nav_search)
        val ivAdd = findViewById<ImageView>(R.id.iv_nav_add)
        val ivHeart = findViewById<ImageView>(R.id.iv_nav_heart)
        val ivProfile = findViewById<CircleImageView>(R.id.iv_your_profile)

        ivHome.setOnClickListener {
            startActivity(Intent(this, main_feed::class.java))
            finish()
        }

        ivSearch.setOnClickListener {
            startActivity(Intent(this, search_page::class))
            finish()
        }

        ivAdd.setOnClickListener {


        }

        ivHeart.setOnClickListener {


        }

        ivProfile.setOnClickListener {


        }
    }
}
