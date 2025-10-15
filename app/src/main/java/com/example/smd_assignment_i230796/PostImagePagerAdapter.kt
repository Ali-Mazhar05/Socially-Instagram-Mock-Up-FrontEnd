package com.example.smd_assignment_i230796

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smd_assignment_i230796.databinding.ItemPostImageBinding

class PostImagePagerAdapter(
    private val imageUris: List<Uri>? = null,
    private val imageBase64List: List<String>? = null,
    private val context: Context
) : RecyclerView.Adapter<PostImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemPostImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemPostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return when {
            !imageBase64List.isNullOrEmpty() -> imageBase64List.size
            !imageUris.isNullOrEmpty() -> imageUris.size
            else -> 0
        }
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        when {
            //CASE 1: Base64 encoded images (Firebase)
            !imageBase64List.isNullOrEmpty() -> {
                val base64Str = imageBase64List[position]
                val bytes = Base64.decode(base64Str, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.binding.ivPostImage.setImageBitmap(bitmap)
            }

            //CASE 2: Local image URIs
            !imageUris.isNullOrEmpty() -> {
                holder.binding.ivPostImage.setImageURI(imageUris[position])
            }

            // CASE 3: Fallback (shouldn't happen, but safe)
            else -> {
                holder.binding.ivPostImage.setImageResource(R.drawable.main_post_img)
            }
        }
    }
}
