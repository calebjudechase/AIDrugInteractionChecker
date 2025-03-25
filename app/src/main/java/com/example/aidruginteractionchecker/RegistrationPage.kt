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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class RegistrationPage : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_page)

        firebaseAuth = FirebaseAuth.getInstance()
        var db = Firebase.firestore
        var template = hashMapOf( //makes template hashmap in order to have init vals
            "Age" to "",
            "Sex" to "",
            "Meds" to "",
            "Conditions" to ""
        )
        // Register Button Functionality
        val progressBar = findViewById<ProgressBar>(R.id.progressBar) //sets progress bar to variable
        val btnRegister = findViewById<Button>(R.id.registerBtn) //sets register button to variable
        btnRegister.setOnClickListener{ //when register button clicked
            progressBar.visibility = View.VISIBLE
            val email = findViewById<EditText>(R.id.registerEmail).text.toString()
            val password = findViewById<EditText>(R.id.registerPassword).text.toString()
            val retypePassword = findViewById<EditText>(R.id.registerRetypePassword).text.toString()
//            println(email)
//            println(password)
//            println(retypePassword)

            if (email.isNotEmpty() && password.isNotEmpty() && retypePassword.isNotEmpty()) { //if all fields are filled
                if (password == retypePassword) { //if password and retyped password are the same
                    if (password.length >= 6){ //if password is at least 6 characters long
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ //makes account
                            if (it.isSuccessful) { //if successful
                                firebaseAuth.currentUser?.sendEmailVerification() //sends email verification
                                progressBar.visibility = View.INVISIBLE //progress bar invisible

                                db.collection("users").document(firebaseAuth.uid.toString()).set(template)

                                Toast.makeText(this, "Account successfully created! A verification link has been sent to your email!", Toast.LENGTH_LONG).show() //gives message that account made
                            }else { //if unsuccessful
                                progressBar.visibility = View.INVISIBLE //progress bar invisible
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show() //gives error
                            }
                        }
                    }
                    else{
                        Toast.makeText(this, "Password must at least 6 characters!", Toast.LENGTH_SHORT).show() //gives error
                        progressBar.visibility = View.INVISIBLE //progress bar invisible
                    }
                }
                else{
                    Toast.makeText(this, "Password is not matching!", Toast.LENGTH_SHORT).show()//gives error
                    progressBar.visibility = View.INVISIBLE //progress bar invisible
                }
            }
            else {
                Toast.makeText(this, "Fill all empty fields!", Toast.LENGTH_SHORT).show()//gives error
                progressBar.visibility = View.INVISIBLE //progress bar invisible
            }
        }

        //Switch To Login Functionality
        val goToLogin = findViewById<TextView>(R.id.textGoToLogin)
        goToLogin.setOnClickListener{
            val sendToLogin = Intent(this, LoginPage::class.java)
            startActivity(sendToLogin)
        }
    }

}