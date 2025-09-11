package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smd_assignment_i230796.R


class story_screen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story_screen)   // make sure your XML file is named story_screen.xml

        // Top bar
        val closeBtn: ImageView = findViewById(R.id.closeBtn) // give the close button an id in XML (e.g. android:id="@+id/closeBtn")
        val storyTitle: TextView = findViewById(R.id.storyTitle)
        val storyDate: TextView = findViewById(R.id.storyDate)
        val storySong: TextView = findViewById(R.id.storySong)

        // Bottom navigation buttons
        val navActivity: ImageView = findViewById(R.id.navActivity)
        val navCreate: ImageView = findViewById(R.id.navCreate)
        val navCreatePost: ImageView = findViewById(R.id.navCreatePost)
        val navBrowse: ImageView = findViewById(R.id.navBrowse)
        val navSend: ImageView = findViewById(R.id.navSend)
        val navMore: ImageView = findViewById(R.id.navMore)

        // Close button exits activity
        closeBtn.setOnClickListener {
            finish() // closes this screen
        }

        // Example click listeners for bottom nav buttons
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

        // You can also attach clicks to title/song if you want
        storySong.setOnClickListener {

        }
    }
}
