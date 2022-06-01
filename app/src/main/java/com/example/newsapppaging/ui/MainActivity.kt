package com.example.newsapppaging.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapppaging.R
import com.example.newsapppaging.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = ActivityMainBinding.inflate(
            layoutInflater
        )

        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView =
            binding.bottomNav.setupWithNavController(navController)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.newsSearchFragment, R.id.headlinesCollectionFragment, R.id.bookmarksFragment)
        )



        setupActionBarWithNavController(navController, appBarConfiguration)

    }
}