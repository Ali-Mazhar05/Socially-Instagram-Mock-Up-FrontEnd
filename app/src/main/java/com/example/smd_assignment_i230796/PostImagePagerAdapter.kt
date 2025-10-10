package com.example.smd_assignment_i230796

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smd_assignment_i230796.databinding.ItemPostImageBinding

class PostImagePagerAdapter(
    private val imageUris: List<Uri> = emptyList(),
    private val imageResIds: List<Int> = emptyList(),
    private val context: Context
) : RecyclerView.Adapter<PostImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemPostImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding =
            ItemPostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int = imageUris.size + imageResIds.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        when {
            position < imageUris.size ->
                holder.binding.ivPostImage.setImageURI(imageUris[position])
            else ->
                holder.binding.ivPostImage.setImageResource(imageResIds[position - imageUris.size])
        }
    }
}
