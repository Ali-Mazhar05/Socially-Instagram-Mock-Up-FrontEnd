package com.example.smd_assignment_i230796

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class dm_feed : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dm_feed)

        // Top buttons
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnAdd = findViewById<ImageView>(R.id.btnAdd)

        // Chats
        val chat1 = findViewById<LinearLayout>(R.id.chat1)
        val chat2 = findViewById<LinearLayout>(R.id.chat2)
        val chat3 = findViewById<LinearLayout>(R.id.chat3)
        val chat4 = findViewById<LinearLayout>(R.id.chat4)
        val chat5 = findViewById<LinearLayout>(R.id.chat5)
        val chat6 = findViewById<LinearLayout>(R.id.chat6)


        // Bottom camera
        val imgBottomCamera = findViewById<ImageView>(R.id.imgBottomCamera)

        // Event Listeners
        btnBack.setOnClickListener {
            Toast.makeText(this, "Back pressed", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnAdd.setOnClickListener {
            Toast.makeText(this, "Add new chat", Toast.LENGTH_SHORT).show()
        }

        chat1.setOnClickListener {
            Toast.makeText(this, "Opening chat with joshua_j", Toast.LENGTH_SHORT).show()
        }

        chat2.setOnClickListener {
            Toast.makeText(this, "Opening chat with karenne", Toast.LENGTH_SHORT).show()
        }

        imgBottomCamera.setOnClickListener {
            Toast.makeText(this, "Camera opened", Toast.LENGTH_SHORT).show()
        }
    }
}
