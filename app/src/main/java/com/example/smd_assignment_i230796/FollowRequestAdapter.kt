package com.example.smd_assignment_i230796

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class FollowRequestAdapter(
    private val context: Context,
    private val requests: MutableList<User>,
    private val onListEmpty: (() -> Unit)? = null
) : RecyclerView.Adapter<FollowRequestAdapter.FollowRequestViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    inner class FollowRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfile: CircleImageView = itemView.findViewById(R.id.ivProfileImage)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowRequestViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_follow_request, parent, false)
        return FollowRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowRequestViewHolder, position: Int) {
        val user = requests[position]
        holder.tvUsername.text = user.username ?: "Unknown"

        // decode base64 image if exists
        val imgBase64 = user.profileImage
        if (!imgBase64.isNullOrEmpty()) {
            try {
                val decoded = Base64.decode(imgBase64, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                holder.ivProfile.setImageBitmap(bmp)
            } catch (e: Exception) {
                holder.ivProfile.setImageResource(R.drawable.profile)
            }
        } else {
            holder.ivProfile.setImageResource(R.drawable.profile)
        }

        // Accept follow request
        holder.btnAccept.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION && currentUserId != null && user.uid != null) {
                FollowManager.acceptFollowRequest(user.uid!!, currentUserId) { success ->
                    if (success) {
                        Toast.makeText(context, "Accepted ${user.username}", Toast.LENGTH_SHORT).show()
                        requests.removeAt(pos)
                        notifyItemRemoved(pos)
                        if (requests.isEmpty()) onListEmpty?.invoke()
                    } else {
                        Toast.makeText(context, "Failed to accept request", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Reject follow request
        holder.btnReject.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION && currentUserId != null && user.uid != null) {
                FollowManager.rejectFollowRequest(user.uid!!, currentUserId) { success ->
                    if (success) {
                        Toast.makeText(context, "Rejected ${user.username}", Toast.LENGTH_SHORT).show()
                        requests.removeAt(pos)
                        notifyItemRemoved(pos)
                        if (requests.isEmpty()) onListEmpty?.invoke()
                    } else {
                        Toast.makeText(context, "Failed to reject request", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = requests.size
}
