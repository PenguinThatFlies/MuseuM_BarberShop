package com.museum.barbershop

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaParser.InputReader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import android.window.SplashScreen
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import com.museum.barbershop.Adapter.AdminAdapter
import com.museum.barbershop.Adapter.TimeAdapter
import com.museum.barbershop.Modul.AuthModul.SignUpData
import com.museum.barbershop.Modul.OrderData
import com.museum.barbershop.Modul.times
import com.museum.barbershop.ProfileAdmin.AdminProfile
import com.museum.barbershop.databinding.ActivityMainBinding
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
import com.google.firestore.v1.StructuredQuery.Order
import com.museum.barbershop.Adapter.OrderListAdapter
import com.museum.barbershop.Auth.SignIn
import com.museum.barbershop.Modul.TimeData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener  {
    private lateinit var binding : ActivityMainBinding

    companion object {
        const val INTENT_PARCELABLE = "OBJECT_INTENT"
    }
    private lateinit var adminRecycler: RecyclerView
    private lateinit var timeRecycler: RecyclerView

    private val auth = Firebase.auth
    private lateinit var database: DatabaseReference
    var storageRef = Firebase.storage

    private val adminList = ArrayList<SignUpData>()
    private lateinit var adapter: AdminAdapter

    private val orderList = ArrayList<OrderData>()
    private lateinit var orderadapter: OrderListAdapter

    private var barber_id = ""
    private var barber_fullname = ""
    private var user_fullname = ""
    private var user_id = auth.uid!!

    private var hairbeard = 55
    private var hairPrice = 35
    private var child = 30
    private var bearPrice = 25
    private var clipperPrice = 20
    private var waxPrice = 10

    private var priceZero = 0


    var day = 0
    var month = 0
    var hour = 0
    var minute = 0

    var saveDay = 0
    var saveMonth = 0
    var saveHour = 0
    var saveMinute = ""

    @SuppressLint("ResourceAsColor", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storageRef = FirebaseStorage.getInstance()
        adminRecycler = findViewById(R.id.adminRecycler)
        timeRecycler = findViewById(R.id.timeRecycler)
        logout()

        binding.menu.visibility = View.VISIBLE
        binding.menubtn.setImageResource(R.drawable.baseline_close_24)
        binding.redball.visibility = View.GONE

        binding.hairbeardpricenot.text = hairbeard.toString()
        binding.hairpricenot.text = hairPrice.toString()
        binding.beardpricenot.text = bearPrice.toString()
        binding.childpricenot.text = child.toString()
        binding.calssicpricenot.text = clipperPrice.toString()
        binding.washpricenot.text = waxPrice.toString()

//        val dayFormat = SimpleDateFormat("d")
//        val day = dayFormat.format(Date())
//
//        val dayInt: Int = Integer.parseInt(day)
//
//        val monthFormat = SimpleDateFormat("MMM")
//        val month = monthFormat.format(Date())
//
//        binding.daytoday.text = day.toString()
//        binding.datatoday.text = month.toString()
//        binding.datanextday.text = month.toString()
//        binding.datatomorrow.text = month.toString()
//
//        binding.daytomorrow.text = (dayInt + 1).toString()
//        binding.daynextday.text = (dayInt + 2).toString()

        timeRecycler.layoutManager = LinearLayoutManager(this)
        timeRecycler.setHasFixedSize(true)

        binding.contact.setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }

        binding.profile.setOnClickListener {
            startActivity(Intent(this@MainActivity, AdminProfile::class.java))
        }

        binding.options.setOnClickListener {
            startActivity(Intent(this@MainActivity, Options::class.java))
        }

        binding.review.setOnClickListener {
            startActivity(Intent(this@MainActivity, Review::class.java))
        }

        binding.menubtn.setOnClickListener {
            if (binding.menu.visibility == View.GONE) {
                binding.menubtn.animate().apply {
                    rotation(180f)
                }.start()
                binding.menu.visibility = View.VISIBLE
                binding.menubtn.setImageResource(R.drawable.baseline_close_24)
                timeRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                timeRecycler.setHasFixedSize(true)
            } else if (binding.menu.visibility != View.GONE){
                binding.menu.visibility = View.GONE
                binding.scrollview.visibility = View.VISIBLE
                binding.notbtn.visibility = View.VISIBLE
                binding.cutline.visibility = View.VISIBLE
                binding.menubtn.animate().apply {
                    rotation(0f)
                }
                binding.menubtn.setImageResource(R.drawable.baseline_menu_24)
                timeRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                timeRecycler.setHasFixedSize(true)
            }
        }



        binding.barberlist.setOnClickListener {
            if (binding.barbervisibility.visibility == View.GONE) {
                binding.imageView3.animate().apply {
                    rotation(90f)
                }
            binding.barbervisibility.visibility = View.VISIBLE

            } else if (binding.barbervisibility.visibility != View.GONE){
                binding.barbervisibility.visibility = View.GONE
                binding.imageView3.animate().apply {
                    rotation(0f)
                }
            }
        }



        binding.servicevisibility.visibility = View.GONE


        ProfileInfoFromFirebase()

        binding.datatimevisibility.visibility = View.GONE


        adminRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        adminRecycler.setHasFixedSize(true)


        adapter = AdminAdapter(this, adminList) { admin ->
            val tf = FirebaseDatabase.getInstance()
            val orderRef = tf.getReference("order")
            val ttRef = tf.getReference("time")
            val dataFormat = SimpleDateFormat("d MMM")
            val data = dataFormat.format(Date())

            val order = mapOf<String, Any>(
                "User_ID" to user_id,
                "Barber_ID" to admin.productId!!,
                "Barber_ID_TF" to true,
                "Barber_Fullname" to admin.username!!,
                "User_Fullname" to binding.mainUsername.text,
                "Time_TF" to false
            )


            orderRef.child(user_id).updateChildren(order).addOnSuccessListener{
                val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>You have successfully selected Barber ${admin.username}</b></font>") , Toast.LENGTH_SHORT)
                val view = toast.view
                view?.apply {
                    background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                }
                toast.show()
            }.addOnFailureListener{
                val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>Failed to add barber</b></font>") , Toast.LENGTH_SHORT)
                val view = toast.view
                view?.apply {
                    background?.setColorFilter(Color.rgb(255, 74, 0), PorterDuff.Mode.SRC_IN)
                }
                toast.show()
            }
        }



        fun barberID(){
            database = Firebase.database.reference
            val database = FirebaseDatabase.getInstance()
            val productRef = database.getReference("order")

            productRef.child(user_id).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(OrderData::class.java) ?: return
                    barber_id = user.Barber_ID!!



                    if (user.Barber_ID_TF == true) {
                        timeRecycler.visibility = View.VISIBLE
                        binding.timeRecyclerLayout.visibility = View.VISIBLE

                        orderadapter = OrderListAdapter(this@MainActivity, orderList){}
                        timeRecycler.adapter = orderadapter
                        orderListsFromFirebase()

                        pickDate()
                    }

                    if (user.User_ID == user_id) {


                        binding.servicelist.setOnClickListener {
                            if (binding.servicevisibility.visibility == View.GONE) {
                                binding.imageView14.animate().apply {
                                    rotation(90f)
                                }
                                binding.servicevisibility.visibility = View.VISIBLE
                            } else if (binding.servicevisibility.visibility != View.GONE){
                                binding.servicevisibility.visibility = View.GONE
                                binding.imageView14.animate().apply {
                                    rotation(0f)
                                }
                            }
                        }

                        fun toast() {
                            binding.datatimelist.setOnClickListener {
                                val toast = Toast.makeText(
                                    this@MainActivity,
                                    Html.fromHtml("<font color='#FFFFFF' ><b>Please select Barber !</b></font>"),
                                    Toast.LENGTH_SHORT
                                )
                                val view = toast.view
                                view?.apply {
                                    background?.setColorFilter(
                                        Color.rgb(255, 74, 0),
                                        PorterDuff.Mode.SRC_IN
                                    )
                                }
                                toast.show()
                            }
                        }

                        fun  dataList(){
                            binding.datatimelist.setOnClickListener {
                                if (binding.datatimevisibility.visibility == View.GONE) {
                                    binding.imageView10.animate().apply {
                                        rotation(90f)
                                    }
                                    binding.datatimevisibility.visibility = View.VISIBLE
                                } else if (binding.datatimevisibility.visibility != View.GONE){
                                    binding.datatimevisibility.visibility = View.GONE
                                    binding.imageView10.animate().apply {
                                        rotation(0f)
                                    }
                                }
                            }
                        }
                        dataList()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
                }
            })
        }
        barberID()
        adminRecycler.adapter = adapter
        adminListsFromFirebase()
        servicesVisibilityTFFirebase()
        notification()
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
                    startActivity(Intent(this@MainActivity, AdminProfile::class.java))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logout() {
        binding.logoutMain.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this@MainActivity, Splash::class.java))
            finish()
        }
    }

    private fun adminListsFromFirebase() {
        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val productRef = database.getReference("admin")

        productRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                adminList.clear()
                for (productSnapshot in snapshot.children) {
                    val admin = productSnapshot.getValue(SignUpData::class.java)

                    barber_fullname = admin?.username!!

                    admin?.let {
                        adminList.add(admin)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun orderListsFromFirebase() {
        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val ooRef = database.getReference("order")

        ooRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                for (productSnapshot in snapshot.children) {
                    val order = productSnapshot.getValue(OrderData::class.java)

                    order?.let {
                        orderList.add(order)
                    }
                }
                orderadapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun serviceHaircutBeardFromFirebase() {
        val tf = FirebaseDatabase.getInstance()
        val orderRef = tf.getReference("order")


        val order = mapOf<String, Any>(
            "Service_1" to "Haircut & Beard",
            "Service_1_TF" to true
        )

        orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
//            Toast.makeText(this@MainActivity, "${"Haircut"} was added to services", Toast.LENGTH_SHORT).show()
            val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Haircut & Beard"} was added to services</b></font>") , Toast.LENGTH_SHORT)
            val view = toast.view
            view?.apply {
                background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
            }
            toast.show()
        }.addOnFailureListener{
            Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancleServiceHaircutBeardFromFirebase() {
        val tf = FirebaseDatabase.getInstance()
        val orderRef = tf.getReference("order")


        val order = mapOf<String, Any>(
            "Service_1" to "",
            "Service_1_TF" to false
        )

        orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
//            Toast.makeText(this@MainActivity, "${"Haircut"} service has been removed", Toast.LENGTH_SHORT).show()
            val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Haircut & Beard"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
            val view = toast.view
            view?.apply {
                background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
            }
            toast.show()
        }.addOnFailureListener{
            Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun serviceHaircutFromFirebase() {
            val tf = FirebaseDatabase.getInstance()
            val orderRef = tf.getReference("order")


            val order = mapOf<String, Any>(
                "Service_2" to "Haircut",
                "Service_2_TF" to true
            )

            orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
//                Toast.makeText(this@MainActivity, "${"Children's Haircut"} was added to services", Toast.LENGTH_SHORT).show()
                val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Haircut"} was added to services</b></font>") , Toast.LENGTH_SHORT)
                val view = toast.view
                view?.apply {
                    background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                }
                toast.show()
            }.addOnFailureListener{
                Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }

    }

    private fun cancleServiceHaircutFromFirebase() {
            val tf = FirebaseDatabase.getInstance()
            val orderRef = tf.getReference("order")


            val order = mapOf<String, Any>(
                "Service_2" to "",
                "Service_2_TF" to false
            )

            orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
//                Toast.makeText(this@MainActivity, "${"Children's Haircut"} service has been removed", Toast.LENGTH_SHORT).show()
                val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Haircut"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
                val view = toast.view
                view?.apply {
                    background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                }
                toast.show()
            }.addOnFailureListener{
                Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
    }


    private fun serviceChildreHaircutFromFirebase() {
            val tf = FirebaseDatabase.getInstance()
            val orderRef = tf.getReference("order")


            val order = mapOf<String, Any>(
                "Service_3" to "Children's Haircut",
                "Service_3_TF" to true
            )

            orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
                val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Children's Haircut"} was added to services</b></font>") , Toast.LENGTH_SHORT)
                val view = toast.view
                view?.apply {
                    background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                }
                toast.show()
            }.addOnFailureListener{
                Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }

    }

    private fun cancleServiceChildreHaircutFromFirebase() {
        val tf = FirebaseDatabase.getInstance()
        val orderRef = tf.getReference("order")


        val order = mapOf<String, Any>(
            "Service_3" to "",
            "Service_3_TF" to false
        )

        orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
//            Toast.makeText(this@MainActivity, "${"Beard Correction"} service has been removed", Toast.LENGTH_SHORT).show()
            val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Children's Haircut"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
            val view = toast.view
            view?.apply {
                background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
            }
            toast.show()
        }.addOnFailureListener{
            Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
        }

    }


    private fun serviceBearCorrectionFromFirebase() {
            val tf = FirebaseDatabase.getInstance()
            val orderRef = tf.getReference("order")


            val order = mapOf<String, Any>(
                "Service_4" to "Beard Correction",
                "Service_4_TF" to true
            )

            orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
//                Toast.makeText(this@MainActivity, "${"Beard Correction"} was added to services", Toast.LENGTH_SHORT).show()
                val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Beard Correction"} was added to services</b></font>") , Toast.LENGTH_SHORT)
                val view = toast.view
                view?.apply {
                    background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                }
                toast.show()
            }.addOnFailureListener{
                Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cancleServiceBearCorrectionFromFirebase() {
            val tf = FirebaseDatabase.getInstance()
            val orderRef = tf.getReference("order")


            val order = mapOf<String, Any>(
                "Service_4" to "",
                "Service_4_TF" to false
            )

            orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
//                Toast.makeText(this@MainActivity, "${"Beard Correction"} service has been removed", Toast.LENGTH_SHORT).show()
                val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Beard Correction"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
                val view = toast.view
                view?.apply {
                    background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                }
                toast.show()
            }.addOnFailureListener{
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clipperFromFirebase() {
        val tf = FirebaseDatabase.getInstance()
        val orderRef = tf.getReference("order")


        val order = mapOf<String, Any>(
            "Service_5" to "Clipper Haircut",
            "Service_5_TF" to true
        )

        orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
//                Toast.makeText(this@MainActivity, "${"Beard Correction"} was added to services", Toast.LENGTH_SHORT).show()
            val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Clipper Haircut"} was added to services</b></font>") , Toast.LENGTH_SHORT)
            val view = toast.view
            view?.apply {
                background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
            }
            toast.show()
        }.addOnFailureListener{
            Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cleanClipperServiceFromFirebase() {
        val tf = FirebaseDatabase.getInstance()
        val orderRef = tf.getReference("order")


        val order = mapOf<String, Any>(
            "Service_5" to "",
            "Service_5_TF" to false
        )

        orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
//            Toast.makeText(this@MainActivity, "${"Beard Correction"} service has been removed", Toast.LENGTH_SHORT).show()
            val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Clipper Haircut"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
            val view = toast.view
            view?.apply {
                background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
            }
            toast.show()
        }.addOnFailureListener{
            Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
        }
    }


    private fun waxFromFirebase() {
        val tf = FirebaseDatabase.getInstance()
        val orderRef = tf.getReference("order")


        val order = mapOf<String, Any>(
            "Service_6" to "Wax",
            "Service_6_TF" to true
        )

        orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
//                Toast.makeText(this@MainActivity, "${"Beard Correction"} was added to services", Toast.LENGTH_SHORT).show()
            val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Wax"} was added to services</b></font>") , Toast.LENGTH_SHORT)
            val view = toast.view
            view?.apply {
                background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
            }
            toast.show()
        }.addOnFailureListener{
            Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun waxServiceFromFirebase() {
        val tf = FirebaseDatabase.getInstance()
        val orderRef = tf.getReference("order")


        val order = mapOf<String, Any>(
            "Service_6" to "",
            "Service_6_TF" to false
        )

        orderRef.child(auth.uid!!).updateChildren(order).addOnSuccessListener{
//            Toast.makeText(this@MainActivity, "${"Beard Correction"} service has been removed", Toast.LENGTH_SHORT).show()
            val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Wax"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
            val view = toast.view
            view?.apply {
                background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
            }
            toast.show()
        }.addOnFailureListener{
            Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
        }
    }


    private fun servicesVisibilityTFFirebase() {
        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val productRef = database.getReference("order")

        productRef.child(user_id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderTf = snapshot.getValue(OrderData::class.java) ?: return


                if (user_id == orderTf.User_ID){

                    if (orderTf.Service_1_TF == true) {
                        binding.addhairbeard.animate().apply {
                            rotation(135f)
                        }.duration
                        binding.hairbeardLayout.setOnClickListener {
                            cancleServiceHaircutBeardFromFirebase()
                        }
                    } else if (orderTf.Service_1_TF == false) {
                        binding.cleanhair.visibility = View.VISIBLE
                        binding.addhairbeard.animate().apply {
                            rotation(0f)
                        }.duration
                        binding.hairbeardLayout.setOnClickListener {
                            serviceHaircutBeardFromFirebase()
                        }
                    } else {
                        binding.hairbeardLayout.setOnClickListener {
                            serviceHaircutBeardFromFirebase()
                        }
                    }

                    if (orderTf.Service_2_TF == true) {
                        binding.addhair.animate().apply {
                            rotation(135f)
                        }.duration
                        binding.hairLayout.setOnClickListener {
                            cancleServiceHaircutFromFirebase()
                        }
                    } else if (orderTf.Service_2_TF == false) {
                        binding.addhair.animate().apply {
                            rotation(0f)
                        }.duration
                        binding.hairLayout.setOnClickListener {
                            serviceHaircutFromFirebase()
                        }
                    } else {
                        binding.hairLayout.setOnClickListener {
                            serviceHaircutFromFirebase()
                        }
                    }

                    if (orderTf.Service_3_TF == true) {
                        binding.addchild.animate().apply {
                            rotation(135f)
                        }.duration
                        binding.childLayout.setOnClickListener {
                            cancleServiceChildreHaircutFromFirebase()
                        }
                    } else if (orderTf.Service_3_TF == false) {
                        binding.addchild.animate().apply {
                            rotation(0f)
                        }.duration
                        binding.childLayout.setOnClickListener {
                            serviceChildreHaircutFromFirebase()
                        }
                    } else {
                        binding.childLayout.setOnClickListener {
                            serviceChildreHaircutFromFirebase()
                        }
                    }


                    if (orderTf.Service_4_TF == true) {
                        binding.addbeard.animate().apply {
                            rotation(135f)
                        }.duration
                        binding.beardLayout.setOnClickListener {
                            cancleServiceBearCorrectionFromFirebase()
                        }
                    } else if (orderTf.Service_4_TF == false) {
                        binding.addbeard.animate().apply {
                            rotation(0f)
                        }.duration
                        binding.beardLayout.setOnClickListener {
                            serviceBearCorrectionFromFirebase()
                        }
                    } else {
                        binding.beardLayout.setOnClickListener {
                            serviceBearCorrectionFromFirebase()
                        }
                    }


                    if (orderTf.Service_5_TF == true) {
                        binding.addclipp.animate().apply {
                            rotation(135f)
                        }.duration
                        binding.clipperLayout.setOnClickListener {
                            cleanClipperServiceFromFirebase()
                        }
                    } else if (orderTf.Service_5_TF == false) {
                        binding.addclipp.animate().apply {
                            rotation(0f)
                        }.duration
                        binding.clipperLayout.setOnClickListener {
                            clipperFromFirebase()
                        }
                    } else {
                        binding.clipperLayout.setOnClickListener {
                            clipperFromFirebase()
                        }
                    }

                    if (orderTf.Service_6_TF == true) {
                        binding.addwax.animate().apply {
                            rotation(135f)
                        }.duration
                        binding.waxLayout.setOnClickListener {
                            waxServiceFromFirebase()
                        }
                    } else if (orderTf.Service_6_TF == false) {
                        binding.addwax.animate().apply {
                            rotation(0f)
                        }.duration
                        binding.waxLayout.setOnClickListener {
                            waxFromFirebase()
                        }
                    } else {
                        binding.waxLayout.setOnClickListener {
                            waxFromFirebase()
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("MissingInflatedId")
    private fun notification() {
        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val productRef = database.getReference("order")
        val adminRef = database.getReference("admin")
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.notification, null)
        val canclealert = dialogLayout.findViewById<ImageView>(R.id.canclealert)
        val namelastnot = dialogLayout.findViewById<TextView>(R.id.namelastnot)
        val imagenot = dialogLayout.findViewById<ImageView>(R.id.imagenot)
        val clocknot = dialogLayout.findViewById<TextView>(R.id.clocknot)
        val day = dialogLayout.findViewById<TextView>(R.id.day)
        val month = dialogLayout.findViewById<TextView>(R.id.month)

        val hairpricenot = dialogLayout.findViewById<TextView>(R.id.hairpricenot)
        val beardpricenot = dialogLayout.findViewById<TextView>(R.id.beardpricenot)
        val calssicpricenot = dialogLayout.findViewById<TextView>(R.id.calssicpricenot)
        val washpricenot = dialogLayout.findViewById<TextView>(R.id.washpricenot)
        val hairbeardpricenot = dialogLayout.findViewById<TextView>(R.id.hairbeardpricenot)
        val childpricenot = dialogLayout.findViewById<TextView>(R.id.childpricenot)

        val price = dialogLayout.findViewById<TextView>(R.id.price)

        val star1: ImageView = dialogLayout.findViewById(R.id.star111)
        val star2: ImageView = dialogLayout.findViewById(R.id.star222)
        val star3: ImageView = dialogLayout.findViewById(R.id.star333)
        val star4: ImageView = dialogLayout.findViewById(R.id.star444)
        val star5: ImageView = dialogLayout.findViewById(R.id.star555)


        val hairbeardLayout = dialogLayout.findViewById<LinearLayout>(R.id.hairbeardLayout)
        val hairLayoutnot = dialogLayout.findViewById<LinearLayout>(R.id.hairLayoutnot)
        val childLayoutnot = dialogLayout.findViewById<LinearLayout>(R.id.childLayoutnot)
        val beardLayoutnot = dialogLayout.findViewById<LinearLayout>(R.id.beardLayoutnot)
        val classLayoutnot = dialogLayout.findViewById<LinearLayout>(R.id.classLayoutnot)
        val washLayoutnot = dialogLayout.findViewById<LinearLayout>(R.id.washLayoutnot)
        val timeLayoutnot = dialogLayout.findViewById<LinearLayout>(R.id.timeLayoutnot)
        val barberLayoutnot = dialogLayout.findViewById<LinearLayout>(R.id.barberLayoutnot)

        val addhairbeard = dialogLayout.findViewById<ImageView>(R.id.addhairbeard)
        val addhairnot = dialogLayout.findViewById<ImageView>(R.id.addhairnot)
        val addchildnot = dialogLayout.findViewById<ImageView>(R.id.addchildnot)
        val addbeardnot = dialogLayout.findViewById<ImageView>(R.id.addbeardnot)
        val addclassnot = dialogLayout.findViewById<ImageView>(R.id.addclassnot)
        val addwashnot = dialogLayout.findViewById<ImageView>(R.id.addwashnot)

        val deleteOrder = dialogLayout.findViewById<ImageView>(R.id.deleteOrder)



        productRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(OrderData::class.java) ?: return

                if (user_id == order.User_ID){
                    binding.redball.visibility = View.VISIBLE
                    if (order.Barber_ID == barber_id && order.Time != "" || binding.redball.visibility == View.VISIBLE) {
                        binding.notbtn.setOnClickListener{

                            val alert = AlertDialog.Builder(this@MainActivity)


                            with(alert) {

                                hairpricenot.text = hairPrice.toString()
                                beardpricenot.text = bearPrice.toString()
                                calssicpricenot.text = clipperPrice.toString()
                                washpricenot.text = waxPrice.toString()
                                hairbeardpricenot.text = hairbeard.toString()
                                childpricenot.text = child.toString()

                                canclealert.setOnClickListener {
                                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
                                    finish()
                                }

                                adminRef.child(barber_id).addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val admin = snapshot.getValue(SignUpData::class.java) ?: return

                                        if (admin.productId == barber_id) {
                                            namelastnot.text = admin.username

                                            Glide.with(this@MainActivity)
                                                .load(admin.imageSrc)
                                                .into(imagenot)


                                            star1.visibility = View.GONE
                                            star2.visibility = View.GONE
                                            star3.visibility = View.GONE
                                            star4.visibility = View.GONE
                                            star5.visibility = View.GONE

                                            fun star0() {
                                                star1.setImageResource(R.drawable.baseline_star_border_24)
                                                star2.setImageResource(R.drawable.baseline_star_border_24)
                                                star3.setImageResource(R.drawable.baseline_star_border_24)
                                                star4.setImageResource(R.drawable.baseline_star_border_24)
                                                star5.setImageResource(R.drawable.baseline_star_border_24)
                                            }

                                            fun star1() {
                                                star1.visibility = View.VISIBLE
                                                star2.visibility = View.VISIBLE
                                                star3.visibility = View.VISIBLE
                                                star4.visibility = View.VISIBLE
                                                star5.visibility = View.VISIBLE
                                                star2.setImageResource(R.drawable.baseline_star_border_24)
                                                star3.setImageResource(R.drawable.baseline_star_border_24)
                                                star4.setImageResource(R.drawable.baseline_star_border_24)
                                                star5.setImageResource(R.drawable.baseline_star_border_24)
                                            }

                                            fun star2() {
                                                star1.visibility = View.VISIBLE
                                                star2.visibility = View.VISIBLE
                                                star3.visibility = View.VISIBLE
                                                star4.visibility = View.VISIBLE
                                                star5.visibility = View.VISIBLE
                                                star3.setImageResource(R.drawable.baseline_star_border_24)
                                                star4.setImageResource(R.drawable.baseline_star_border_24)
                                                star5.setImageResource(R.drawable.baseline_star_border_24)
                                            }

                                            fun star3() {
                                                star1.visibility = View.VISIBLE
                                                star2.visibility = View.VISIBLE
                                                star3.visibility = View.VISIBLE
                                                star4.visibility = View.VISIBLE
                                                star5.visibility = View.VISIBLE
                                                star4.setImageResource(R.drawable.baseline_star_border_24)
                                                star5.setImageResource(R.drawable.baseline_star_border_24)
                                            }

                                            fun star4() {
                                                star1.visibility = View.VISIBLE
                                                star2.visibility = View.VISIBLE
                                                star3.visibility = View.VISIBLE
                                                star4.visibility = View.VISIBLE
                                                star5.visibility = View.VISIBLE
                                                star5.setImageResource(R.drawable.baseline_star_border_24)
                                            }

                                            fun star5() {
                                                star1.visibility = View.VISIBLE
                                                star2.visibility = View.VISIBLE
                                                star3.visibility = View.VISIBLE
                                                star4.visibility = View.VISIBLE
                                                star5.visibility = View.VISIBLE
                                            }



                                            when(admin.star){
                                                null -> star0()
                                                0 -> star0()
                                                1 -> star1()
                                                2 -> star2()
                                                3 -> star3()
                                                4 -> star4()
                                                5 -> star5()
                                                else -> star0()
                                            }
                                        }

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Handle the error
                                    }
                                })


                                clocknot.text = order.Time
                                day.text = order.Day

                                when(order.Month.toString()){
                                    "0" -> month.text = "January"
                                    "1" -> month.text = "February "
                                    "2" -> month.text = "March "
                                    "3" -> month.text = "April "
                                    "4" -> month.text = "May "
                                    "5" -> month.text = "June  "
                                    "6" -> month.text = "July "
                                    "7" -> month.text = "August"
                                    "8" -> month.text = "September "
                                    "9" -> month.text = "October "
                                    "10" -> month.text = "November "
                                    "11" -> month.text = "December  "
                                }

//                                month.text = order.Month


                                if (order.Service_1_TF == true) {
                                    hairbeardLayout.visibility = View.VISIBLE
                                    priceZero += hairbeard
                                    price.text = priceZero.toString()
                                } else {
                                    hairbeardLayout.visibility = View.GONE
                                }

                                if (order.Service_2_TF == true) {
                                    hairLayoutnot.visibility = View.VISIBLE
                                    priceZero += hairPrice
                                    price.text = priceZero.toString()
                                } else {
                                    hairLayoutnot.visibility = View.GONE
                                }

                                if (order.Service_3_TF == true) {
                                    childLayoutnot.visibility = View.VISIBLE
                                    priceZero += child
                                    price.text = priceZero.toString()
                                } else {
                                    childLayoutnot.visibility = View.GONE
                                }

                                if (order.Service_4_TF == true) {
                                    beardLayoutnot.visibility = View.VISIBLE
                                    priceZero += bearPrice
                                    price.text = priceZero.toString()
                                } else {
                                    beardLayoutnot.visibility = View.GONE
                                }

                                if (order.Service_5_TF == true) {
                                    classLayoutnot.visibility = View.VISIBLE
                                    priceZero += clipperPrice
                                    price.text = priceZero.toString()

                                } else {
                                    classLayoutnot.visibility = View.GONE
                                }

                                if (order.Service_6_TF == true) {
                                    washLayoutnot.visibility = View.VISIBLE
                                    priceZero += waxPrice
                                    price.text = priceZero.toString()
                                } else {
                                    washLayoutnot.visibility = View.GONE
                                }

                                if (order.Time != "") {
                                    timeLayoutnot.visibility = View.VISIBLE
                                } else {
                                    timeLayoutnot.visibility = View.GONE
                                }


                                fun addAndDeleteOrder() {
                                    if (user_id == order.User_ID){

                                        fun hairbeard() {
                                            if (order.Service_1_TF == true) {
                                                addhairbeard.animate().apply {
                                                    rotation(135f)
                                                }.duration
                                                hairbeardLayout.setOnClickListener {
                                                    val tf = FirebaseDatabase.getInstance()
                                                    val orderRef = tf.getReference("order")
                                                    val orderTwoRef = tf.getReference("order")

                                                    orderRef.child(auth.uid!!).child("Service_1_TF").removeValue().addOnSuccessListener{
//                                                    Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
//                                                        Toast.makeText(this@MainActivity, "${"Haircut & Beard"} service has been removed", Toast.LENGTH_SHORT).show()
                                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Haircut & Beard"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
                                                        val view = toast.view
                                                        view?.apply {
                                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                        }
                                                        toast.show()
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }

                                                    orderTwoRef.child(auth.uid!!).child("Service_1").removeValue().addOnSuccessListener{
//                                                    Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
                                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Haircut & Beard"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
                                                        val view = toast.view
                                                        view?.apply {
                                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                        }
                                                        toast.show()
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }

                                                    orderTwoRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            val user = snapshot.getValue(OrderData::class.java) ?: return

                                                            if (user.Service_1_TF != null && user.Service_1 != null) {
                                                                hairbeardLayout.visibility = View.VISIBLE
                                                            } else {
                                                                hairbeardLayout.visibility = View.GONE
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            // Handle the error
                                                            Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    })

                                                    priceZero -= hairbeard
                                                    price.text = priceZero.toString()
                                                    hairbeardLayout.visibility = View.GONE
                                                }
                                            } else if (order.Service_1_TF == false) {
                                                addhairbeard.animate().apply {
                                                    rotation(0f)
                                                }.duration
                                                hairbeardLayout.setOnClickListener {
                                                    serviceHaircutBeardFromFirebase()
                                                }
                                            } else {
                                                hairbeardLayout.setOnClickListener {
                                                    serviceHaircutBeardFromFirebase()
                                                }
                                            }
                                        }
                                        hairbeard()

                                        fun hair() {
                                            if (order.Service_2_TF == true) {
                                                addhairnot.animate().apply {
                                                    rotation(135f)
                                                }.duration
                                                hairLayoutnot.setOnClickListener {
                                                    val tf = FirebaseDatabase.getInstance()
                                                    val orderRef = tf.getReference("order")
                                                    val orderTwoRef = tf.getReference("order")

                                                    orderRef.child(auth.uid!!).child("Service_2_TF").removeValue().addOnSuccessListener{
//                                                    Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
//                                                        Toast.makeText(this@MainActivity, "${"Haircut"} service has been removed", Toast.LENGTH_SHORT).show()
                                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Haircut"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
                                                        val view = toast.view
                                                        view?.apply {
                                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                        }
                                                        toast.show()
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }

                                                    orderTwoRef.child(auth.uid!!).child("Service_2").removeValue().addOnSuccessListener{
//                                                    Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
                                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>It was successfully deleted</b></font>") , Toast.LENGTH_SHORT)
                                                        val view = toast.view
                                                        view?.apply {
                                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                        }
                                                        toast.show()
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }

                                                    orderTwoRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            val user = snapshot.getValue(OrderData::class.java) ?: return

                                                            if (user.Service_2_TF != null && user.Service_2 != null) {
                                                                hairLayoutnot.visibility = View.VISIBLE
                                                            } else {
                                                                hairLayoutnot.visibility = View.GONE
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            // Handle the error
                                                            Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    })

                                                    priceZero -= hairPrice
                                                    price.text = priceZero.toString()
                                                    hairLayoutnot.visibility = View.GONE
                                                }
                                            } else if (order.Service_2_TF == false) {
                                                addhairnot.animate().apply {
                                                    rotation(0f)
                                                }.duration
                                                hairLayoutnot.setOnClickListener {
                                                    serviceHaircutFromFirebase()
                                                }
                                            } else {
                                                hairLayoutnot.setOnClickListener {
                                                    serviceHaircutFromFirebase()
                                                }
                                            }
                                        }
                                        hair()

                                        fun child() {
                                            if (order.Service_3_TF == true) {
                                                addchildnot.animate().apply {
                                                    rotation(135f)
                                                }.duration
                                                childLayoutnot.setOnClickListener {
                                                    val tf = FirebaseDatabase.getInstance()
                                                    val orderRef = tf.getReference("order")
                                                    val orderTwoRef = tf.getReference("order")

                                                    orderRef.child(auth.uid!!).child("Service_3_TF").removeValue().addOnSuccessListener{
//                                                    Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
//                                                        Toast.makeText(this@MainActivity, "${"Children's Haircut"} service has been removed", Toast.LENGTH_SHORT).show()
                                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Children's Haircut"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
                                                        val view = toast.view
                                                        view?.apply {
                                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                        }
                                                        toast.show()
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }

                                                    orderTwoRef.child(auth.uid!!).child("Service_3").removeValue().addOnSuccessListener{
//                                                    Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
                                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>It was successfully deleted</b></font>") , Toast.LENGTH_SHORT)
                                                        val view = toast.view
                                                        view?.apply {
                                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                        }
                                                        toast.show()
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }

                                                    orderTwoRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            val user = snapshot.getValue(OrderData::class.java) ?: return

                                                            if (user.Service_3_TF != null && user.Service_3 != null) {
                                                                childLayoutnot.visibility = View.VISIBLE
                                                            } else {
                                                                childLayoutnot.visibility = View.GONE
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            // Handle the error
                                                            Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    })

                                                    priceZero -= child
                                                    price.text = priceZero.toString()
                                                    childLayoutnot.visibility = View.GONE
                                                }
                                            } else if (order.Service_3_TF == false) {
                                                addhairnot.animate().apply {
                                                    rotation(0f)
                                                }.duration
                                                childLayoutnot.setOnClickListener {
                                                    serviceChildreHaircutFromFirebase()
                                                }
                                            } else {
                                                childLayoutnot.setOnClickListener {
                                                    serviceChildreHaircutFromFirebase()
                                                }
                                            }
                                        }
                                        child()

                                        fun beard() {
                                            if (order.Service_4_TF == true) {
                                                addbeardnot.animate().apply {
                                                    rotation(135f)
                                                }.duration
                                                beardLayoutnot.setOnClickListener {
                                                    val tf = FirebaseDatabase.getInstance()
                                                    val orderRef = tf.getReference("order")
                                                    val orderTwoRef = tf.getReference("order")

                                                    orderRef.child(auth.uid!!).child("Service_4_TF").removeValue().addOnSuccessListener{
//                                                    Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
//                                                        Toast.makeText(this@MainActivity, "${"Beard Correction"} service has been removed", Toast.LENGTH_SHORT).show()
                                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Beard Correction"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
                                                        val view = toast.view
                                                        view?.apply {
                                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                        }
                                                        toast.show()
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }

                                                    orderTwoRef.child(auth.uid!!).child("Service_4").removeValue().addOnSuccessListener{
//                                                    Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
                                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>It was successfully deleted</b></font>") , Toast.LENGTH_SHORT)
                                                        val view = toast.view
                                                        view?.apply {
                                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                        }
                                                        toast.show()
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }

                                                    orderTwoRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            val user = snapshot.getValue(OrderData::class.java) ?: return

                                                            if (user.Service_4_TF != null && user.Service_4 != null) {
                                                                beardLayoutnot.visibility = View.VISIBLE
                                                            } else {
                                                                beardLayoutnot.visibility = View.GONE
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            // Handle the error
                                                            Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    })

                                                    priceZero -= bearPrice
                                                    price.text = priceZero.toString()
                                                    beardLayoutnot.visibility = View.GONE
                                                }
                                            } else if (order.Service_4_TF == false) {
                                                addhairnot.animate().apply {
                                                    rotation(0f)
                                                }.duration
                                                beardLayoutnot.setOnClickListener {
                                                    serviceBearCorrectionFromFirebase()
                                                }
                                            } else {
                                                beardLayoutnot.setOnClickListener {
                                                    serviceBearCorrectionFromFirebase()
                                                }
                                            }
                                        }
                                        beard()

                                        fun clipper() {
                                            if (order.Service_5_TF == true) {
                                                addclassnot.animate().apply {
                                                    rotation(135f)
                                                }.duration
                                                classLayoutnot.setOnClickListener {
                                                    val tf = FirebaseDatabase.getInstance()
                                                    val orderRef = tf.getReference("order")
                                                    val orderTwoRef = tf.getReference("order")

                                                    orderRef.child(auth.uid!!).child("Service_5_TF").removeValue().addOnSuccessListener{
//                                                    Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
//                                                        Toast.makeText(this@MainActivity, "${"Clipper Haircut"} service has been removed", Toast.LENGTH_SHORT).show()
                                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Clipper Haircut"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
                                                        val view = toast.view
                                                        view?.apply {
                                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                        }
                                                        toast.show()
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }

                                                    orderTwoRef.child(auth.uid!!).child("Service_5").removeValue().addOnSuccessListener{
//                                                    Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
                                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>It was successfully deleted</b></font>") , Toast.LENGTH_SHORT)
                                                        val view = toast.view
                                                        view?.apply {
                                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                        }
                                                        toast.show()
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }

                                                    orderTwoRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            val user = snapshot.getValue(OrderData::class.java) ?: return

                                                            if (user.Service_5_TF != null && user.Service_5 != null) {
                                                                classLayoutnot.visibility = View.VISIBLE
                                                            } else {
                                                                classLayoutnot.visibility = View.GONE
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            // Handle the error
                                                            Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    })

                                                    priceZero -= clipperPrice
                                                    price.text = priceZero.toString()
                                                    classLayoutnot.visibility = View.GONE
                                                }
                                            } else if (order.Service_5_TF == false) {
                                                addhairnot.animate().apply {
                                                    rotation(0f)
                                                }.duration
                                                classLayoutnot.setOnClickListener {
                                                    clipperFromFirebase()
                                                }
                                            } else {
                                                classLayoutnot.setOnClickListener {
                                                    clipperFromFirebase()
                                                }
                                            }
                                        }
                                        clipper()

                                        fun wax() {
                                            if (order.Service_6_TF == true) {
                                                addwashnot.animate().apply {
                                                    rotation(135f)
                                                }.duration
                                                washLayoutnot.setOnClickListener {
                                                    val tf = FirebaseDatabase.getInstance()
                                                    val orderRef = tf.getReference("order")
                                                    val orderTwoRef = tf.getReference("order")

                                                    orderRef.child(auth.uid!!).child("Service_6_TF").removeValue().addOnSuccessListener{
//                                                    Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
//                                                        Toast.makeText(this@MainActivity, "${"Wax"} service has been removed", Toast.LENGTH_SHORT).show()
                                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>${"Wax"} service has been removed</b></font>") , Toast.LENGTH_SHORT)
                                                        val view = toast.view
                                                        view?.apply {
                                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                        }
                                                        toast.show()
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }

                                                    orderTwoRef.child(auth.uid!!).child("Service_6").removeValue().addOnSuccessListener{
//                                                    Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
                                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>It was successfully deleted</b></font>") , Toast.LENGTH_SHORT)
                                                        val view = toast.view
                                                        view?.apply {
                                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                        }
                                                        toast.show()
                                                    }.addOnFailureListener{
                                                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }

                                                    orderTwoRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            val user = snapshot.getValue(OrderData::class.java) ?: return

                                                            if (user.Service_6_TF != null && user.Service_6 != null) {
                                                                washLayoutnot.visibility = View.VISIBLE
                                                            } else {
                                                                washLayoutnot.visibility = View.GONE
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            // Handle the error
                                                            Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    })

                                                    priceZero -= waxPrice
                                                    price.text = priceZero.toString()
                                                    washLayoutnot.visibility = View.GONE
                                                }
                                            } else if (order.Service_6_TF == false) {
                                                addwashnot.animate().apply {
                                                    rotation(0f)
                                                }.duration
                                                washLayoutnot.setOnClickListener {
                                                    waxFromFirebase()
                                                }
                                            } else {
                                                washLayoutnot.setOnClickListener {
                                                    waxFromFirebase()
                                                }
                                            }
                                        }
                                        wax()
                                    }
                                }
                                fun deleteOrder() {
                                    val tf = FirebaseDatabase.getInstance()
                                    val orderRef = tf.getReference("order")

                                    orderRef.child(auth.uid!!).removeValue().addOnSuccessListener{
//                                        Toast.makeText(this@MainActivity, "It was successfully deleted", Toast.LENGTH_SHORT).show()
                                        val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>It was successfully deleted</b></font>") , Toast.LENGTH_SHORT)
                                        val view = toast.view
                                        view?.apply {
                                            background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                        }
                                        toast.show()
                                    }.addOnFailureListener{
                                        Toast.makeText(this@MainActivity, "Failed to delete information", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                addAndDeleteOrder()
                                deleteOrder.setOnClickListener {
                                    deleteOrder()
                                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
                                    finish()
                                }
                                setView(dialogLayout)
                                show()
                            }
                        }
                    }


                } else {
                    binding.redball.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }


    private fun starCountFromFirebase() {
        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val orderReviewRef = database.getReference("order").child(user_id)
        val orderCount = database.getReference("stars")
        val admin = database.getReference("admin")

        orderCount.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (productSnapshot in snapshot.children) {
                    val star = productSnapshot.getValue(SignUpData::class.java)
                    var count = productSnapshot.childrenCount
                    count += 1
                    var c = 0
                    var cc = 0
                    orderReviewRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (starSnapshot in snapshot.children) {
                                val orderBarber = starSnapshot.getValue(OrderData::class.java)
                                for (i in 1 until count) {
                                    cc++
                                }

                                val order = mapOf<String, Any>(
                                    "star" to c / cc
                                )

                                admin.child(user_id).updateChildren(order).addOnSuccessListener{
                                    val toast = Toast.makeText(this@MainActivity,
                                        Html.fromHtml("<font color='#FFFFFF' ><b> ${c / cc}</b></font>") , Toast.LENGTH_SHORT)
                                    val view = toast.view
                                    view?.apply {
                                        background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                    }
                                    toast.show()
                                }.addOnFailureListener{
                                    Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            // Handle the error
                            Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Toast.makeText(this@MainActivity, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getDateTimeCalendar() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        hour = cal.get(Calendar.HOUR)
    }

    private fun pickDate() {
        binding.selecttimetxt.setOnClickListener{
            getDateTimeCalendar()

            DatePickerDialog(this, this, month, day, hour).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfyYear: Int, dayOfMonth: Int) {
        saveDay = dayOfMonth
        saveMonth = monthOfyYear

        getDateTimeCalendar()
        TimePickerDialog(this, this, hour, minute, true).show()

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        saveHour = hourOfDay
        saveMinute = "00"

        binding.daytoday.text = saveDay.toString()
        binding.datatoday.text = saveMonth.toString()

        binding.clockselect.text = "${saveHour}:${saveMinute}"

        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val productRef = database.getReference("order")


        val ttt = mapOf<String, Any>(
            "Time" to "${saveHour}:${saveMinute}",
            "Month" to "$saveMonth",
            "Day" to saveDay.toString()
        )

        productRef.child(user_id).updateChildren(ttt).addOnSuccessListener{
//                            name.text = "$saveMonth $saveDay $saveHour"
            val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>Time: $saveMonth $saveDay $saveHour</b></font>") , Toast.LENGTH_SHORT)
            val view = toast.view
            view?.apply {
                background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)

            }
            toast.show()
        }.addOnFailureListener{
            val toast = Toast.makeText(this@MainActivity,Html.fromHtml("<font color='#FFFFFF' ><b>Failed to select time</b></font>") , Toast.LENGTH_SHORT)
            val view = toast.view
            view?.apply {
                background?.setColorFilter(Color.rgb(255, 74, 0), PorterDuff.Mode.SRC_IN)

            }
            toast.show()
        }
    }
}