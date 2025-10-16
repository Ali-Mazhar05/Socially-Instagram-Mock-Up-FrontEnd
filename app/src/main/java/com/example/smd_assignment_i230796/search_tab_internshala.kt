package com.example.smd_assignment_i230796

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class search_tab_internshala : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_tab_internshala)

        findViewById<TextView>(R.id.clear_Btn).setOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }
    }
}
