package com.example.smd_assignment_i230796

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class post_picture_screen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_picture_screen)

        val cancelBtn: TextView = findViewById(R.id.cancel_btn)
        val nextBtn: TextView = findViewById(R.id.next_btn)
        val centerTitle: LinearLayout = findViewById(R.id.center_title)


        val selectedImage: ImageView = findViewById(R.id.selected_image)

        // GridLayout
        val photoGrid: GridLayout = findViewById(R.id.photo_grid)

        cancelBtn.setOnClickListener {
            finish()
                        overridePendingTransition(0,0)
        }

        nextBtn.setOnClickListener {
            finish()
                        overridePendingTransition(0,0)
        }

        centerTitle.setOnClickListener {
            Toast.makeText(this, "Recents dropdown clicked!", Toast.LENGTH_SHORT).show()
        }

        // Loop through all ImageViews inside GridLayout
        for (i in 0 until photoGrid.childCount) {
            val child = photoGrid.getChildAt(i)
            if (child is ImageView) {
                child.setOnClickListener {
                    // Update the large preview image with the clicked one
                    selectedImage.setImageDrawable(child.drawable)
                }
            }
        }
    }
}
