package com.example.smd_assignment_i230796.adapters

import android.graphics.*
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smd_assignment_i230796.R
import com.example.smd_assignment_i230796.ChatPreview
import java.text.SimpleDateFormat
import java.util.*

class ChatPreviewAdapter(
    private val chatList: List<ChatPreview>,
    private val onChatClick: (ChatPreview) -> Unit,
    private val onCameraClick: (ChatPreview) -> Unit
) : RecyclerView.Adapter<ChatPreviewAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val txtUsername: TextView = itemView.findViewById(R.id.txtUsername)
        val txtLastMessage: TextView = itemView.findViewById(R.id.txtLastMessage)
        val txtTimestamp: TextView = itemView.findViewById(R.id.txtTimestamp)
        val btnCamera: ImageButton = itemView.findViewById(R.id.btnCamera)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_preview, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]

        // ✅ Load profile image (Base64) or fallback
        holder.imgProfile.setImageBitmap(
            decodeBase64ToCircularBitmap(chat.profileImage) ?: run {
                BitmapFactory.decodeResource(holder.itemView.resources, R.drawable.jack_profile)
            }
        )

        // ✅ Set username safely
        holder.txtUsername.text = chat.username.ifBlank { "Unknown" }

        // ✅ Set last message and timestamp
        holder.txtLastMessage.text = chat.lastMessage.ifBlank { "No messages yet" }
        holder.txtTimestamp.text =
            if (chat.lastMessageTime > 0L) formatTime(chat.lastMessageTime) else ""

        // ✅ Click listeners
        holder.itemView.setOnClickListener { onChatClick(chat) }
        holder.btnCamera.setOnClickListener { onCameraClick(chat) }
    }

    override fun getItemCount(): Int = chatList.size

    private fun formatTime(ts: Long): String {
        return try {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(ts))
        } catch (e: Exception) {
            ""
        }
    }

    // Decode Base64 to circular bitmap
    private fun decodeBase64ToCircularBitmap(base64: String?): Bitmap? {
        if (base64.isNullOrBlank()) return null
        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            getCircularBitmap(bitmap)
        } catch (e: Exception) {
            null
        }
    }

    // Convert bitmap to circular
    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, size, size)
        val rectF = RectF(rect)

        // Draw circle
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawOval(rectF, paint)

        // Mask bitmap
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val left = (bitmap.width - size) / 2
        val top = (bitmap.height - size) / 2
        canvas.drawBitmap(bitmap, -left.toFloat(), -top.toFloat(), paint)
        return output
    }
}
