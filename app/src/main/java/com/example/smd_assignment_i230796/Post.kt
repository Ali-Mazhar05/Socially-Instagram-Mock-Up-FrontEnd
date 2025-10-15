package com.example.smd_assignment_i230796

import android.net.Uri
import com.google.firebase.database.Exclude

data class Post(
    var postId: String? = null,
    var userId: String? = null,
    var username: String? = null,
    var location: String? = null,
    var caption: String? = null,
    var imageBase64List: List<String>? = null,
    var profileImageUrl: String? = null, // Base64 string for profile pic
    var likedByProfileResId: Int? = null,
    var likedByName: String? = null,
    var likeCount: Int? = null,
    var isVerified: Boolean = false,
    var paginationIconResId: Int? = null,
    var isLiked: Boolean = false,
    var comments: MutableList<comment>? = null,

    var likedBy: Map<String, Boolean>? = null,
    // Only used locally — not stored in Firebase
    @Exclude
    var imageUris: List<Uri>? = null
)
