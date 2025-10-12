package com.example.smd_assignment_i230796

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(
    private val messages: MutableList<ChatMessage>,
    private val currentUserId: String,
    private val onEdit: (ChatMessage) -> Unit,
    private val onDelete: (ChatMessage) -> Unit
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val imgMessage: ImageView? = view.findViewById(R.id.imgMessage)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = if (viewType == 1)
            R.layout.item_message_sent
        else
            R.layout.item_message_received

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val msg = messages[position]

        // Reset visibility to avoid layout recycling issues
        holder.tvMessage.visibility = View.GONE
        holder.imgMessage?.visibility = View.GONE

        if (!msg.imageUrl.isNullOrEmpty()) {
            // It's an image message
            val bytes = Base64.decode(msg.imageUrl, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            holder.imgMessage?.setImageBitmap(bitmap)
            holder.imgMessage?.visibility = View.VISIBLE
        } else if (msg.text.isNotEmpty()) {
            // It's a text message
            holder.tvMessage.text = if (msg.edited) "${msg.text} (edited)" else msg.text
            holder.tvMessage.visibility = View.VISIBLE
        }

        // Allow edit/delete only for sender within 5 minutes
        if (msg.senderId == currentUserId &&
            System.currentTimeMillis() - msg.timestamp < 5 * 60 * 1000
        ) {
            holder.itemView.setOnLongClickListener {
                val popup = PopupMenu(holder.itemView.context, holder.itemView)
                popup.menu.add("Edit")
                popup.menu.add("Delete")
                popup.setOnMenuItemClickListener {
                    when (it.title) {
                        "Edit" -> onEdit(msg)
                        "Delete" -> onDelete(msg)
                    }
                    true
                }
                popup.show()
                true
            }
        } else {
            holder.itemView.setOnLongClickListener(null)
        }
    }


    override fun getItemCount() = messages.size

    fun setMessages(newList: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newList)
        notifyDataSetChanged()
    }
}
