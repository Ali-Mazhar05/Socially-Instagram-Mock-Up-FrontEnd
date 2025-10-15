package com.example.smd_assignment_i230796

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.smd_assignment_i230796.R

class ProfilePostAdapter(
    private val postImages: List<String>
) : RecyclerView.Adapter<ProfilePostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProfilePost: ImageView = itemView.findViewById(R.id.imageProfilePost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val imageData = postImages[position]

        // ✅ Decode Base64 safely
        val bitmap = decodeBase64Image(imageData)

        if (bitmap != null) {
            holder.imageProfilePost.setImageBitmap(bitmap)
            holder.imageProfilePost.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            holder.imageProfilePost.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            // ✅ Show a placeholder when decoding fails
            holder.imageProfilePost.setImageResource(R.drawable.main_post_img)
            holder.imageProfilePost.setBackgroundColor(android.graphics.Color.LTGRAY)
        }
    }

    override fun getItemCount() = postImages.size

    // --- Helper function ---
    private fun decodeBase64Image(base64Str: String?): Bitmap? {
        if (base64Str.isNullOrBlank()) return null

        return try {
            // Remove unwanted characters
            val cleanBase64 = base64Str
                .replace("data:image/png;base64,", "")
                .replace("data:image/jpeg;base64,", "")
                .replace("\\s".toRegex(), "")

            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            if (bitmap == null || bitmap.width == 0 || bitmap.height == 0) null else bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
