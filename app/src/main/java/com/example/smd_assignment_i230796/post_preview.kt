package com.example.smd_assignment_i230796

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.smd_assignment_i230796.databinding.PostPreviewBinding

class post_preview : AppCompatActivity() {

    private lateinit var binding: PostPreviewBinding
    private lateinit var imageUris: List<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PostPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get image URIs from intent
        val uris = intent.getStringArrayListExtra("imageUris") ?: arrayListOf()
        imageUris = uris.map { Uri.parse(it) }


        binding.viewPagerPreview.adapter = PostImagePagerAdapter(
            imageUris,
             emptyList(),
             this
        )

        //buttons
        binding.btnPost.setOnClickListener {
            val caption = binding.etCaption.text.toString()
            val resultIntent = intent.apply {
                putStringArrayListExtra("imageUris", ArrayList(imageUris.map { it.toString() }))
                putExtra("caption", caption)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
