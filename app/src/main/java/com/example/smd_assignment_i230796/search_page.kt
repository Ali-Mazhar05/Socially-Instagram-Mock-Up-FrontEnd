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

        val etSearch = findViewById<EditText>(R.id.et_search)

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

        etSearch.setOnClickListener {
            startActivity(Intent(this, search_tab_internshala::class.java))
                        overridePendingTransition(0,0)
            finish()
                        overridePendingTransition(0,0)
        }

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
