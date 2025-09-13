package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class story_other : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story_other)
        findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.closeBtn).setOnClickListener { finish()
            overridePendingTransition(0,0)
        }
        findViewById<ImageView>(R.id.ic_messenger).setOnClickListener {

            startActivity(Intent(this, dm_feed::class.java))
            overridePendingTransition(0,0)
        }

    }
}