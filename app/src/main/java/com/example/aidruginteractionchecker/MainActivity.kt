package com.example.aidruginteractionchecker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavView: BottomNavigationView //initializes the bottom navigation view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavView = findViewById(R.id.bottomNavigationView2) //sets the bottom nav view to the menu ID
        bottomNavView.setOnItemSelectedListener { menuItem -> //logic for switching fragments when menu used
            when(menuItem.itemId){
                R.id.bottomProfile -> { //if profile clicked goes to profile
                    switchFragment(profileFragment())
                    true
                }
                R.id.bottomInteractionChecker -> { //if interaction checker clicked goes to interaction checker
                    switchFragment(interactionCheckerFragment())
                    true
                }
                R.id.bottomHelp -> { //if help clicked goes to help
                    switchFragment(helpFragment())
                    true
                }
                else -> false //if nothing clicked does nothing
            }
        }

        switchFragment(profileFragment()) //starts at profile fragment
    }

    private fun switchFragment(fragment : Fragment) { //this function changes the current fragment
        val fragManager = supportFragmentManager //initializes as fragManager
        val fragTransaction = fragManager.beginTransaction() //initialises as fragTransaction
        fragTransaction.replace(R.id.frameLayout,fragment) //performs replacement
        fragTransaction.commit() //commits
    }
}