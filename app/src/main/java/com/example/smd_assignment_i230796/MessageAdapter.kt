package com.example.smd_assignment_i230796

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MessageAdapter(
    private val messageList: MutableList<ChatMessage>,
    private val currentUserId: String,
    private val otherUserProfileBase64: String?, // New: pass other user profile
    private val onMessageLongClick: (ChatMessage) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_SENT = 1
    private val ITEM_RECEIVED = 2

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].senderId == currentUserId) ITEM_SENT else ITEM_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sent, parent, false)
            SentViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
            ReceivedViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is SentViewHolder) holder.bind(message)
        else if (holder is ReceivedViewHolder) holder.bind(message)

        holder.itemView.setOnLongClickListener {
            onMessageLongClick(message)
            true
        }
    }

    override fun getItemCount(): Int = messageList.size

    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val imgMessage: ImageView = itemView.findViewById(R.id.imgMessage)
        fun bind(message: ChatMessage) {
            bindCommon(message, tvMessage, imgMessage)
        }
    }

    inner class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val imgMessage: ImageView = itemView.findViewById(R.id.imgMessage)


        fun bind(message: ChatMessage) {
            bindCommon(message, tvMessage, imgMessage)

            // Set other user's profile image

        }
    }

    private fun bindCommon(message: ChatMessage, tvMessage: TextView, imgMessage: ImageView) {
        if (!message.imageUrl.isNullOrEmpty()) {
            try {
                val bytes = Base64.decode(message.imageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imgMessage.visibility = View.VISIBLE
                tvMessage.visibility = View.GONE
                imgMessage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Glide.with(imgMessage.context).load(message.imageUrl).into(imgMessage)
            }
        } else {
            imgMessage.visibility = View.GONE
            tvMessage.visibility = View.VISIBLE
            tvMessage.text = if (message.edited) "${message.text} (edited)" else message.text
        }
    }

    // Circular bitmap helper
    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = Math.min(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(output)
        val paint = android.graphics.Paint()
        paint.isAntiAlias = true
        val rect = android.graphics.Rect(0, 0, size, size)
        val rectF = android.graphics.RectF(rect)
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawOval(rectF, paint)
        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        val left = (bitmap.width - size) / 2
        val top = (bitmap.height - size) / 2
        canvas.drawBitmap(bitmap, -left.toFloat(), -top.toFloat(), paint)
        return output
    }
}
