package com.example.chatappkt.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappkt.R
import com.example.chatappkt.adapter.UserAdapter
import com.example.chatappkt.databinding.ActivityMainBinding
import com.example.chatappkt.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var userList : ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userList = ArrayList()
        adapter = UserAdapter(this@MainActivity, userList)


        binding.iconLogout.setOnClickListener{
            showLogoutConfirmation(this)
        }

        binding.userRecycler.layoutManager = LinearLayoutManager(this)
        binding.userRecycler.adapter = adapter

        fetchData()

    }

    private fun fetchData() {

        mDbRef.child("ChatUsers").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()

                for(postSnapshot in snapshot.children){

                    val currentUser = postSnapshot.getValue(User::class.java)
                    if(firebaseAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun showLogoutConfirmation(context: Activity) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Logout Confirmation")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, which ->

                firebaseAuth.signOut()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finishAffinity()

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }
}