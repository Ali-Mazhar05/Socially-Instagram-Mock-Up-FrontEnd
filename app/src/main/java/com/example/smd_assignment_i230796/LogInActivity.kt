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
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val forgotPassword: TextView = findViewById(R.id.forgotPassword)
        val signupPrompt: TextView = findViewById(R.id.signupPrompt)

        // ✅ Auto-fill username if passed from SignUpActivity
        val receivedUsername = intent.getStringExtra("USERNAME-RECIEVED")
        if (!receivedUsername.isNullOrEmpty()) {
            etUsername.setText(receivedUsername)
        }

        // --- Make only "Sign up" clickable and underlined ---
        val fullText = "Don’t have an account? Sign up."
        val spannable = SpannableString(fullText)

        val startIndex = fullText.indexOf("Sign up")
        val endIndex = startIndex + "Sign up".length

        val backArrow= findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            finish()
        }
        // Underline the "Sign up"
        spannable.setSpan(
            UnderlineSpan(),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Make "Sign up" clickable
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Navigate to SignUpActivity
                val intent = Intent(this@LogInActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
        spannable.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        signupPrompt.text = spannable
        signupPrompt.movementMethod = LinkMovementMethod.getInstance()

        // --- Login button click ---
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login successful for $username", Toast.LENGTH_SHORT).show()

                // Navigate to main_feed and close login
                val intent = Intent(this, main_feed::class.java)
                startActivity(intent)
                finish()
            }
        }


        // --- Forgot Password click ---
        forgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
            // TODO: Open ForgotPasswordActivity
        }
    }
}
