package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class story_preview: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story_preview)

        findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.closeBtn)
            .setOnClickListener {
                finish()
                overridePendingTransition(0,0)
            }

    }



}