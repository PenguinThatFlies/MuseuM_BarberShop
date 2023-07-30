package com.museum.barbershop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
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
import com.google.type.LatLng
import com.museum.barbershop.Adapter.AdminAdapter
import com.museum.barbershop.Modul.AuthModul.SignUpData
import com.museum.barbershop.ProfileAdmin.AdminProfile
import com.museum.barbershop.databinding.ActivityContactBinding
import javax.annotation.concurrent.ThreadSafe

class ContactActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mGoogleMap:GoogleMap? = null
    private lateinit var binding: ActivityContactBinding
    private val auth = Firebase.auth
    private lateinit var database: DatabaseReference
    var storageRef = Firebase.storage

    private var barber_id = ""
    private var barber_id_eq = ""
    private var user_id = auth.uid!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storageRef = FirebaseStorage.getInstance()
        ProfileInfoFromFirebase()
        menu()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        val location = com.google.android.gms.maps.model.LatLng(41.7244556,44.7478324)
        googleMap.addMarker(MarkerOptions().position(location).title("MuseuM Barbershop\nსადალაქო მუზეუმი"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    private fun ProfileInfoFromFirebase() {
        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val productRef = database.getReference("users")

        productRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(SignUpData::class.java) ?: return

                binding.mainUsername.text = user.username

                binding.user.setOnClickListener {
                    startActivity(Intent(this@ContactActivity, AdminProfile::class.java))
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Toast.makeText(this@ContactActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
        })
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
            startActivity(Intent(this@ContactActivity, Options::class.java))
        }

        binding.homeCont.setOnClickListener {
            startActivity(Intent(this@ContactActivity, MainActivity::class.java))
        }

        binding.profile.setOnClickListener {
            startActivity(Intent(this@ContactActivity, AdminProfile::class.java))
        }

        binding.review2.setOnClickListener {
            startActivity(Intent(this@ContactActivity, Review::class.java))
        }
    }
}