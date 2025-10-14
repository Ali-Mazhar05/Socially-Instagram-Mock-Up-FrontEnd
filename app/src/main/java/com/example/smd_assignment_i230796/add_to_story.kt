package com.example.smd_assignment_i230796

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class add_to_story : AppCompatActivity() {

    private lateinit var storyImage: ImageView
    private lateinit var closeBtn: ImageView
    private lateinit var postBtn: ImageView
    private lateinit var yourStory: LinearLayout
    private lateinit var closeFriends: LinearLayout

    private lateinit var addPartnership: ImageView
    private lateinit var addText: ImageView
    private lateinit var addStickers: ImageView
    private lateinit var addMusic: ImageView
    private lateinit var addFilters: ImageView
    private lateinit var addOptions: ImageView

    private var imageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                Glide.with(this).load(uri).into(storyImage)
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_to_story)

        storyImage = findViewById(R.id.storyImage)
        closeBtn = findViewById(R.id.add_to_story_close)
        postBtn = findViewById(R.id.add_to_story_post)
        yourStory = findViewById(R.id.your_story)
        closeFriends = findViewById(R.id.close_friends)

        addPartnership = findViewById(R.id.add_to_story_partnership)
        addText = findViewById(R.id.add_to_story_text)
        addStickers = findViewById(R.id.add_to_story_stickers)
        addMusic = findViewById(R.id.add_to_story_music)
        addFilters = findViewById(R.id.add_to_story_filters)
        addOptions = findViewById(R.id.add_to_story_options)

        storyImage.setOnClickListener { pickImage.launch("image/*") }

        yourStory.setOnClickListener {
            if (imageUri == null) showError()
            else uploadStory(imageUri!!, false)
        }

        closeFriends.setOnClickListener {
            if (imageUri == null) showError()
            else uploadStory(imageUri!!, true)
        }

        postBtn.setOnClickListener {
            if (imageUri == null) showError()
            else uploadStory(imageUri!!, false)
        }

        closeBtn.setOnClickListener { finish() }

        // Temporary inactive buttons
        val inactiveButtons = listOf(addPartnership, addText, addStickers, addMusic, addFilters, addOptions)
        inactiveButtons.forEach {
            it.setOnClickListener {
                Toast.makeText(this, "Feature coming soon ✨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showError() {
        Toast.makeText(this, "Please select an image first!", Toast.LENGTH_SHORT).show()
    }

    private fun uploadStory(uri: Uri, closeFriends: Boolean) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val uid = firebaseUser?.uid

        if (uid == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap == null) {
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                return
            }

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos)
            val base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

            val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .format(Date())

            //Fetch user's profile image first (asynchronous)
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)
            userRef.child("profileImage").get().addOnSuccessListener { snapshot ->
                val profileImageBase64 = snapshot.getValue(String::class.java) ?: ""

                //Only proceed once profile image is loaded
                val storyData = mapOf(
                    "caption" to "No caption",
                    "closeFriends" to closeFriends,
                    "imageBase64" to base64Image,
                    "timestamp" to timestamp,
                    "isViewed" to false,
                    "viewedBy" to mapOf<String, Boolean>(),
                    "profileImage" to profileImageBase64
                )

                val userStoriesRef = FirebaseDatabase.getInstance().getReference("Stories").child(uid)
                val newKey = userStoriesRef.push().key ?: return@addOnSuccessListener

                userStoriesRef.child(newKey).updateChildren(storyData)
                    .addOnSuccessListener {
                        val intent = Intent("com.example.smd_assignment_i230796.STORY_UPDATED").apply {
                            putExtra("uid", uid)
                            putExtra("closeFriends", closeFriends)
                            putExtra("imageBase64", base64Image)
                            putExtra("timestamp", timestamp)
                            putExtra("profileImage", profileImageBase64)
                        }
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

                        val typeText = if (closeFriends) "Close Friends" else "Public"
                        Toast.makeText(this, "✅ $typeText Story uploaded!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "❌ Upload failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(this, "⚠️ Failed to load profile image", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "❌ Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
