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

class SwitchAccountsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.switch_accounts)

        val tvUsername: TextView = findViewById(R.id.tvUsername)
        val btnLoginSwitch: TextView = findViewById(R.id.btnLoginSwitch)
        val signupPrompt: TextView = findViewById(R.id.signupPromptSwitch)

        btnLoginSwitch.setOnClickListener {

            val username = tvUsername.text.toString()

            val intent = Intent(this, LogInActivity::class.java)
            intent.putExtra("USERNAME-RECIEVED", username)
            startActivity(intent)
            overridePendingTransition(0,0)
        }

        signupPrompt.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java) )
            overridePendingTransition(0,0)
            finish()
        }
    }
}
