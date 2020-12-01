package com.myrtle.busschedule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addNavigation()
    }

    private fun addNavigation() = findViewById<BottomNavigationView>(R.id.bttm_bar)
        .setupWithNavController(findNavController(R.id.root))
}
