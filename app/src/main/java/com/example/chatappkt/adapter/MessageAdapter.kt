package com.example.chatappkt.adapter

import android.content.Context
import android.provider.Telephony.Mms.Sent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappkt.R
import com.example.chatappkt.model.Messages
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Messages>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE  = 1;
    val ITEM_SENT = 2;


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1){
            // inflate receive message layout
            val view: View = LayoutInflater.from(context).inflate(R.layout.recieve_msg_layout, parent, false)
            return ReceiveViewHolder(view)
        }
        else{
            // inflate sent message layout
            val view: View = LayoutInflater.from(context).inflate(R.layout.send_msg_layout, parent, false)
            return SentViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {

        val currentMessages = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessages.senderId)){
            return ITEM_SENT
        }
        else{
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
       return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        // Message object
        val currentMessage = messageList[position]


        if(holder.javaClass == SentViewHolder::class.java){

            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message

        }
        else{

            val viewHolder = holder as ReceiveViewHolder
            holder.recieveMessage.text = currentMessage.message
        }
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val sentMessage = itemView.findViewById<TextView>(R.id.send_message)
    }
    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val recieveMessage = itemView.findViewById<TextView>(R.id.recieve_message)
    }

}