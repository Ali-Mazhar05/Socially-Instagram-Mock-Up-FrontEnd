package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import de.hdodenhof.circleimageview.CircleImageView

class chat_screen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_screen)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnVideo = findViewById<ImageView>(R.id.btnVideo)
        val btnInfo = findViewById<ImageView>(R.id.btnInfo)
        val imgProfile = findViewById<CircleImageView>(R.id.imgProfile)
        val tvChatName = findViewById<TextView>(R.id.tvChatName)

        val messageScroll = findViewById<ScrollView>(R.id.messageScroll)
        val messageContainer = findViewById<RelativeLayout>(R.id.messageContainer)

        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnCamera = findViewById<ImageView>(R.id.btnCamera)
        val btnEmoji = findViewById<ImageView>(R.id.btnEmoji)
        val btnGallery = findViewById<ImageView>(R.id.btnGallery)
        val btnGifs = findViewById<ImageView>(R.id.btnGifs)

        btnVideo.setOnClickListener{
            startActivity(Intent(this, call_screen::class.java))
                        overridePendingTransition(0,0)
        }
        btnGallery.setOnClickListener {
            val intent=Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
                overridePendingTransition(0, 0)
        }
        }
        btnCamera.setOnClickListener {
            startActivity(Intent(this, post_picture_screen::class.java))
                        overridePendingTransition(0,0)
        }

        btnBack.setOnClickListener { finish()
            overridePendingTransition(0,0)}
        /*btnVideo.setOnClickListener { Toast.makeText(this, "Video Call Clicked", Toast.LENGTH_SHORT).show() }
        btnInfo.setOnClickListener { Toast.makeText(this, "Info Clicked", Toast.LENGTH_SHORT).show() }
        btnCamera.setOnClickListener { Toast.makeText(this, "Camera Clicked", Toast.LENGTH_SHORT).show() }*/


    }

   /* private fun addMessage(message: String, messageContainer: RelativeLayout, messageScroll: ScrollView) {
        val msgView = TextView(this).apply {
            text = message
            setBackgroundResource(R.drawable.hello_text)
            setTextColor(resources.getColor(android.R.color.white))
            textSize = 16f
            setPadding(24, 16, 24, 16)
        }

        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_END)
            topMargin = 12
        }

        msgView.layoutParams = params
        messageContainer.addView(msgView)

        messageScroll.post {
            messageScroll.fullScroll(View.FOCUS_DOWN)
        }
    }*/
}
