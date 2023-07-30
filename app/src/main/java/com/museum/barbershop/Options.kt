package com.museum.barbershop

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ProxyFileDescriptorCallback
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
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
import com.museum.barbershop.Adapter.AdminAdapter
import com.museum.barbershop.Modul.AuthModul.SignUpData
import com.museum.barbershop.ProfileAdmin.AdminProfile
import com.museum.barbershop.databinding.ActivityOptionsBinding
import java.text.SimpleDateFormat
import java.util.Date

class Options : AppCompatActivity() {

    private lateinit var binding: ActivityOptionsBinding

    private var auth = Firebase.auth
    private lateinit var database: DatabaseReference
    var storageRef = Firebase.storage

    private var user_id = auth.uid!!

    private var username = ""
    private var phone = ""
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storageRef = FirebaseStorage.getInstance()
        profileInfoFromFirebase()
        menu()
        binding.update.setOnClickListener {
            updateProfileFromFirebase()
        }

        binding.send.setOnClickListener {
            supportSendMessageFromFirebase()
        }
    }
    private fun profileInfoFromFirebase() {
        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val productRef = database.getReference("users")

        productRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(SignUpData::class.java) ?: return
//                resetPassword()

                username = user.username!!
                phone = user.number!!
                email = user.email!!
                password = user.password!!

                binding.username2.setText(user.username)
                binding.number2.setText(user.number)
                binding.email2.setText(user.email)
                binding.pasword2.setText(user.password)

                binding.mainUsername.text = user.username

                binding.user.setOnClickListener {
                    startActivity(Intent(this@Options, AdminProfile::class.java))

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Toast.makeText(this@Options, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun menu() {
        binding.menu.visibility = View.VISIBLE
        binding.menubtn.setImageResource(R.drawable.baseline_close_24)

        binding.contact.setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }

        binding.homeOpt.setOnClickListener {
            startActivity(Intent(this@Options, MainActivity::class.java))

        }

        binding.user.setOnClickListener {
            startActivity(Intent(this@Options, AdminProfile::class.java))

        }

        binding.profile.setOnClickListener {
            startActivity(Intent(this@Options, AdminProfile::class.java))

        }

        binding.review3.setOnClickListener {
            startActivity(Intent(this@Options, Review::class.java))

        }

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
                }
                binding.menubtn.setImageResource(R.drawable.baseline_menu_24)
            }
        }

        binding.profile.setOnClickListener {
            startActivity(Intent(this@Options, AdminProfile::class.java))

        }

        binding.languageselect.visibility = View.GONE

        binding.langugelinear.setOnClickListener {
            if (binding.languageselect.visibility == View.GONE) {
                binding.languageselect.visibility = View.VISIBLE
                binding.languageimg.animate().apply {
                    rotation(90f)

                }.duration
            } else if (binding.languageselect.visibility != View.GONE){
                binding.languageselect.visibility = View.GONE
                binding.languageimg.animate().apply {
                    rotation(0f)
                }
            }
        }

        //

        binding.profileEditlinear.setOnClickListener {
            if (binding.profileEditSelect.visibility == View.GONE) {
                binding.profileEditSelect.visibility = View.VISIBLE
                binding.profileEditimg.animate().apply {
                    rotation(90f)

                }.duration
            } else if (binding.profileEditSelect.visibility != View.GONE){
                binding.profileEditSelect.visibility = View.GONE
                binding.profileEditimg.animate().apply {
                    rotation(0f)
                }
            }
        }

        binding.supportinear.setOnClickListener {
            if (binding.supporSelect.visibility == View.GONE) {
                binding.supporSelect.visibility = View.VISIBLE
                binding.supportimg.animate().apply {
                    rotation(90f)

                }.duration
            } else if (binding.supporSelect.visibility != View.GONE){
                binding.supporSelect.visibility = View.GONE
                binding.supportimg.animate().apply {
                    rotation(0f)
                }
            }
        }

        binding.aboutlinear.setOnClickListener {
            if (binding.aboutSelect.visibility == View.GONE) {
                binding.aboutSelect.visibility = View.VISIBLE
                binding.aboutimg.animate().apply {
                    rotation(90f)

                }.duration
            } else if (binding.aboutSelect.visibility != View.GONE){
                binding.aboutSelect.visibility = View.GONE
                binding.aboutimg.animate().apply {
                    rotation(0f)
                }
            }
        }
    }

    private fun updateProfileFromFirebase() {
        val tf = FirebaseDatabase.getInstance()
        val orderRef = tf.getReference("users")

        val order = mapOf<String, Any>(
            "username" to binding.username2.text.toString().trim(),
            "number" to binding.number2.text.toString().trim(),
            "email" to binding.email2.text.toString().trim()
        )

        orderRef.child(user_id).updateChildren(order).addOnSuccessListener{
//            Toast.makeText(this@Options, "Information updated successfully", Toast.LENGTH_SHORT).show()
            val toast = Toast.makeText(this@Options,
                Html.fromHtml("<font color='#FFFFFF' ><b>Information updated successfully</b></font>") , Toast.LENGTH_SHORT)
            val view = toast.view
            view?.apply {
                background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
            }
            toast.show()
        }.addOnFailureListener{
            Toast.makeText(this@Options, "Failed to update information", Toast.LENGTH_SHORT).show()
        }
    }


    private fun supportSendMessageFromFirebase() {
        val tf = FirebaseDatabase.getInstance()
        val orderRef = tf.getReference("support")


        val order = mapOf<String, Any>(
            "User_ID" to user_id,
            "email" to binding.email2.text.toString().trim(),
            "username" to binding.username2.text.toString().trim(),
            "number" to binding.number2.text.toString().trim(),
            "text" to binding.textsup.text.toString().trim(),
        )

        orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
//            Toast.makeText(this@Options, "Message sent successfully", Toast.LENGTH_SHORT).show()
            val toast = Toast.makeText(this@Options,
                Html.fromHtml("<font color='#FFFFFF' ><b>Message sent successfully</b></font>") , Toast.LENGTH_SHORT)
            val view = toast.view
            view?.apply {
                background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
            }
            toast.show()
        }.addOnFailureListener{
            Toast.makeText(this@Options, "Failed to send message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetPassword() {
        auth = FirebaseAuth.getInstance()

        binding.update.setOnClickListener {
            auth.sendPasswordResetEmail(binding.pasword2.text.toString())
                .addOnSuccessListener {
                    val toast = Toast.makeText(this@Options,
                        Html.fromHtml("<font color='#FFFFFF' ><b>Information updated successfully</b></font>") , Toast.LENGTH_SHORT)
                    val view = toast.view
                    view?.apply {
                        background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                    }
                    toast.show()
                }
        }

    }

}