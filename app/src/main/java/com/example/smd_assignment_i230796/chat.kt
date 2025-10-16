import com.example.smd_assignment_i230796.ChatMessage

data class Chat(
    val chatId: String = "",
    val userId1: String = "",
    val userId2: String = "",
    val lastMessage: String = "",
    val messages: Map<String, ChatMessage>? = null
)
