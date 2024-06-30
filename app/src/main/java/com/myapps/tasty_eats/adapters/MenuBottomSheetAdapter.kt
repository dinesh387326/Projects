package com.myapps.tasty_eats.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myapps.tasty_eats.databinding.SearchItemBinding

class MenuBottomSheetAdapter(private val menuItems: ArrayList<String>, private val menuItemPrice: ArrayList<String>
                              , private val menuImages: ArrayList<Int> ):  RecyclerView.Adapter<MenuBottomSheetAdapter.MenuBottomViewHolder>(){

    lateinit var binding:SearchItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuBottomViewHolder {
        binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuBottomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    override fun onBindViewHolder(holder: MenuBottomViewHolder, position: Int) {
       holder.menuImage.setImageResource(menuImages[position])
       holder.menuName.text = menuItems[position]
       holder.menuPrice.text = menuItemPrice[position]
    }

    class MenuBottomViewHolder(private val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root){
        val menuImage = binding.menuImage
        val menuName = binding.menuFood
        val menuPrice = binding.menuPrice
    }

}