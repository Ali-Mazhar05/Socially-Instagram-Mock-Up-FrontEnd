package com.example.smd_assignment_i230796

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.smd_assignment_i230796.databinding.ItemPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PostAdapter(
    private val context: android.content.Context,
    private val posts: MutableList<Post>,
    private val currentUsername: String,
    private val currentUserProfileBase64: String?
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        // ---------- FETCH CURRENT USER INFO ----------
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUsername = currentUser?.displayName ?: "You"
        val currentUserId = currentUser?.uid ?: ""
        var currentUserProfileBase64 = ""

        // Fetch user's profile image (if stored in Firebase)
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId)
        userRef.child("profileImage").get().addOnSuccessListener { snapshot ->
            currentUserProfileBase64 = snapshot.getValue(String::class.java) ?: ""
        }
        userRef.child("username").get().addOnSuccessListener {  snapshot ->
            currentUsername =snapshot.getValue(String::class.java) ?: " "
        }

        // ---------- BASIC INFO ----------
        holder.binding.tvUsername.text = post.username ?: ""
        holder.binding.tvLocation.text = post.location ?: ""
        holder.binding.igpostCaption.text = post.caption ?: ""
        holder.binding.tvLikedByName.text = post.likedByName ?: ""
        holder.binding.tvLikeCount.text = "${post.likeCount ?: 0} others"

        // ---------- PROFILE IMAGE ----------
        if (!post.profileImageUrl.isNullOrEmpty()) {
            try {
                val bytes = Base64.decode(post.profileImageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.binding.ivPostProfile.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.binding.ivPostProfile.setImageResource(R.drawable.profile)
            }
        } else {
            holder.binding.ivPostProfile.setImageResource(R.drawable.profile)
        }

        // ---------- POST IMAGES ----------
        when {
            !post.imageBase64List.isNullOrEmpty() -> {
                if (post.imageBase64List!!.size == 1) {
                    holder.binding.ivMainPostImage.visibility = View.VISIBLE
                    holder.binding.viewPagerPost.visibility = View.GONE
                    holder.binding.postIndicator.visibility = View.GONE

                    val bytes = Base64.decode(post.imageBase64List!!.first(), Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    holder.binding.ivMainPostImage.setImageBitmap(bitmap)
                } else {
                    holder.binding.ivMainPostImage.visibility = View.GONE
                    holder.binding.viewPagerPost.visibility = View.VISIBLE
                    holder.binding.postIndicator.visibility = View.VISIBLE

                    val adapter = PostImagePagerAdapter(
                        imageUris = null,
                        imageBase64List = post.imageBase64List,
                        context = context
                    )
                    holder.binding.viewPagerPost.adapter = adapter
                    holder.binding.postIndicator.setViewPager(holder.binding.viewPagerPost)
                }
            }

            !post.imageUris.isNullOrEmpty() -> {
                if (post.imageUris!!.size == 1) {
                    holder.binding.ivMainPostImage.visibility = View.VISIBLE
                    holder.binding.viewPagerPost.visibility = View.GONE
                    holder.binding.postIndicator.visibility = View.GONE
                    holder.binding.ivMainPostImage.setImageURI(post.imageUris!!.first())
                } else {
                    holder.binding.ivMainPostImage.visibility = View.GONE
                    holder.binding.viewPagerPost.visibility = View.VISIBLE
                    holder.binding.postIndicator.visibility = View.VISIBLE

                    val adapter = PostImagePagerAdapter(
                        imageUris = post.imageUris,
                        imageBase64List = null,
                        context = context
                    )
                    holder.binding.viewPagerPost.adapter = adapter
                    holder.binding.postIndicator.setViewPager(holder.binding.viewPagerPost)
                }
            }

            else -> {
                holder.binding.ivMainPostImage.visibility = View.GONE
                holder.binding.viewPagerPost.visibility = View.GONE
                holder.binding.postIndicator.visibility = View.GONE
            }
        }

        // ---------- LIKE HANDLING ----------
        holder.binding.ivLike.setImageResource(
            if (post.isLiked) R.drawable.ic_heart_red else R.drawable.like
        )

        holder.binding.ivLike.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return@setOnClickListener
            val currentUserId = currentUser.uid
            val postRef = FirebaseDatabase.getInstance().getReference("Posts").child(post.postId ?: return@setOnClickListener)

            val newLiked = !post.isLiked
            post.isLiked = newLiked

            // Ensure likedBy map exists
            val likedByMap = post.likedBy?.toMutableMap() ?: mutableMapOf()


            //remove this logic when no demos and using real
            //use likedByMap.size
            if (newLiked) {
                //Add current user to likedBy
                likedByMap[currentUserId] = true
                post.likeCount = post.likeCount?.plus(1)
            } else {
                //Remove current user from likedBy
                likedByMap.remove(currentUserId)
                post.likeCount = post.likeCount?.plus(-1)
            }

            //logic real to update by size but rn we js increment and decrement
            //post.likeCount = likedByMap.size

            post.likedBy = likedByMap

            //Update UI immediately
            holder.binding.ivLike.setImageResource(
                if (newLiked) R.drawable.ic_heart_red else R.drawable.like
            )
            holder.binding.tvLikeCount.text = "${post.likeCount ?: 0} others"
            holder.binding.layoutLikedBySection.visibility =
                if ((post.likeCount ?: 0) <= 1) View.GONE else View.VISIBLE

            //Push updates to Firebase atomically
            val updates = mapOf(
                "likeCount" to post.likeCount,
                "likedBy" to likedByMap
            )
            postRef.updateChildren(updates)
        }

        holder.binding.layoutLikedBySection.visibility =
            if ((post.likeCount ?: 0) <= 1) View.GONE else View.VISIBLE

        // ---------- COMMENTS SECTION ----------
        val commentsSection = holder.binding.commentsSection
        val commentCountText = holder.binding.tvCommentCount
        val inputBar = holder.binding.commentInputBar
        val etComment = holder.binding.etAddComment
        val btnPost = holder.binding.btnPostComment
        val btnCancel = holder.binding.btnCancelComment

        val comments = post.comments ?: mutableListOf()
        commentCountText.text = "${comments.size} comments"
        commentsSection.removeAllViews()

        val inflater = LayoutInflater.from(context)

        // Inflate existing comments
        if (comments.isNotEmpty()) {
            for (c in comments) {
                val commentView = inflater.inflate(R.layout.item_comment, commentsSection, false)
                val ivProfile = commentView.findViewById<ImageView>(R.id.ivCommentProfile)
                val tvUsername = commentView.findViewById<TextView>(R.id.tvCommentUsername)
                val tvText = commentView.findViewById<TextView>(R.id.tvCommentText)

                tvUsername.text = c.username ?: ""
                tvText.text = c.text ?: ""

                if (!c.profileImageBase64.isNullOrEmpty()) {
                    try {
                        val bytes = android.util.Base64.decode(
                            c.profileImageBase64,
                            android.util.Base64.DEFAULT
                        )
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        ivProfile.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        ivProfile.setImageResource(R.drawable.profile)
                    }
                } else {
                    ivProfile.setImageResource(R.drawable.profile)
                }

                commentsSection.addView(commentView)
            }
        }

        // ---------- COMMENT VISIBILITY ----------
        commentsSection.visibility = View.GONE
        inputBar.visibility = View.GONE

        holder.binding.ivComment.setOnClickListener {
            val isVisible = commentsSection.visibility == View.VISIBLE
            commentsSection.visibility = if (isVisible) View.GONE else View.VISIBLE
            inputBar.visibility = if (isVisible) View.GONE else View.VISIBLE
        }

        commentCountText.visibility = View.VISIBLE
        commentCountText.setOnClickListener {
            inputBar.visibility =
                if (inputBar.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // ---------- ADD NEW COMMENT ----------

        btnPost.setOnClickListener {


            val commentText = etComment.text.toString().trim()
            if (commentText.isNotEmpty()) {
                val newComment = comment(
                    profileImageBase64 = currentUserProfileBase64,
                    username = currentUsername,
                    text = commentText
                )

                post.comments?.add(newComment) ?: run {
                    post.comments = mutableListOf(newComment)
                }

                inputBar.visibility = View.GONE

                val postRef = FirebaseDatabase.getInstance()
                    .getReference("Posts")
                    .child(post.postId ?: return@setOnClickListener)

                postRef.child("comments").setValue(post.comments)
                    .addOnSuccessListener {
                        etComment.text.clear()
                        inputBar.visibility = View.GONE
                        notifyItemChanged(position)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Failed to add comment: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

        btnCancel.setOnClickListener {
            etComment.text.clear()
            inputBar.visibility = View.GONE
        }
    }

    // ---------- LIKE TOGGLE ----------
    private fun toggleLike(post: Post, holder: PostViewHolder) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userId = currentUser.uid

        val postRef = FirebaseDatabase.getInstance()
            .getReference("Posts")
            .child(post.postId!!)

        // If post already has likedBy map
        val likedBy = post.likedBy?.toMutableMap() ?: mutableMapOf()

        val isCurrentlyLiked = likedBy.containsKey(userId)
        val newLiked = !isCurrentlyLiked

        // Toggle like
        if (newLiked) {
            likedBy[userId] = true
            post.likeCount = (post.likeCount ?: 0) + 1
        } else {
            likedBy.remove(userId)
            post.likeCount = (post.likeCount ?: 0) - 1
            if (post.likeCount!! < 0) post.likeCount = 0
        }

        // Update UI immediately
        post.isLiked = newLiked
        holder.binding.ivLike.setImageResource(
            if (newLiked) R.drawable.ic_heart_red else R.drawable.like
        )
        holder.binding.tvLikeCount.text = "${post.likeCount} others"
        holder.binding.layoutLikedBySection.visibility =
            if ((post.likeCount ?: 0) <= 1) View.GONE else View.VISIBLE

        // Update Firebase atomically
        val updates = mapOf(
            "likeCount" to post.likeCount,
            "likedBy" to likedBy
        )

        postRef.updateChildren(updates)
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Failed to update like: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}
