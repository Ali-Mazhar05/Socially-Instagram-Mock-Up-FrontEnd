package com.example.smd_assignment_i230796

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class you_notif : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.you_notif)

        underlineUsernames(
            findViewById(R.id.text_karenne),
            listOf("karenne")
        )

        underlineUsernames(
            findViewById(R.id.text_kiero),
            listOf("kiero_d", "zackjohn", "26 others")
        )

        underlineUsernames(
            findViewById(R.id.text_craig),
            listOf("craig_love")
        )

        underlineUsernames(
            findViewById(R.id.text_martini),
            listOf("martini_rond")
        )
    }

    private fun underlineUsernames(textView: TextView, usernames: List<String>) {
        val originalText = textView.text.toString()
        val spannable = SpannableString(originalText)

        for (username in usernames) {
            val start = originalText.indexOf(username)
            if (start >= 0) {
                val end = start + username.length
                spannable.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        textView.text = spannable
    }
}
