package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class gursky_studio_story : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.gursky_studio_story)


        var closebtn=findViewById<ImageView>(R.id.closeBtn)

        closebtn.setOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }

        findViewById<ImageView>(R.id.navCreate).setOnClickListener { startActivity(Intent(this,
            post_picture_screen::class.java))
            overridePendingTransition(0,0)
        }

    }
}