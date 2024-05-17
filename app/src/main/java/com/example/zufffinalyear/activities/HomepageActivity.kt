package com.example.zufffinalyear.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.zufffinalyear.R
import com.google.android.material.navigation.NavigationView

class HomepageActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = ContextCompat.getColor(this, R.color.yellow)

        // Find and set up toolbar
        toolbar = findViewById<Toolbar>(R.id.toolbar).apply { setSupportActionBar(this) }

        // Find drawer layout and navigation view
        drawerLayout = findViewById(R.id.drawer)
        navigationView = findViewById(R.id.navigationView)

        // Set up navigation controller
        navController = findNavController(R.id.fragmentContainerView)

        // Set up app bar configuration
        val topLevelDestinationIds = setOf(R.id.homeFragment)
        appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinationIds)
            .setOpenableLayout(drawerLayout)
            .build()

        // Set up action bar with navigation controller
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // Set up navigation view with navigation controller
        NavigationUI.setupWithNavController(navigationView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }
}
