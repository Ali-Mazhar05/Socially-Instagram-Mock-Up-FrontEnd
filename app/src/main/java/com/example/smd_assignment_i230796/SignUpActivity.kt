package com.example.smd_assignment_i230796

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var imageUri: Uri? = null
    private var encodedImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signupactivity)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val username = findViewById<EditText>(R.id.etUsername)
        val firstName = findViewById<EditText>(R.id.etFirstName)
        val lastName = findViewById<EditText>(R.id.etLastName)
        val dob = findViewById<EditText>(R.id.etDob)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnCreate = findViewById<FrameLayout>(R.id.btnCreateAccount)
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val backArrow = findViewById<ImageView>(R.id.backArrow)

        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        btnCreate.setOnClickListener {
            val u = username.text.toString()
            val f = firstName.text.toString()
            val l = lastName.text.toString()
            val d = dob.text.toString()
            val e = email.text.toString()
            val p = password.text.toString()

            if (e.isEmpty() || p.isEmpty() || encodedImage == null) {
                Toast.makeText(this, "Please fill all fields and select a profile picture", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(e, p)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                    val userData = mapOf(
                        "username" to u,
                        "firstName" to f,
                        "lastName" to l,
                        "dob" to d,
                        "email" to e,
                        "profileImage" to encodedImage
                    )

                    database.reference.child("Users").child(uid).setValue(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LogInActivity::class.java))
                            finish()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Signup failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        backArrow.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            findViewById<ImageView>(R.id.profileImage).setImageBitmap(bitmap)
            
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }
    }
}
