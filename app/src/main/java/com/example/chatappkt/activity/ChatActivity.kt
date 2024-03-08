package com.example.chatappkt.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappkt.R
import com.example.chatappkt.adapter.MessageAdapter
import com.example.chatappkt.databinding.ActivityChatBinding
import com.example.chatappkt.model.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList : ArrayList<Messages>
    private lateinit var mDbRef : DatabaseReference

    var receiverRoom : String? = null
    var senderRoom : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mDbRef = FirebaseDatabase.getInstance().getReference()

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        val name = intent.getStringExtra("name")
        val receiverUid= intent.getStringExtra("uid")

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        binding.toolbarTitle.setTitle(name)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        binding.chatRecycler.layoutManager = LinearLayoutManager(this)
        binding.chatRecycler.adapter = messageAdapter

        // Fetch data on Recycler
        mDbRef.child("UserChats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()
                    for(postSnapshot in snapshot.children){

                        val messages = postSnapshot.getValue(Messages::class.java)
                        messageList.add(messages!!)
                    }
                    messageAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
             )

        // To add message to Firebase RealTime Database
        binding.sentBtn.setOnClickListener {

                val message = binding.messageBox.text.toString()
                val messageObject = Messages(message, senderUid)

                mDbRef.child("UserChats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {

                        mDbRef.child("UserChats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@ChatActivity, e.message , Toast.LENGTH_SHORT).show()
                    }

                binding.messageBox.text = null



        }



    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


}