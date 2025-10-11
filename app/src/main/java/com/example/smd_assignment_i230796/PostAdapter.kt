package com.example.smd_assignment_i230796

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smd_assignment_i230796.databinding.ItemPostBinding

class PostAdapter(private val posts: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val context = holder.itemView.context

        // ---------- BASIC INFO ----------
        holder.binding.tvUsername.text = post.username
        holder.binding.tvLocation.text = post.location
        holder.binding.igpostCaption.text = post.caption
        holder.binding.tvLikedByName.text = post.likedByName ?: ""
        holder.binding.tvLikeCount.text = "${post.likeCount} others"
        holder.binding.ivPostProfile.setImageResource(post.profileResId)



        if (post.likedByProfileResId != 0) {
            holder.binding.ivLikedByProfile.setImageResource(post.likedByProfileResId)
        }

        //----------IMAGE HANDLING----------
        if (!post.imageUris.isNullOrEmpty() && post.imageResIds.isNullOrEmpty()) {
            if (post.imageUris.size == 1) {
                holder.binding.ivMainPostImage.visibility = View.VISIBLE
                holder.binding.viewPagerPost.visibility = View.GONE
                holder.binding.postIndicator.visibility = View.GONE
                holder.binding.ivMainPostImage.setImageURI(post.imageUris.first())
            } else {
                holder.binding.ivMainPostImage.visibility = View.GONE
                holder.binding.viewPagerPost.visibility = View.VISIBLE
                holder.binding.postIndicator.visibility = View.VISIBLE

                val adapter = PostImagePagerAdapter(
                    post.imageUris,
                    emptyList(),
                    context
                )
                holder.binding.viewPagerPost.adapter = adapter
                holder.binding.postIndicator.setViewPager(holder.binding.viewPagerPost)
            }
        } else if (!post.imageResIds.isNullOrEmpty()) {
            if (post.imageResIds.size == 1) {
                holder.binding.ivMainPostImage.visibility = View.VISIBLE
                holder.binding.viewPagerPost.visibility = View.GONE
                holder.binding.postIndicator.visibility = View.GONE
                holder.binding.ivMainPostImage.setImageResource(post.imageResIds.first())
            } else {
                holder.binding.ivMainPostImage.visibility = View.GONE
                holder.binding.viewPagerPost.visibility = View.VISIBLE
                holder.binding.postIndicator.visibility = View.VISIBLE

                val adapter = PostImagePagerAdapter(
                    emptyList(),
                    post.imageResIds,
                    context
                )
                holder.binding.viewPagerPost.adapter = adapter
                holder.binding.postIndicator.setViewPager(holder.binding.viewPagerPost)
            }
        } else {
            holder.binding.ivMainPostImage.visibility = View.GONE
            holder.binding.viewPagerPost.visibility = View.GONE
            holder.binding.postIndicator.visibility = View.GONE
        }

        //----------MAIN LIKE HANDLING IN THE BINDER----------
        holder.binding.ivLike.setImageResource(
            if (post.isLiked) R.drawable.ic_heart_red else R.drawable.like
        )
        holder.binding.ivLike.setOnClickListener {
            toggleLike(post, holder)
        }

        //----------STARTING LIKED SECTION VISIBILITY----------
        if (post.likeCount <=1) {

            holder.binding.layoutLikedBySection.visibility = View.GONE

        } else {
            holder.binding.layoutLikedBySection.visibility = View.VISIBLE
        }

        //----------COMMENTS HANDLING ENTIRE----------
        val commentsSection = holder.binding.commentsSection
        val commentCountText = holder.binding.tvCommentCount
        val inputBar = holder.binding.commentInputBar
        val etComment = holder.binding.etAddComment
        val btnPost = holder.binding.btnPostComment
        val btnCancel = holder.binding.btnCancelComment

        commentCountText.text = "${post.comments.size} comments"
        commentsSection.removeAllViews()
        val inflater = LayoutInflater.from(context)

        if (post.showComments) {
            for (comment in post.comments) {
                val commentView =
                    inflater.inflate(R.layout.item_comment, commentsSection, false)
                commentView.findViewById<ImageView>(R.id.ivCommentProfile)
                    .setImageResource(comment.profileImageResId)
                commentView.findViewById<TextView>(R.id.tvCommentUsername).text = comment.username
                commentView.findViewById<TextView>(R.id.tvCommentText).text = comment.text
                commentsSection.addView(commentView)
            }
            commentsSection.visibility = View.VISIBLE
            inputBar.visibility = View.VISIBLE
        } else {
            commentsSection.visibility = View.GONE
            inputBar.visibility = View.GONE
        }

        holder.binding.ivComment.setOnClickListener {
            post.showComments = !post.showComments
            notifyItemChanged(position)
        }

        commentCountText.setOnClickListener {
            inputBar.visibility =
                if (inputBar.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        btnPost.setOnClickListener {
            val commentText = etComment.text.toString().trim()
            if (commentText.isNotEmpty()) {
                val newComment = comment(
                    username = "you",
                    text = commentText,
                    profileImageResId = R.drawable.profile
                )
                post.comments.add(newComment)
                etComment.text.clear()
                inputBar.visibility = View.GONE
                post.showComments = true
                notifyItemChanged(position)
            }
        }

        btnCancel.setOnClickListener {
            etComment.text.clear()
            inputBar.visibility = View.GONE
        }
    }

    //----------LIKE BTN TOGGLING ----------
    private fun toggleLike(post: Post, holder: PostViewHolder) {
        post.isLiked = !post.isLiked
        post.likeCount += if (post.isLiked) 1 else -1
        if (post.likeCount < 0) post.likeCount = 0

        holder.binding.ivLike.setImageResource(
            if (post.isLiked) R.drawable.ic_heart_red else R.drawable.like
        )
        holder.binding.tvLikeCount.text = "${post.likeCount} others"

        //Visibility change it later once get back after gym
        if (post.likeCount <= 1) {
            holder.binding.layoutLikedBySection.visibility = View.GONE
        } else {
            holder.binding.layoutLikedBySection.visibility = View.VISIBLE
        }
    }

}
