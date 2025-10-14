package com.example.smd_assignment_i230796

import Story

data class UserStory(
    var userId: String = "",
    var stories: MutableList<Story> = mutableListOf()
)
