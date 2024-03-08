package com.example.chatappkt.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatappkt.R
import com.example.chatappkt.databinding.ActivitySignUpBinding
import com.example.chatappkt.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fdref: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.imgNext.setOnClickListener { checkValues() }

        binding.txtSignin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun checkValues() {

        val userName = binding.edtName.text.toString().trim()
        val userEmail = binding.edtEmail.text.toString().trim()
        val userPassword = binding.edtPassword.text.toString().trim()


        when {
            userName.isEmpty() -> {
                binding.edtName.error = "Please Enter Your Name!"
                binding.edtName.requestFocus()
            }
            userEmail.isEmpty() -> {
                binding.edtEmail.error = "Email is required!"
                binding.edtEmail.requestFocus()
            }
            !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches() -> {
                binding.edtEmail.error = "Please Enter a Valid Email!"
                binding.edtEmail.requestFocus()
            }
            userPassword.isEmpty() -> {
                binding.edtPassword.error = "Please Set a Password!"
                binding.edtPassword.requestFocus()
            }
            userPassword.length < 7 -> {
                binding.edtPassword.error = "Password must contain at least 7 characters!"
                binding.edtPassword.requestFocus()
            }

            else -> registerUser(userName, userEmail, userPassword)
        }
    }

    private fun registerUser(userName: String, userEmail: String, userPassword: String) {



        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnSuccessListener { authResult ->

                val userUid = firebaseAuth.uid!!
                addUserData(userName, userEmail, userUid)
                Toast.makeText(this@SignUpActivity, "$userName SignUp successfully!😊", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->

                Toast.makeText(this@SignUpActivity, e.message, Toast.LENGTH_SHORT).show()
            }

    }

    private fun addUserData(userName: String, userEmail: String, userUid: String) {

        fdref = FirebaseDatabase.getInstance().getReference()
        fdref.child("ChatUsers").child(userUid).setValue(User(userName, userEmail, userUid))
            .addOnCompleteListener {
                Toast.makeText(this@SignUpActivity, "Data Added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                Toast.makeText(this@SignUpActivity, e.message, Toast.LENGTH_SHORT).show()
            }

    }
}