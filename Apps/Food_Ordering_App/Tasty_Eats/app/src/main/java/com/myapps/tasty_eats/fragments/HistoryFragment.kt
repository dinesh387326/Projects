package com.myapps.tasty_eats.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.collection.floatObjectMapOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.myapps.tasty_eats.HistoryViewModel
import com.myapps.tasty_eats.R
import com.myapps.tasty_eats.SharedViewModel
import com.myapps.tasty_eats.adapters.BuyAgainAdapter
import com.myapps.tasty_eats.adapters.CartAdapter
import com.myapps.tasty_eats.adapters.CurrentOrderAdapter
import com.myapps.tasty_eats.databinding.FragmentHistoryBinding
import com.myapps.tasty_eats.models.AllMenu
import com.myapps.tasty_eats.models.CartItem
import com.myapps.tasty_eats.models.CurrentOrderItem
import com.myapps.tasty_eats.models.OrderItem
import com.myapps.tasty_eats.models.RecentItem


class HistoryFragment : Fragment(), BuyAgainAdapter.HistoryClickListener {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: BuyAgainAdapter
    //Firebase
    private var orderItems: ArrayList<CurrentOrderItem> = arrayListOf()
    private var recentOrders: ArrayList<RecentItem> = arrayListOf()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var userEmail: String
    private lateinit var userPhone: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Firebase Initialization
        databaseReference = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

        binding = FragmentHistoryBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = auth.currentUser!!.uid
        val orderItemRef = databaseReference.child("user").child(userId)

        orderItemRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                orderItems.clear()

                for(foodSnapshot in snapshot.children){
                    if(foodSnapshot.key == "currentOrders"){
                        for(item in foodSnapshot.children){
                            val orderItem = item.getValue(CurrentOrderItem::class.java)
                            if (orderItem != null) {
                                orderItems.add(orderItem)
                            }
                        }
                    }
                    if(foodSnapshot.key == "email") {
                        userEmail = foodSnapshot.getValue(String::class.java).toString()
                    }
                    if(foodSnapshot.key == "phone"){
                        userPhone = foodSnapshot.getValue(String::class.java).toString()
                    }
                }

                if(orderItems.isNotEmpty()){
                    context?.let {
                        binding.textOrderStatus.visibility = View.VISIBLE
                        binding.orderRecycler.visibility = View.VISIBLE
                        val adapter = CurrentOrderAdapter(orderItems,databaseReference,requireContext(),userId,userEmail,userPhone)
                        binding.orderRecycler.layoutManager = LinearLayoutManager(requireContext())
                        binding.orderRecycler.adapter = adapter
                    }
                }else{
                    binding.textOrderStatus.visibility = View.GONE
                    binding.orderRecycler.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),error.message, Toast.LENGTH_SHORT).show()
            }
        })

        orderItemRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                recentOrders.clear()

                for(foodSnapshot in snapshot.children){
                    if(foodSnapshot.key == "recentOrders"){
                        for(item in foodSnapshot.children){
                            val recentItem = item.getValue(RecentItem::class.java)
                            if (recentItem != null) {
                                recentOrders.add(recentItem)
                            }
                        }
                    }
                }
                if(recentOrders.isNotEmpty()){
                    context?.let {
                        adapter = BuyAgainAdapter(recentOrders,this@HistoryFragment,requireContext())
                        binding.historyRecycler.layoutManager = LinearLayoutManager(requireContext())
                        binding.historyRecycler.adapter = adapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),error.message, Toast.LENGTH_SHORT).show()
            }

        })

    }


    companion object {

    }

    override fun onClick(position: Int) {
        var flag = false

        databaseReference.child("menu").child(recentOrders[position].shopId!!).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(items in snapshot.children){
                    val item = items.getValue(AllMenu::class.java)
                    if (item != null) {
                        if(item.foodName == recentOrders[position].foodName){
                            if(item.stocks == true){
                                flag = true
                            }
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
            }

        })
        if(flag){
            val cartItem = CartItem(recentOrders[position].shopId,recentOrders[position].foodName,
                recentOrders[position].foodPrice,recentOrders[position].foodImage,1)
            val userId = auth.currentUser!!.uid

            databaseReference.child("user").child(userId).child("cartItems").push().setValue(cartItem).addOnSuccessListener {
                Toast.makeText(requireContext(),"Item added to Cart",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireContext(),"Out of Stock",Toast.LENGTH_SHORT).show()
        }

    }
}