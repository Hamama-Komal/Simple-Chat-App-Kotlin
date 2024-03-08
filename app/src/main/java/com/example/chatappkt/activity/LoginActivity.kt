package com.example.chatappkt.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatappkt.R
import com.example.chatappkt.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        firebaseAuth = FirebaseAuth.getInstance()

        binding.txtSignup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish()
        }

        binding.imgNext.setOnClickListener {
            checkInputValues()
        }
    }

    private fun checkInputValues() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        when {
            email.isEmpty() -> {
                binding.edtEmail.error = "Email is required"
                binding.edtEmail.requestFocus()
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.edtEmail.error = "Valid Email is required"
                binding.edtEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.edtPassword.error = "Password is required"
                binding.edtPassword.requestFocus()
            }
            else -> loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    try {
                        task.exception?.let { e ->
                            Log.e(ContentValues.TAG, e.toString())
                            Toast.makeText(this@LoginActivity, e.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e(ContentValues.TAG, e.toString())
                        Toast.makeText(this@LoginActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


}