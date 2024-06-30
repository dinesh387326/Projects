package com.myapps.tasty_eats.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.myapps.tasty_eats.R
import com.myapps.tasty_eats.adapters.MenuBottomSheetAdapter
import com.myapps.tasty_eats.databinding.FragmentMenuBottomSheetBinding

class MenuBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentMenuBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMenuBottomSheetBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuFoodName = listOf("Burger","Sandwich","Pizza","Momo","Burger","Sandwich","Pizza","Momo")
        val menuItemPrice = listOf("$10","$7","$20","$5","$10","$7","$20","$5")
        val menuImage = listOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3,R.drawable.menu4,R.drawable.menu1,R.drawable.menu2,R.drawable.menu3,R.drawable.menu4)

        val adapter = MenuBottomSheetAdapter(ArrayList(menuFoodName),ArrayList(menuItemPrice),ArrayList(menuImage))
        binding.menuBottomRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.menuBottomRecycler.adapter = adapter

    }

    companion object {
    }
}