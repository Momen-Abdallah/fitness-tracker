package com.example.fitnesstracker

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.fitnesstracker.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationBar.itemActiveIndicatorColor = ColorStateList.valueOf(Color.parseColor("#661BA8F0"))

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHost.navController
        binding.bottomNavigationBar.setupWithNavController(navController)


        navController.addOnDestinationChangedListener{_,destination,_->
            binding.bottomNavigationBar.isVisible =
                destination.id == R.id.homeScreen ||
                destination.id == R.id.settingsScreen
        }


        binding.bottomNavigationBar.setOnItemSelectedListener {item->

            when(item.itemId){
                R.id.homeScreen->{
                    navController.navigate(R.id.homeScreen)
                }
                R.id.settingsScreen->{
                    navController.navigate(R.id.settingsScreen)
                }
            }

            true
        }


        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED)
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),10)



//        if (getSharedPreferences("pref", MODE_PRIVATE).getBoolean("login", false))
//            navHost.findNavController().navigate(R.id.homeScreen)


//        else
//            navHost.findNavController().navigate(R.id.)


    }
}