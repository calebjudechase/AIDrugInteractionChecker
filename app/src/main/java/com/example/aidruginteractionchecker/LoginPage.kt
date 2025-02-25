package com.example.aidruginteractionchecker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val btnLogin = findViewById<Button>(R.id.loginBtn) //temporary code to send to main on click
        btnLogin.setOnClickListener(){
            val mainPageSkip = Intent(this, MainActivity::class.java)
            startActivity(mainPageSkip)
        }

    }
}