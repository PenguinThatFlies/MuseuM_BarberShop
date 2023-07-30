package com.museum.barbershop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.museum.barbershop.Auth.SignIn
import com.museum.barbershop.Auth.SignUp
import com.museum.barbershop.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signinnext.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
        }

        binding.signupnext.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
    }
}