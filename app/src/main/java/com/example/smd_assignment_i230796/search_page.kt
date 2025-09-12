package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class search_page : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_page)

        // 🔍 Search Bar
        val etSearch = findViewById<EditText>(R.id.et_search)

        // 🏠 Bottom navigation items
        val ivHome = findViewById<ImageView>(R.id.iv_nav_home)
        val ivSearch = findViewById<ImageView>(R.id.iv_nav_search)
        val ivAdd = findViewById<ImageView>(R.id.iv_nav_add)
        val ivHeart = findViewById<ImageView>(R.id.iv_nav_heart)
        val ivProfile = findViewById<ImageView>(R.id.iv_your_profile)

        // Example: handle clicks
        ivHome.setOnClickListener {
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, main_feed::class.java))
        }

        ivSearch.setOnClickListener {
            Toast.makeText(this, "Already on Search", Toast.LENGTH_SHORT).show()
        }

        ivAdd.setOnClickListener {
            Toast.makeText(this, "Add Post clicked", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, AddPostActivity::class.java))
        }

        ivHeart.setOnClickListener {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, NotificationsActivity::class.java))
        }

        ivProfile.setOnClickListener {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, ProfileActivity::class.java))
        }

        // 🔎 Handle typing in search
        etSearch.setOnEditorActionListener { v, _, _ ->
            val query = v.text.toString().trim()
            if (query.isNotEmpty()) {
                Toast.makeText(this, "Searching: $query", Toast.LENGTH_SHORT).show()
                // TODO: trigger your search logic
            }
            true
        }
    }
}
