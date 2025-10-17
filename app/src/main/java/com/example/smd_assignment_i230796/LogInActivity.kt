package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginactivity)

        auth = FirebaseAuth.getInstance()

        // UI references
        val etEmail: EditText = findViewById(R.id.etUsername) // Your layout uses etUsername ID
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnLogin: TextView = findViewById(R.id.btnLogin)
        val forgotPassword: TextView = findViewById(R.id.forgotPassword)
        val signupPrompt: TextView = findViewById(R.id.signupPrompt)
        val backArrow: ImageView = findViewById(R.id.backArrow)

        // Autofill if user came from signup
        val receivedEmail = intent.getStringExtra("USERNAME-RECIEVED")
        if (!receivedEmail.isNullOrEmpty()) {
            etEmail.setText(receivedEmail)
        }

        // 🔹 LOGIN BUTTON HANDLER
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    MyFirebaseMessagingService.updateTokenAfterLogin()


                    // Go to main feed
                    startActivity(Intent(this, main_feed::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        // 🔹 BACK BUTTON HANDLER
        backArrow.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        // 🔹 FORGOT PASSWORD HANDLER
        forgotPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email to reset password", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // 🔹 SIGNUP PROMPT HANDLER
        signupPrompt.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }
}
