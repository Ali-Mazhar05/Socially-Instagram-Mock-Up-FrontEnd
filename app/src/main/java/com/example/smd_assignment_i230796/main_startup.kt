package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class main_startup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_startup)

        // Delay 5 seconds, then go to login
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SwitchAccountsActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)
    }
}
