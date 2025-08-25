package com.myapps.tasty_eats.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.myapps.tasty_eats.databinding.OrderedItemBinding
import com.myapps.tasty_eats.models.CurrentOrderItem
import com.myapps.tasty_eats.models.DispatchedItems
import com.myapps.tasty_eats.models.OrderItem
import com.myapps.tasty_eats.models.RecentItem

class CurrentOrderAdapter(private var orderItems: ArrayList<CurrentOrderItem>,private val databaseReference: DatabaseReference
,private val context: Context,private val userId: String,private val userEmail: String,private val userPhone: String):
    RecyclerView.Adapter<CurrentOrderAdapter.CurrentOrderViewHolder>() {
    lateinit var binding: OrderedItemBinding

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrentOrderAdapter.CurrentOrderViewHolder {
        binding = OrderedItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CurrentOrderViewHolder(binding)
    }

    inner class CurrentOrderViewHolder(private val binding: OrderedItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bindAll(position_old: Int){
            val bitmap = decodeBase64ToBitmap(orderItems[position_old].foodImage!!)
            binding.orderFoodImage.setImageBitmap(bitmap)
            binding.orderFoodName.text = orderItems[position_old].foodName
            binding.orderFoodPrice.text = orderItems[position_old].foodPrice
            binding.orderQuantity.text = orderItems[position_old].foodQuantity.toString()
            binding.recievedBtn.setText(orderItems[position_old].status)

            binding.recievedBtn.setOnClickListener {
                val position = absoluteAdapterPosition
                if(position!= RecyclerView.NO_POSITION){
                    if(binding.recievedBtn.text.toString() == "Press if Received"){
                        val userRef = databaseReference.child("user").child(userId).child("currentOrders")
                        val ref = databaseReference.child("user").child(orderItems[position].shopId!!).child("dispatchedItems")
                        // updating delivery status
                        ref.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(items in snapshot.children){
                                    val item = items.getValue(DispatchedItems::class.java)
                                    if (item != null) {
                                        if(item.status == "Not Received" && item.foodName == orderItems[position].foodName && item.foodQuantity == orderItems[position].foodQuantity
                                            && (item.customerEmail == userEmail || item.customerPhone == userPhone)){
                                            items.ref.child("status").setValue("Received")
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                            }
                        })
                        // updating completedOrders and earnings of shop
                        databaseReference.child("user").child(orderItems[position].shopId!!).addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for(items in snapshot.children){
                                        if(items.key == "earnings"){
                                            val value = items.getValue(Int::class.java)
                                            val totalEarning = value?.plus(
                                                (orderItems[position].foodPrice?.toInt()
                                                    ?: 0) * (orderItems[position].foodQuantity
                                                    ?: 0)
                                            )
                                            items.ref.setValue(totalEarning)
                                        }
                                        if(items.key == "completedOrders"){
                                            val value = items.getValue(Int::class.java)
                                            val totalOrders = value?.plus(1)
                                            items.ref.setValue(totalOrders)
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                                }
                            })


                        // deleting that order from currentOrders
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(items in snapshot.children){
                                    val item = items.getValue(CurrentOrderItem::class.java)
                                    if (item != null) {
                                        if(item.shopId == orderItems[position].shopId &&
                                            item.foodName == orderItems[position].foodName &&
                                            item.foodQuantity == orderItems[position].foodQuantity){
                                            items.ref.removeValue()
                                            val newItem = RecentItem(orderItems[position].shopId,orderItems[position].foodName,
                                                orderItems[position].foodPrice,orderItems[position].foodImage)
                                            databaseReference.child("user").child(userId).child("recentOrders").push().setValue(newItem)
                                            // deleting from orderItems
                                            orderItems.removeAt(position)
                                            notifyItemRemoved(position)
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return orderItems.size
    }

    override fun onBindViewHolder(
        holder: CurrentOrderViewHolder,
        position: Int
    ) {
        holder.bindAll(position)
    }
}