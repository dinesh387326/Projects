package com.myapps.tasty_eats_admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.myapps.tasty_eats_admin.adapters.PendingOrderAdapter
import com.myapps.tasty_eats_admin.databinding.ActivityPendingOrderBinding
import com.myapps.tasty_eats_admin.models.OrderItem

class PendingOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPendingOrderBinding
    private var pendingOrders: ArrayList<OrderItem> = arrayListOf()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

        binding.backButton.setOnClickListener{
            finish()
        }

        val userId = auth.currentUser!!.uid
        val userRef = databaseReference.child("user").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(items in snapshot.children){
                    if(items.key == "pendingOrders"){
                        pendingOrders.clear()
                        for(newItems in items.children){
                            val item = newItems.getValue(OrderItem::class.java)
                            if (item != null) {
                                pendingOrders.add(item)
                            }
                        }
                    }
                }

                if(pendingOrders.isNotEmpty()){
                    Log.d("Reached", "POA Reached")
                    val adapter = PendingOrderAdapter(pendingOrders,this@PendingOrderActivity,databaseReference,userId)
                    binding.pendingRecycler.layoutManager = LinearLayoutManager(this@PendingOrderActivity)
                    binding.pendingRecycler.adapter = adapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PendingOrderActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        })

    }
}