package com.example.smd_assignment_i230796

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smd_assignment_i230796.databinding.ItemStoryBinding
class StoryAdapter(private val stories: List<Story>) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]

        with(holder.binding) {
            tvStoryName.text = story.name
            ivStoryProfile.setImageResource(story.imageRes)

            val ringRes = if (story.isViewed) R.drawable.dm_viewed_story else R.drawable.storyring
            ivStoryRing.setImageResource(ringRes)

            root.setOnClickListener {
                if (!story.isViewed) {
                    story.isViewed = true
                    notifyItemChanged(position)
                }

            }
        }
    }
}

