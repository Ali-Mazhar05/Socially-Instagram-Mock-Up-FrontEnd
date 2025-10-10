package com.example.smd_assignment_i230796

import android.net.Uri

data class Post(
    val username: String,
    val location: String,
    val caption: String,
    val imageUris: List<Uri>? = null,
    val imageResIds: List<Int>? = null,
    val profileResId: Int,
    val likedByProfileResId: Int,
    val likedByName: String ?=null,
    var likeCount: Int,
    val isVerified: Boolean,
    val paginationIconResId: Int?,
    var isLiked: Boolean,
    val comments: MutableList<comment> = mutableListOf(),
    var showComments: Boolean = false
)
