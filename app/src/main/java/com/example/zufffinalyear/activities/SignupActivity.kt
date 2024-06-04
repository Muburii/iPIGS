package com.example.zufffinalyear.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.zufffinalyear.R
import com.example.zufffinalyear.databinding.ActivitySignupBinding
import com.example.zufffinalyear.models.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val farmrole = resources.getStringArray(R.array.farm_role_list)
        val farmroleAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, farmrole)
        binding.autoCompleteTextView.setAdapter(farmroleAdapter)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = ContextCompat.getColor(this, R.color.yellow)

        // Initialize Firebase Auth and Database references
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        binding.welcome.setOnClickListener {
            registerUser()
        }

        binding.alreadyregistered.setOnClickListener {
            startActivity(Intent(applicationContext, SigninActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmpasswordEditText.text.toString().trim()
        val farmrole = binding.autoCompleteTextView.text.toString().trim()
        val farmname = binding.farmNameEdittext.text.toString().trim()

        // Validate input fields
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
            return
        }
        if (!password.contains(Regex("\\d"))) {
            Toast.makeText(this, "Password must contain at least one number", Toast.LENGTH_SHORT).show()
            return
        }
        if (!password.contains(Regex("[!@#\$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?]"))) {
            Toast.makeText(this, "Password must contain at least one special character", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(farmrole)) {
            Toast.makeText(this, "Select a role", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(farmname)) {
            Toast.makeText(this, "Enter farm name", Toast.LENGTH_SHORT).show()
            return
        }

        // Show progress bar
        binding.progressBar.visibility = View.VISIBLE

        // Create user with email and password using Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Hide progress bar
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    user?.uid?.let { uid ->
                        val userDetails = UserDetails(email, password, farmrole, farmname)
                        databaseReference.child(uid).setValue(userDetails)
                            .addOnSuccessListener {
                                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                                navigateToLogin()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to store user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } ?: run {
                        Toast.makeText(this, "Current user is null.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, SigninActivity::class.java))
        finish()
    }
}
