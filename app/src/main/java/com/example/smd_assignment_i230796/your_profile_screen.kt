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

        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.edit_profile)
            .setOnClickListener {
                            overridePendingTransition(0,0)
                startActivity(Intent(this, edit_profile_screen::class.java))
                overridePendingTransition(0,0)
            }

        val addStory = findViewById<ImageView>(R.id.your_profile_addtostory)
        val friends = findViewById<ImageView>(R.id.your_profile_friends)
        val sports = findViewById<ImageView>(R.id.your_profile_sports)
        val design = findViewById<ImageView>(R.id.your_profile_design)


        addStory.setOnClickListener {
            startActivity(Intent(this, story_preview::class.java))
                        overridePendingTransition(0,0)

        }

        friends.setOnClickListener {
            startActivity(Intent(this, story_screen::class.java))
                        overridePendingTransition(0,0)

        }
        sports.setOnClickListener {
            startActivity(Intent(this, story_other::class.java))
                        overridePendingTransition(0,0)

        }
        design.setOnClickListener {
            startActivity(Intent(this, story_other::class.java))
                        overridePendingTransition(0,0)

        }

        findViewById<CircleImageView>(R.id.ivProfilePic).setOnClickListener{
            startActivity(Intent(this, gursky_studio_story::class.java))
            overridePendingTransition(0,0)
        }

        //bottom nav
        var ichome=findViewById<ImageView>(R.id.iv_nav_home)
        var icsearch=findViewById<ImageView>(R.id.iv_nav_search)
        var icadd=findViewById<ImageView>(R.id.iv_nav_add)
        var icheart=findViewById<ImageView>(R.id.iv_nav_heart)
        var icprofile=findViewById<ImageView>(R.id.iv_your_profile)

        ichome.setOnClickListener{ startActivity(Intent(this, main_feed::class.java))
                        overridePendingTransition(0,0)

        }
        icsearch.setOnClickListener { startActivity(Intent(this, search_page::class.java))
                        overridePendingTransition(0,0)

        }
        icadd.setOnClickListener { startActivity(Intent(this, post_picture_screen::class.java))
                        overridePendingTransition(0,0)

        }
        icheart.setOnClickListener { startActivity(Intent(this, following_notif::class.java))
                        overridePendingTransition(0,0)
             }
        icprofile.setOnClickListener { startActivity(Intent(this, your_profile_screen::class.java))
                        overridePendingTransition(0,0)

        }

    }
}
