package com.example.zufffinalyear.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.zufffinalyear.R
import com.example.zufffinalyear.databinding.ActivityHomepageBinding
import com.example.zufffinalyear.models.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
class HomepageActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomepageBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize view binding
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = ContextCompat.getColor(this, R.color.yellow)

        // Set up toolbar
        setSupportActionBar(binding.toolbar.toolbar)
        binding.toolbar.toolbar.setTitleTextColor(Color.BLACK)

        // Set up navigation controller
        val navController = findNavController(R.id.fragmentContainerView)

        // Set up app bar configuration
        val topLevelDestinationIds = setOf(R.id.homeFragment)
        appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinationIds)
            .setOpenableLayout(binding.drawer)
            .build()

        // Set up action bar with navigation controller
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // Set up navigation view with navigation controller
        NavigationUI.setupWithNavController(binding.navigationView, navController)

        // Initialize Firebase Auth and Database reference
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        // Fetch and display user details
        fetchUserDetails()

        // Handle navigation item clicks
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    // Handle logout
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, SigninActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> {
                    // Handle other navigation items
                    NavigationUI.onNavDestinationSelected(item, navController)
                    binding.drawer.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }
    }
    private fun fetchUserDetails() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val userDetails = dataSnapshot.getValue(UserDetails::class.java)
                        if (userDetails != null) {
                            val headerView: View = binding.navigationView.getHeaderView(0)
                            val farmNameText: TextView = headerView.findViewById(R.id.farm_name_text)
                            val emailText: TextView = headerView.findViewById(R.id.email_text)

                            farmNameText.text = userDetails.farmname
                            emailText.text = userDetails.email
                        }
                    } else {
                        Toast.makeText(this@HomepageActivity, "User details not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("HomepageActivity", "Failed to read user details", databaseError.toException())
                    Toast.makeText(this@HomepageActivity, "Failed to read user details", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }
}
