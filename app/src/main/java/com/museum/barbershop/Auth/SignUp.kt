package com.museum.barbershop.Auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.museum.barbershop.Modul.AuthModul.SignUpData
import com.museum.barbershop.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class SignUp:AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    var storageRef = Firebase.storage
    private lateinit var imageUri: Uri

    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()

        saveUser()

        binding.gosignin.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }

    }

    private fun saveUser() {
        binding.submit.setOnClickListener {
            val username = binding.username.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val pass = binding.pasword.text.toString().trim()
            val number = binding.number.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && number.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {

                        val database = Firebase.database
                        val userRef = database.getReference("users")

                        val user = SignUpData(
                            auth.uid.toString(),
                            null,
                            username,
                            email,
                            number.toString(),
                            pass,
                            0,
                            null
                        )

                                    val userMap = HashMap<String, Any>()
                                    userMap["productId"] = user.productId!!
                                    userMap["username"] = user.username!!
                                    userMap["email"] = user.email!!
                                    userMap["number"] = user.number!!
                                    userMap["password"] = user.password!!
                                    userMap["star"] = user.star!!

                                    userRef.child(auth.uid.toString()).setValue(userMap)
                                        .addOnSuccessListener {
                                            // Product saved successfully
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            // Handle the failure
                                        }
                            }
                        }
                        startActivity(Intent(this,SignIn::class.java))
                    }


    }
}