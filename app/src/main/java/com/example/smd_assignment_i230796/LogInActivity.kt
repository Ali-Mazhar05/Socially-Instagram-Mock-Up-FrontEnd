package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginactivity)

        val etUsername: EditText = findViewById(R.id.etUsername)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnLogin: TextView = findViewById(R.id.btnLogin)
        val forgotPassword: TextView = findViewById(R.id.forgotPassword)
        val signupPrompt: TextView = findViewById(R.id.signupPrompt)

        val receivedUsername = intent.getStringExtra("USERNAME-RECIEVED")
        if (!receivedUsername.isNullOrEmpty()) {
            etUsername.setText(receivedUsername)
        }


        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login successful for $username", Toast.LENGTH_SHORT).show()



                val intent = Intent(this, main_feed::class.java)
                startActivity(intent)
                overridePendingTransition(0,0)
                finish()
            }
        }

        findViewById<ImageView>(R.id.backArrow).setOnClickListener { finish()
            overridePendingTransition(0,0)}


        forgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        }

        signupPrompt.setOnClickListener {
          startActivity(Intent(this, SignUpActivity::class.java))
            overridePendingTransition(0,0)
            finish()
        }
    }
}
