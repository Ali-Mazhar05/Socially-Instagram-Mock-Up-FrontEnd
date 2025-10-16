package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smd_assignment_i230796.R
import de.hdodenhof.circleimageview.CircleImageView


class story_screen : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story_screen)

        val closeBtn: CircleImageView = findViewById(R.id.closeBtn)
        val storyTitle: TextView = findViewById(R.id.storyTitle)
        // Top bar
        val storyDate: TextView = findViewById(R.id.storyDate)
        val storySong: TextView = findViewById(R.id.storySong)

        // Bottom navigation buttons
        val navActivity: ImageView = findViewById(R.id.navActivity)
        val navCreate: ImageView = findViewById(R.id.navCreate)
        val navCreatePost: ImageView = findViewById(R.id.navCreatePost)
        val navBrowse: ImageView = findViewById(R.id.navBrowse)
        val navSend: ImageView = findViewById(R.id.navSend)
        val navMore: ImageView = findViewById(R.id.navMore)


        closeBtn.setOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }


        navActivity.setOnClickListener {
            // Replace with real navigation

        }

        navCreate.setOnClickListener {

        }

        navCreatePost.setOnClickListener {

        }

        navBrowse.setOnClickListener {

        }

        navSend.setOnClickListener {

        }

        navMore.setOnClickListener {

        }


        storySong.setOnClickListener {

        }
    }
}
