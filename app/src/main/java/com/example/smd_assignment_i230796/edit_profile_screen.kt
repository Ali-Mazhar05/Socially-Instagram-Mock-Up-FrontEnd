package com.example.smd_assignment_i230796

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class edit_profile_screen : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_screen)

        val cancelBtn = findViewById<TextView>(R.id.cancel_btn)
        val doneBtn = findViewById<TextView>(R.id.done_btn)
        val nameEt = findViewById<EditText>(R.id.name_edittext)
        val usernameEt = findViewById<EditText>(R.id.username_edittext)
        val websiteEt = findViewById<EditText>(R.id.website_edittext)
        val bioEt = findViewById<EditText>(R.id.bio_edittext)
        val emailEt = findViewById<EditText>(R.id.email_edittext)
        val phoneEt = findViewById<EditText>(R.id.phone_edittext)
        val genderEt = findViewById<EditText>(R.id.gender_edittext)

        cancelBtn.setOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }

        /*doneBtn.setOnClickListener { finish() }*/

        doneBtn.setOnClickListener {
            val userData = """
                Name: ${nameEt.text}
                Username: ${usernameEt.text}
                Website: ${websiteEt.text}
                Bio: ${bioEt.text}
                Email: ${emailEt.text}
                Phone: ${phoneEt.text}
                Gender: ${genderEt.text}
            """.trimIndent()

            Toast.makeText(this, userData, Toast.LENGTH_LONG).show()
            finish()
            overridePendingTransition(0,0)
        }
    }
}
