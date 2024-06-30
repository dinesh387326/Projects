package com.myapps.tasty_eats.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myapps.tasty_eats.databinding.CartItemBinding

class CartAdapter(private val cartItems: MutableList<String>, private val cartItemPrice: MutableList<String>
    , private val cartImage: MutableList<Int>
): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    lateinit var binding: CartItemBinding
    private val itemQuantities = IntArray(cartItems.size){ 1 }
    inner class CartViewHolder(private val binding: CartItemBinding): RecyclerView.ViewHolder(binding.root) {
        val foodImage = binding.imageView5
        val foodName = binding.foodName
        val itemPrice = binding.cartPrice
        val quantity = binding.textQuantity
        init {
            binding.buttonMinus.setOnClickListener{
                val position = adapterPosition
                if(position!= RecyclerView.NO_POSITION){
                    if(itemQuantities[position]>1){
                        itemQuantities[position]--
                        quantity.text = itemQuantities[position].toString()
                    }
                }
            }
            binding.buttonPlus.setOnClickListener{
                val position = adapterPosition
                if(position!= RecyclerView.NO_POSITION){
                    if(itemQuantities[position]<10){
                        itemQuantities[position]++
                        quantity.text = itemQuantities[position].toString()
                    }
                }
            }
            binding.buttonDelete.setOnClickListener {
                val position = adapterPosition
                if(position!= RecyclerView.NO_POSITION){
                    cartImage.removeAt(position)
                    cartItems.removeAt(position)
                    cartItemPrice.removeAt(position)
                    itemQuantities[position] = 1
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        binding = CartItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return  cartItems.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.foodImage.setImageResource(cartImage[position])
        holder.foodName.text = cartItems[position]
        holder.itemPrice.text = cartItemPrice[position]
        holder.quantity.text = itemQuantities[position].toString()
    }
}