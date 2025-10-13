package com.example.smd_assignment_i230796

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class AddChoiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_choice)

        val btnAddStory = findViewById<Button>(R.id.btnAddStory)
        val btnAddPost = findViewById<Button>(R.id.btnAddPost)

        btnAddStory.setOnClickListener {
            val intent = Intent(this, add_to_story::class.java)
            startActivity(intent)
            finish()
        }

        btnAddPost.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra("addType", "post"))
            finish()
        }
    }
}
