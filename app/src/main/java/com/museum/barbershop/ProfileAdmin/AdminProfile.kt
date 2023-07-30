package com.museum.barbershop.ProfileAdmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.museum.barbershop.Auth.SignIn
import com.museum.barbershop.MainActivity
import com.museum.barbershop.Modul.AuthModul.SignUpData
import com.museum.barbershop.R
import com.museum.barbershop.databinding.ActivityAdminProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.museum.barbershop.ContactActivity
import com.museum.barbershop.Options

class AdminProfile : AppCompatActivity() {
    private lateinit var binding: ActivityAdminProfileBinding

    private val auth = Firebase.auth
    private lateinit var database: DatabaseReference
    var storageRef = Firebase.storage


    private var barber_id = ""
    private var user_id = auth.uid!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storageRef = FirebaseStorage.getInstance()

        val userProfile = intent.getParcelableExtra<SignUpData>(MainActivity.INTENT_PARCELABLE)
        ProfileInfoFromFirebase()
        logout()
        menu()
    }


    private fun menu() {
        binding.menu.visibility = View.VISIBLE


        binding.menubtn.setOnClickListener {
            if (binding.menu.visibility == View.GONE) {
                binding.menubtn.animate().apply {
                    rotation(180f)
                }.start()
                binding.menu.visibility = View.VISIBLE
                binding.menubtn.setImageResource(R.drawable.baseline_close_24)
            } else if (binding.menu.visibility != View.GONE){
                binding.menu.visibility = View.GONE
                binding.menubtn.animate().apply {
                    rotation(0f)
                }.start()
                binding.menubtn.setImageResource(R.drawable.baseline_menu_24)

            }
        }

        binding.options.setOnClickListener {
            startActivity(Intent(this@AdminProfile, Options::class.java))

        }

        binding.contactProf.setOnClickListener {
            startActivity(Intent(this@AdminProfile, ContactActivity::class.java))
        }

        binding.homePage.setOnClickListener {
            startActivity(Intent(this@AdminProfile, MainActivity::class.java))

        }
    }

    private fun ProfileInfoFromFirebase() {
        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val productRef = database.getReference("users")

        productRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(SignUpData::class.java) ?: return

                binding.fullname.text = user.username

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminProfile, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logout() {
        binding.logout.setOnClickListener{
            auth.signOut()
            val i = Intent(this@AdminProfile, SignIn::class.java)
            startActivity(i)
            finish()
        }
    }
}