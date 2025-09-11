package com.example.smd_assignment_i230796

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class you_notif : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.you_notif)

        // Pick text by IDs
        val karenneText: TextView = findViewById(R.id.karenne_you_text)
        val kieroText: TextView = findViewById(R.id.kiero_d_zackjon_you_text)
        val craigText: TextView = findViewById(R.id.craig_love_you_text)
        val martiniText: TextView = findViewById(R.id.martini_rond_you_text)
        val missPotterText: TextView = findViewById(R.id.miss_potter_you_text)
        val humphreyText: TextView = findViewById(R.id.m_humphrey_you_text)
        val jammmieText: TextView = findViewById(R.id.jammmie_you_text)
        val joshuaText: TextView = findViewById(R.id.joshua_you_text)
        val parkerText: TextView = findViewById(R.id.parkerr_you_text)

        karenneText.text = boldFirstWordAnd26Others(karenneText.text.toString())
        kieroText.text = boldFirstWordAnd26Others(kieroText.text.toString())
        craigText.text = boldFirstWordAnd26Others(craigText.text.toString())
        martiniText.text = boldFirstWordAnd26Others(martiniText.text.toString())
        missPotterText.text = boldFirstWordAnd26Others(missPotterText.text.toString())
        humphreyText.text = boldFirstWordAnd26Others(humphreyText.text.toString())
        jammmieText.text = boldFirstWordAnd26Others(jammmieText.text.toString())
        joshuaText.text = boldFirstWordAnd26Others(joshuaText.text.toString())
        parkerText.text = boldFirstWordAnd26Others(parkerText.text.toString())
    }


    private fun boldFirstWordAnd26Others(text: String): SpannableString {
        val spannable = SpannableString(text)

        // Bold first word
        val firstSpaceIndex = text.indexOf(" ")
        if (firstSpaceIndex != -1) {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                firstSpaceIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Bold "26 others" if present
        val spanText = "26 others"
        var startIndex = text.indexOf(spanText)
        while (startIndex != -1) {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                startIndex + spanText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            startIndex = text.indexOf(spanText, startIndex + 1)
        }

        return spannable
    }
}
