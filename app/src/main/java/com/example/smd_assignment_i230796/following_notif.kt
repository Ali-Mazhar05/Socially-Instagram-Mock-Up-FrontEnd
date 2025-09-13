package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class following_notif : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.following_notif)

        val youtab=findViewById<LinearLayout>(R.id.you_tab)
        youtab.setOnClickListener{
            startActivity(Intent(this, you_notif::class.java))
            overridePendingTransition(0,0)
            finish()
        }

    //bottom nav
        var ichome=findViewById<ImageView>(R.id.iv_nav_home)
        var icsearch=findViewById<ImageView>(R.id.iv_nav_search)
        var icadd=findViewById<ImageView>(R.id.iv_nav_add)
        var icheart=findViewById<ImageView>(R.id.iv_nav_heart)
        var icprofile=findViewById<ImageView>(R.id.iv_your_profile)

        ichome.setOnClickListener{ startActivity(Intent(this, main_feed::class.java))
                        overridePendingTransition(0,0)
        finish()
        }
        icsearch.setOnClickListener { startActivity(Intent(this, search_page::class.java))
                        overridePendingTransition(0,0)
            finish()
        }
        icadd.setOnClickListener { startActivity(Intent(this, post_picture_screen::class.java))
                        overridePendingTransition(0,0)
            finish()
        }
        icheart.setOnClickListener { startActivity(Intent(this, following_notif::class.java))
                        overridePendingTransition(0,0)
            finish() }
        icprofile.setOnClickListener { startActivity(Intent(this, your_profile_screen::class.java))
                        overridePendingTransition(0,0)
            finish()
        }


    }
}
