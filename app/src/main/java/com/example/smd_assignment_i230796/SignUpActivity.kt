package com.example.smd_assignment_i230796

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signupactivity)

        val username = findViewById<EditText>(R.id.etUsername)
        val firstName = findViewById<EditText>(R.id.etFirstName)
        val lastName = findViewById<EditText>(R.id.etLastName)
        val dob = findViewById<EditText>(R.id.etDob)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnCreate = findViewById<Button>(R.id.btnCreateAccount)

        btnCreate.setOnClickListener {
            val u = username.text.toString()
            val f = firstName.text.toString()
            val l = lastName.text.toString()
            val d = dob.text.toString()
            val e = email.text.toString()
            val p = password.text.toString()


            Toast.makeText(this, "Saved: $u, $f $l, $d, $e", Toast.LENGTH_LONG).show()


            val intent=Intent(this, LogInActivity::class.java)
            intent.putExtra("USERNAME-RECIEVED", u)
            startActivity(intent)
            finish()
        }

        val backArrow= findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            finish()
        }

    }
}
