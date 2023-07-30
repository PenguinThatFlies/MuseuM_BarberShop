package com.museum.barbershop

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.museum.barbershop.Adapter.ReviewAdapter
import com.museum.barbershop.Adapter.TimeAdapter
import com.museum.barbershop.Adapter.UserReviewAdapter
import com.museum.barbershop.Modul.AuthModul.SignUpData
import com.museum.barbershop.Modul.ReviewData
import com.museum.barbershop.Modul.times
import com.museum.barbershop.ProfileAdmin.AdminProfile
import com.museum.barbershop.databinding.ActivityReviewBinding
import java.text.SimpleDateFormat
import java.util.Date

class Review : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private val auth = Firebase.auth
    private lateinit var database: DatabaseReference

    private val reviewList = ArrayList<ReviewData>()
    private lateinit var reviewAdapter: ReviewAdapter

    private val barberList = ArrayList<SignUpData>()
    private lateinit var barberAdapter: AdminAdapter

    private val usersReviewList = ArrayList<SignUpData>()
    private lateinit var usersReviewAdapter: UserReviewAdapter

    private val user_id = auth.uid

    private var starF = ""
    private var float = 0.0f
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ProfileInfoFromFirebase()
        menu()
        reviewSelect()

        binding.barberRecycler.visibility = View.GONE

        binding.reviewRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.reviewRecycler.setHasFixedSize(true)

        reviewAdapter = ReviewAdapter(this, reviewList) {}
        binding.reviewRecycler.adapter = reviewAdapter
        binding.addreview.setOnClickListener {
            reviewAddFromFirebase()
        }

        binding.barberRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.barberRecycler.setHasFixedSize(true)

        barberAdapter = AdminAdapter(this, barberList) { barber->
            database = Firebase.database.reference
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users")
            val usersReviewRef = database.getReference("stars").child(barber.productId!!)
            val starCount = database.getReference("stars")
            val admin = database.getReference("admin")
            fun starCountFromFirebase() {
                starCount.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (productSnapshot in snapshot.children) {
//                            val star = productSnapshot.getValue(SignUpData::class.java)
                            var count = productSnapshot.childrenCount
                            count += 1
                            var c = 0
                            var cc = 0
                            usersReviewRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (starSnapshot in snapshot.children) {
                                        val starBarber = starSnapshot.getValue(SignUpData::class.java)
                                        for (i in 1 until count) {
                                            cc++
                                        }
                                        c += starBarber?.star!!

                                        val order = mapOf<String, Any>(
                                            "star" to c / cc
                                        )

                                        admin.child(barber.productId).updateChildren(order).addOnSuccessListener{
                                            val toast = Toast.makeText(this@Review,
                                                Html.fromHtml("<font color='#FFFFFF' ><b>Successfully</b></font>") , Toast.LENGTH_SHORT)
                                            val view = toast.view
                                            view?.apply {
                                                background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                            }
                                            toast.show()
                                        }.addOnFailureListener{
                                            Toast.makeText(this@Review, "An error occurred!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    // Handle the error
                                    Toast.makeText(this@Review, "Failed to read information!", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the error
                        Toast.makeText(this@Review, "Failed to read information!", Toast.LENGTH_SHORT).show()
                    }
                })
            }


            val alert = AlertDialog.Builder(this@Review)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.rating, null)

            val usersReview = inflater.inflate(R.layout.list_users, null)
            val listReview = inflater.inflate(R.layout.list_review_user, null)

            val message: TextView = listReview.findViewById(R.id.reviewmes)

            val ratingBar = dialogLayout.findViewById<RatingBar>(R.id.ratingBar)
            val ratingText = dialogLayout.findViewById<TextView>(R.id.ratingText)
            val ratingButton = dialogLayout.findViewById<Button>(R.id.ratingBtn)
            val textsupuser = dialogLayout.findViewById<EditText>(R.id.textsupuser)

            val addreviewbtn = usersReview.findViewById<Button>(R.id.addreviewbtn)
            val recyclerView = usersReview.findViewById<RecyclerView>(R.id.usersReviewRecycler)


            with(alert) {
                recyclerView.layoutManager = LinearLayoutManager(this@Review, LinearLayoutManager.VERTICAL,false)
                recyclerView.setHasFixedSize(true)

                addreviewbtn.setOnClickListener {
                    with(alert){
                        userRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user = snapshot.getValue(SignUpData::class.java) ?: return

                                ratingBar.setOnRatingBarChangeListener { rBar, fl, b ->
                                    ratingText.text = fl.toString()
                                    when(rBar.rating.toInt()){
                                        1 -> ratingText.text = "Very Bad"
                                        2 -> ratingText.text = "Bad"
                                        3 -> ratingText.text = "Good"
                                        4 -> ratingText.text = "Great"
                                        5 -> ratingText.text = "Awesome"
                                        else -> ratingText.text = ""
                                    }
                                }

                                ratingButton.setOnClickListener {
                                    val message = ratingBar.rating.toInt()
                                    if (user.productId == user_id) {
                                        fun addReview() {
                                            val order = mapOf<String, Any>(
                                                "username" to binding.mainUsername.text.toString(),
                                                "star" to message.toInt(),
                                                "message" to textsupuser.text.toString()
                                            )

                                            usersReviewRef.child(user_id!!).setValue(order).addOnSuccessListener{
                                                val toast = Toast.makeText(this@Review,
                                                    Html.fromHtml("<font color='#FFFFFF' ><b>Successfully</b></font>") , Toast.LENGTH_SHORT)
                                                val view = toast.view
                                                view?.apply {
                                                    background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                                }
                                                toast.show()
                                                startActivity(Intent(this@Review, Review::class.java))
                                                finish()
                                            }.addOnFailureListener{
                                                Toast.makeText(this@Review, "There was an error sending", Toast.LENGTH_SHORT).show()
                                            }}
                                        addReview()
                                        starCountFromFirebase()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle the error
                                Toast.makeText(this@Review, "Failed to read information!", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    setView(dialogLayout)
                    show()
                }


                usersReviewAdapter = UserReviewAdapter(this@Review, usersReviewList) {}
                recyclerView.adapter = usersReviewAdapter
                usersReviewRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        usersReviewList.clear()
                        for (productSnapshot in snapshot.children) {
                            val review = productSnapshot.getValue(SignUpData::class.java)

                            review?.let {
                                usersReviewList.add(review)
                            }
                        }
                        usersReviewAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the error
                        Toast.makeText(this@Review, "Failed to read information!", Toast.LENGTH_SHORT).show()
                    }
                })

                setView(usersReview)
                show()
            }
        }
        binding.barberRecycler.adapter = barberAdapter

        reviewListsFromFirebase()
        barbersListsFromFirebase()
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
                    startActivity(Intent(this@Review, AdminProfile::class.java))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Toast.makeText(this@Review, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun menu() {
        binding.menu.visibility = View.VISIBLE
        binding.menubtn.setImageResource(R.drawable.baseline_close_24)

        binding.contact.setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }

        binding.homerev.setOnClickListener {
            startActivity(Intent(this@Review, MainActivity::class.java))
        }

        binding.options.setOnClickListener {
            startActivity(Intent(this@Review, Options::class.java))
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

    }

    private fun reviewSelect() {
        binding.museum.setOnClickListener {
            binding.museum.setBackgroundResource(R.drawable.review_btn)
            binding.barber.setBackgroundResource(R.drawable.linear)
            binding.barberRecycler.visibility = View.GONE
            binding.reviewRecycler.visibility = View.VISIBLE
            binding.addreview.visibility = View.VISIBLE
            binding.addreviewLayout.visibility = View.VISIBLE
        }

        binding.barber.setOnClickListener {
            binding.museum.setBackgroundResource(R.drawable.linear)
            binding.barber.setBackgroundResource(R.drawable.review_btn)
            binding.barberRecycler.visibility = View.VISIBLE
            binding.reviewRecycler.visibility = View.GONE
            binding.addreview.visibility = View.GONE
            binding.addreviewLayout.visibility = View.GONE
        }
    }

    private fun reviewListsFromFirebase() {
        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val productRef = database.getReference("reviews")

        productRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reviewList.clear()
                for (productSnapshot in snapshot.children) {
                    val review = productSnapshot.getValue(ReviewData::class.java)

                    review?.let {
                        reviewList.add(review)
                    }
                }
                reviewAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Toast.makeText(this@Review, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun reviewAddFromFirebase() {
        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users")
        val alert = AlertDialog.Builder(this@Review)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alert_add_review, null)

        val textsup = dialogLayout.findViewById<EditText>(R.id.textsup)
        val send = dialogLayout.findViewById<Button>(R.id.send)


        with(alert) {

            userRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(SignUpData::class.java) ?: return

                    if (user.productId == user_id) {
                        send.setOnClickListener {
                            fun addReview() {
                                val tf = FirebaseDatabase.getInstance()
                                val orderRef = tf.getReference("reviews")
                                val key = orderRef.push().key


                                val order = mapOf<String, Any>(
                                    "productId" to user.productId!!,
                                    "username" to user.username!!,
                                    "review" to textsup.text.toString().trim()
                                )

                                orderRef.child(key!!).updateChildren(order).addOnSuccessListener{
//                                    Toast.makeText(this@Review, "Your review has been successfully added", Toast.LENGTH_SHORT).show()
                                    val toast = Toast.makeText(this@Review,
                                        Html.fromHtml("<font color='#FFFFFF' ><b>Your review has been successfully added</b></font>") , Toast.LENGTH_SHORT)
                                    val view = toast.view
                                    view?.apply {
                                        background?.setColorFilter(Color.rgb(205, 135, 101), PorterDuff.Mode.SRC_IN)
                                    }
                                    toast.show()
                                    startActivity(Intent(this@Review, Review::class.java))
                                    finish()
                                }.addOnFailureListener{
                                    Toast.makeText(this@Review, "There was an error sending", Toast.LENGTH_SHORT).show()
                                }}
                            addReview()
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    Toast.makeText(this@Review, "Failed to read information!", Toast.LENGTH_SHORT).show()
                }
            })

            setView(dialogLayout)
            show()
        }
    }

    private fun barbersListsFromFirebase() {
        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val barberRef = database.getReference("admin")

        barberRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                barberList.clear()
                for (productSnapshot in snapshot.children) {
                    val barbers = productSnapshot.getValue(SignUpData::class.java)

                    barbers?.let {
                        barberList.add(barbers)
                    }
                }
                barberAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Toast.makeText(this@Review, "Failed to read information!", Toast.LENGTH_SHORT).show()
            }
        })
    }



}