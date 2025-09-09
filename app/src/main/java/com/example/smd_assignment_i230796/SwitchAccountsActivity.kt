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
        val btnLoginSwitch: Button = findViewById(R.id.btnLoginSwitch)
        val signupPrompt: TextView = findViewById(R.id.signupPromptSwitch)



        val fullText = "Don’t have an account? Sign up."
        val spannable = SpannableString(fullText)

        val startIndex = fullText.indexOf("Sign up")
        val endIndex = startIndex + "Sign up".length

        spannable.setSpan(
            UnderlineSpan(),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@SwitchAccountsActivity, SignUpActivity::class.java)
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
        btnLoginSwitch.setOnClickListener {

            val username = tvUsername.text.toString()

            val intent = Intent(this, LogInActivity::class.java)
            intent.putExtra("USERNAME-RECIEVED", username) // send username
            startActivity(intent)
        }
    }
}
