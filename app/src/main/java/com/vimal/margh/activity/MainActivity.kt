package com.vimal.margh.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.elevation.SurfaceColors
import com.vimal.margh.R
import com.vimal.margh.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        window.navigationBarColor = SurfaceColors.SURFACE_1.getColor(this)

        setupWithNavController(
            binding!!.navView,
            findNavController(this, R.id.nav_host_fragment_activity_main)
        )
    }
}