package com.example.aidruginteractionchecker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        firebaseAuth = FirebaseAuth.getInstance() //init firebase auth

        val backBtn = findViewById<Button>(R.id.recoveryBackBtn) //back button init
        backBtn.setOnClickListener { //when clicked
            val sendToLogin = Intent(this, LoginPage::class.java) //send to login page
            startActivity(sendToLogin)
        }

        val recoveryEmail = findViewById<EditText>(R.id.recoveryEmailEntry) //email text and button init
        val recoveryBtn = findViewById<Button>(R.id.recoveryBtn)
        recoveryBtn.setOnClickListener { //when clicked
            if (recoveryEmail.text.toString() == "") { //if edit text is empty
                Toast.makeText(this, "Enter your account's Email!", Toast.LENGTH_SHORT).show() //tell em
            } else { //otherwise
                firebaseAuth.sendPasswordResetEmail(recoveryEmail.text.toString()) //send pass reset email
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) { //if successfully
                            Toast.makeText(this, "Password reset Email sent!", Toast.LENGTH_SHORT).show() //tell em
                        } else { //otherwise
                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show() //show error
                        }
                    }
            }
        }
    }
}