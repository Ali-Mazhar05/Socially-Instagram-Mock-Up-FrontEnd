package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class dm_feed : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dm_feed)


        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnAdd = findViewById<ImageView>(R.id.btnAdd)

        //chats
        val chat1 = findViewById<LinearLayout>(R.id.chat1)
        val chat2 = findViewById<LinearLayout>(R.id.chat2)
        val chat3 = findViewById<LinearLayout>(R.id.chat3)
        val chat4 = findViewById<LinearLayout>(R.id.chat4)
        val chat5 = findViewById<LinearLayout>(R.id.chat5)
        val chat6 = findViewById<LinearLayout>(R.id.chat6)

        //chatcam
        val chatcam1=findViewById<ImageView>(R.id.chat_cam1)
        val chatcam2=findViewById<ImageView>(R.id.chat_cam2)
        val chatcam3=findViewById<ImageView>(R.id.chat_cam3)
        val chatcam4=findViewById<ImageView>(R.id.chat_cam4)
        val chatcam5=findViewById<ImageView>(R.id.chat_cam5)
        val chatcam6=findViewById<ImageView>(R.id.chat_cam6)

        //bottom cam
        val imgBottomCamera = findViewById<ImageView>(R.id.imgBottomCamera)

        chatcam1.setOnClickListener { startActivity(Intent(this, post_picture_screen::class.java))
                    overridePendingTransition(0,0)
        }
        chatcam2.setOnClickListener { startActivity(Intent(this, post_picture_screen::class.java))
                    overridePendingTransition(0,0)
        }
        chatcam3.setOnClickListener { startActivity(Intent(this, post_picture_screen::class.java))
                    overridePendingTransition(0,0)
        }
        chatcam4.setOnClickListener { startActivity(Intent(this, post_picture_screen::class.java))
                    overridePendingTransition(0,0)
        }
        chatcam5.setOnClickListener { startActivity(Intent(this, post_picture_screen::class.java))
                    overridePendingTransition(0,0)
        }
        chatcam6.setOnClickListener { startActivity(Intent(this, post_picture_screen::class.java))
                    overridePendingTransition(0,0)
        }


        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }

        btnAdd.setOnClickListener {
            Toast.makeText(this, "Add new chat", Toast.LENGTH_SHORT).show()
        }

        chat1.setOnClickListener {
            startActivity(Intent(this, chat_screen::class.java))
                        overridePendingTransition(0,0)
        }

        chat2.setOnClickListener {
            startActivity(Intent(this, chat_screen::class.java))
                        overridePendingTransition(0,0)
        }
        chat3.setOnClickListener {
            startActivity(Intent(this, chat_screen::class.java))
                        overridePendingTransition(0,0)
        }

        chat4.setOnClickListener {
            startActivity(Intent(this, chat_screen::class.java))
                        overridePendingTransition(0,0)
        }

        chat5.setOnClickListener {
            startActivity(Intent(this, chat_screen::class.java))
                        overridePendingTransition(0,0)
        }

        chat6.setOnClickListener {
            startActivity(Intent(this, chat_screen::class.java))
                        overridePendingTransition(0,0)
        }


        imgBottomCamera.setOnClickListener {
            startActivity(Intent(this, story_preview::class.java))
                        overridePendingTransition(0,0)
        }
    }
}
