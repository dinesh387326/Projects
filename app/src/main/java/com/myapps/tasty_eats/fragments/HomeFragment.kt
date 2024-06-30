package com.myapps.tasty_eats.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.myapps.tasty_eats.R
import com.myapps.tasty_eats.adapters.PopularAdapter
import com.myapps.tasty_eats.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.CENTER_CROP))

        binding.imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
        binding.viewMenu.setOnClickListener{
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager,"Test")

        }
        binding.imageSlider.setItemClickListener(object : ItemClickListener{
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage = "Selected image $position"
                Toast.makeText(requireContext(),itemMessage,Toast.LENGTH_SHORT).show()
            }

        })
        val foodNames = listOf<String>("Burger","Sandwich","Pizza","Momo")
        val priceTags = listOf<String>("$10","$7","$20","$5")
        val foodImages = listOf<Int>(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3,R.drawable.menu4)

        val adapter = PopularAdapter(foodNames,priceTags,foodImages)
        binding.popularRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.popularRecycler.adapter = adapter

    }
    companion object {

    }
}