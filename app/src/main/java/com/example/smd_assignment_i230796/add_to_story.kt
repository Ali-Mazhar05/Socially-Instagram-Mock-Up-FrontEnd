package com.example.smd_assignment_i230796

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class add_to_story : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.add_to_story)

        val addtostoryclose=findViewById<ImageView>(R.id.add_to_story_close)
        val addtostorypost=findViewById<ImageView>(R.id.add_to_story_post)
        val closefriends=findViewById<LinearLayout>(R.id.close_friends)
        val yourstory=findViewById<LinearLayout>(R.id.your_story)

        addtostorypost.setOnClickListener { finish()
                    overridePendingTransition(0,0)}
        addtostoryclose.setOnClickListener { finish()
                    overridePendingTransition(0,0)}
        closefriends.setOnClickListener { finish()
                    overridePendingTransition(0,0)}
        yourstory.setOnClickListener{ finish()
                    overridePendingTransition(0,0)}

    }
}