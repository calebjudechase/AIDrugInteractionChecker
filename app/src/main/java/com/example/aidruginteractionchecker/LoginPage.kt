package com.example.aidruginteractionchecker


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        firebaseAuth = FirebaseAuth.getInstance()
        if(firebaseAuth.currentUser != null && firebaseAuth.currentUser?.isEmailVerified == true ){ //if user already logged in
            val mainPageSkip = Intent(this, MainActivity::class.java)
            startActivity(mainPageSkip) //goes to main page
        } else {
            firebaseAuth.signOut()
        }

        val progressBar = findViewById<ProgressBar>(R.id.progressBarLogin) //sets progress bar to variable
        val btnLogin = findViewById<Button>(R.id.loginBtn)
        btnLogin.setOnClickListener{ //when login button clicked
            progressBar.visibility = View.VISIBLE
            val email = findViewById<EditText>(R.id.loginEmail).text.toString()
            val password = findViewById<EditText>(R.id.loginPassword).text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) { //if all fields are filled
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { //logs into account
                    if (it.isSuccessful) { //if successful
                        progressBar.visibility = View.INVISIBLE
                        if (firebaseAuth.currentUser?.isEmailVerified == true) {
                            val mainPageSkip = Intent(this, MainActivity::class.java)
                            startActivity(mainPageSkip) //goes to main page
                        }
                        else{
                            Toast.makeText(this, "Please verify email!", Toast.LENGTH_SHORT).show() //gives error
                        }
                    } else { //if unsuccessful
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show() //gives error
                        progressBar.visibility = View.INVISIBLE
                    }
                }
            }
            else {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(this, "Fill all empty fields!", Toast.LENGTH_SHORT).show()//gives error
            }
        }

        //Go to Registration Page Functionality
        val goToRegister = findViewById<TextView>(R.id.textGoToRegister)
        goToRegister.setOnClickListener{
            val sendToRegister = Intent(this, RegistrationPage::class.java)
            startActivity(sendToRegister)
        }

        //Go to Password Recovery Page Functionality
        val goToRecover = findViewById<TextView>(R.id.recoveryText)
        goToRecover.setOnClickListener{
            val sendToRecover = Intent(this, ForgotPassword::class.java)
            startActivity(sendToRecover)
        }
    }
}