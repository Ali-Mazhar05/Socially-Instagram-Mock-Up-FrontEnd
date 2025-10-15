package com.example.smd_assignment_i230796

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Base64
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class you_notif : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.you_notif)

        val followReqManager: LinearLayout = findViewById(R.id.followRequestManagement)
        followReqManager.setOnClickListener {
            val intent = Intent(this, FollowRequestActivity::class.java)
            startActivity(intent)
        }


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



        //-------------bottom nav-----------------------------
            findViewById<ImageView>(R.id.iv_nav_home).setOnClickListener {
                startActivity(Intent(this, main_feed::class.java))
                overridePendingTransition(0, 0)
            }
            findViewById<ImageView>(R.id.iv_nav_search).setOnClickListener {
                startActivity(Intent(this, search_page::class.java))
                overridePendingTransition(0, 0)
            }
            findViewById<ImageView>(R.id.iv_nav_add).setOnClickListener {
                startActivity(Intent(this, AddChoiceActivity::class.java))
                overridePendingTransition(0, 0)
            }
            findViewById<ImageView>(R.id.iv_nav_heart).setOnClickListener {
                startActivity(Intent(this, following_notif::class.java))
                overridePendingTransition(0, 0)
            }
            val icprofile = findViewById<ImageView>(R.id.iv_your_profile)

            val userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser?.uid ?: "demoUser123")
            userRef.child("profileImage").get()
                .addOnSuccessListener { snapshot ->
                    val profileBase64 = snapshot.getValue(String::class.java)
                    if (!profileBase64.isNullOrEmpty()) {
                        try {
                            val bytes = Base64.decode(profileBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            icprofile.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            icprofile.setImageResource(R.drawable.profile)
                        }
                    } else {
                        icprofile.setImageResource(R.drawable.profile)
                    }
                }
                .addOnFailureListener {
                    icprofile.setImageResource(R.drawable.profile)
                }

            findViewById<ImageView>(R.id.iv_your_profile).setOnClickListener {
                startActivity(Intent(this, your_profile_screen::class.java))
                overridePendingTransition(0, 0)
            }




    }



}
