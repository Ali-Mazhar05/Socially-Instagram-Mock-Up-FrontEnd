data class Story(
    var timestamp: String = "",
    var imageBase64: String = "",
    var caption: String = "",
    var closeFriends: Boolean = false,
    var viewedBy: Map<String, Boolean>? = null,
    var isViewed: Boolean = false
)
