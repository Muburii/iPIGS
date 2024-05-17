package com.example.zufffinalyear.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.zufffinalyear.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val SPLASH_DURATION = 3000 // Splash screen duration in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        // Post a delayed action to redirect to SignUpActivity after SPLASH_DURATION
        Handler(Looper.getMainLooper()).postDelayed({
            // Start SignUpActivity
            startActivity(Intent(this@SplashActivity, SigninActivity::class.java))
            // Finish SplashActivity
            finish()
        }, SPLASH_DURATION.toLong())
    }
}
