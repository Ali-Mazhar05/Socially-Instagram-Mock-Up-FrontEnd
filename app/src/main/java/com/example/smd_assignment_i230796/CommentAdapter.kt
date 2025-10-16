package com.example.smd_assignment_i230796

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smd_assignment_i230796.databinding.ItemCommentBinding
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64

class CommentAdapter(private val comments: List<comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun getItemCount() = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val c = comments[position]

        val b64 = c.profileImageBase64
        if (!b64.isNullOrBlank()) {
            try {
                // Remove possible data URI prefix like "data:image/png;base64,...."
                val cleaned = if (b64.contains(",")) b64.substringAfter(",") else b64

                // Use the Android Base64 explicitly to avoid import conflicts
                val bytes = android.util.Base64.decode(cleaned, android.util.Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                if (bitmap != null) {
                    holder.binding.ivCommentProfile.setImageBitmap(bitmap)
                } else {
                    holder.binding.ivCommentProfile.setImageResource(R.drawable.profile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                holder.binding.ivCommentProfile.setImageResource(R.drawable.profile)
            }
        } else {
            holder.binding.ivCommentProfile.setImageResource(R.drawable.profile)
        }

        holder.binding.tvCommentUsername.text = c.username ?: ""
        holder.binding.tvCommentText.text = c.text ?: ""
    }

}
