package com.museum.barbershop.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.museum.barbershop.MainActivity
import com.museum.barbershop.Splash
import com.museum.barbershop.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignIn: AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loginListeners()
        binding.gosignup.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }

        binding.backimg.setOnClickListener {
            startActivity(Intent(this, Splash::class.java))
            finish()
        }
        binding.backtxt.setOnClickListener {
            startActivity(Intent(this, Splash::class.java))
            finish()
        }
    }
    private fun loginListeners() {
        binding.submit.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.pasword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }
}