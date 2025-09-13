package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class kyan_colman_profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kyan_colman_profile)

        findViewById<ImageView>(R.id.ivProfilePic).setOnClickListener {
            startActivity(
                Intent(this, gursky_studio_story::class.java))
            overridePendingTransition(0,0)

        }
        findViewById<ImageView>(R.id.kyan_highlight1).setOnClickListener {
            startActivity(
                Intent(this, story_screen::class.java))
            overridePendingTransition(0,0)

        }
        findViewById<ImageView>(R.id.kyan_highlight2).setOnClickListener {
            startActivity(
                Intent(this, story_other::class.java))
            overridePendingTransition(0,0)

        }
        findViewById<ImageView>(R.id.kyan_highlight3).setOnClickListener {
            startActivity(
                Intent(this, story_other::class.java))
            overridePendingTransition(0,0)

        }
        findViewById<ImageView>(R.id.kyan_highlight4).setOnClickListener {
            startActivity(
                Intent(this, story_other::class.java))
            overridePendingTransition(0,0)

        }
        findViewById<ImageView>(R.id.kyan_highlight5).setOnClickListener {
            startActivity(
                Intent(this, story_preview::class.java))
            overridePendingTransition(0,0)

        }


        findViewById<ImageView>(R.id.back).setOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }

        var ichome=findViewById<ImageView>(R.id.iv_nav_home)
        var icsearch=findViewById<ImageView>(R.id.iv_nav_search)
        var icadd=findViewById<ImageView>(R.id.iv_nav_add)
        var icheart=findViewById<ImageView>(R.id.iv_nav_heart)
        var icprofile=findViewById<ImageView>(R.id.iv_your_profile)



        ichome.setOnClickListener{ startActivity(Intent(this, main_feed::class.java))
                        overridePendingTransition(0,0)
            finish()
                        overridePendingTransition(0,0)
        }
        icsearch.setOnClickListener { startActivity(Intent(this, search_page::class.java))
                        overridePendingTransition(0,0)
            finish()
                        overridePendingTransition(0,0)
        }
        icadd.setOnClickListener { startActivity(Intent(this, post_picture_screen::class.java))
                        overridePendingTransition(0,0)
            finish()
                        overridePendingTransition(0,0)
        }
        icheart.setOnClickListener { startActivity(Intent(this, following_notif::class.java))
                        overridePendingTransition(0,0)
            finish()
                    overridePendingTransition(0,0)}
        icprofile.setOnClickListener { startActivity(Intent(this, your_profile_screen::class.java))
                        overridePendingTransition(0,0)
            finish()
                        overridePendingTransition(0,0)
        }

    }
}