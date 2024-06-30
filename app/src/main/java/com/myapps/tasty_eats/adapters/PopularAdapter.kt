package com.myapps.tasty_eats.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myapps.tasty_eats.databinding.PopularItemBinding

class PopularAdapter(private val items:List<String>,private val prices:List<String>,private val images:List<Int>) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    class PopularViewHolder(private val binding: PopularItemBinding) : RecyclerView.ViewHolder(binding.root){
        val imageView = binding.foodImage
        val foodTextView = binding.foodText
        val priceTextView = binding.priceText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
        holder.foodTextView.text = items[position]
        holder.priceTextView.text = prices[position]
    }

}