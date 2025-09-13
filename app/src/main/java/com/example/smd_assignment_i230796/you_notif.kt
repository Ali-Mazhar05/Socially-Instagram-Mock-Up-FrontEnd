package com.example.smd_assignment_i230796

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class you_notif : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.you_notif)

        findViewById<AppCompatButton>(R.id.msg1)
            .setOnClickListener {
                startActivity(Intent(this, chat_screen::class.java))
                            overridePendingTransition(0,0)

            }
        findViewById<AppCompatButton>(R.id.msg2)
            .setOnClickListener {
                startActivity(Intent(this, chat_screen::class.java))
                            overridePendingTransition(0,0)

            }
        findViewById<AppCompatButton>(R.id.msg3)
            .setOnClickListener {
                startActivity(Intent(this, chat_screen::class.java))
                            overridePendingTransition(0,0)

            }

        findViewById<AppCompatButton>(R.id.follow1)
            .setOnClickListener {

                startActivity(Intent(this, kyan_colman_profile::class.java))
                            overridePendingTransition(0,0)
            }
        findViewById<AppCompatButton>(R.id.follow2)
            .setOnClickListener {
                startActivity(Intent(this, kyan_colman_profile::class.java))
                            overridePendingTransition(0,0)

            }
        findViewById<AppCompatButton>(R.id.follow3)
            .setOnClickListener {
                startActivity(Intent(this, kyan_colman_profile::class.java))
                overridePendingTransition(0,0)

            }

        findViewById<LinearLayout>(R.id.following_tab)
            .setOnClickListener {
                startActivity(Intent(this, following_notif::class.java))
                overridePendingTransition(0,0)
                finish()
            }

        var ichome=findViewById<ImageView>(R.id.iv_nav_home)
        var icsearch=findViewById<ImageView>(R.id.iv_nav_search)
        var icadd=findViewById<ImageView>(R.id.iv_nav_add)
        var icheart=findViewById<ImageView>(R.id.iv_nav_heart)
        var icprofile=findViewById<ImageView>(R.id.iv_your_profile)

        ichome.setOnClickListener{ startActivity(Intent(this, main_feed::class.java))

        }
        icsearch.setOnClickListener { startActivity(Intent(this, search_page::class.java))

        }
        icadd.setOnClickListener { startActivity(Intent(this, post_picture_screen::class.java))

        }
        icheart.setOnClickListener { startActivity(Intent(this, following_notif::class.java))
        }
        icprofile.setOnClickListener { startActivity(Intent(this, your_profile_screen::class.java))

        }



    }



}
