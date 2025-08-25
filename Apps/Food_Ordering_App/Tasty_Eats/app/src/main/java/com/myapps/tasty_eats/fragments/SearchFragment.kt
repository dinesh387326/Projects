package com.myapps.tasty_eats.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.myapps.tasty_eats.DetailsActivity
import com.myapps.tasty_eats.R
import com.myapps.tasty_eats.SharedViewModel
import com.myapps.tasty_eats.adapters.CartAdapter
import com.myapps.tasty_eats.adapters.MenuBottomSheetAdapter
import com.myapps.tasty_eats.databinding.FragmentSearchBinding
import com.myapps.tasty_eats.models.AllMenu
import com.myapps.tasty_eats.models.CartItem

class SearchFragment : Fragment(),MenuBottomSheetAdapter.MenuClickListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuBottomSheetAdapter
    //firebase
    private var menuItems: ArrayList<AllMenu> = arrayListOf()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var searchFoodName : ArrayList<String> = arrayListOf()
    private var searchItemPrice : ArrayList<String> = arrayListOf()
    private var searchImage : ArrayList<String> = arrayListOf()
    private var searchShopIds : ArrayList<String> = arrayListOf()
    private var searchStocks: ArrayList<Boolean> = arrayListOf()

    private var filterMenuFood: MutableList<String> = mutableListOf()
    private var filterMenuItem: MutableList<String> = mutableListOf()
    private var filterMenuImage: MutableList<String> = mutableListOf()
    private var filterStocks: MutableList<Boolean> = mutableListOf()
    private var filterShopIds: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        databaseReference = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        binding = FragmentSearchBinding.inflate(inflater,container,false)

        return binding.root
    }

    private fun showAllMenu() {
        filterMenuFood.clear()
        filterMenuItem.clear()
        filterMenuImage.clear()
        filterShopIds.clear()
        filterStocks.clear()

        searchFoodName.forEachIndexed { index, _ ->
                filterMenuFood.add(searchFoodName[index])
                filterMenuItem.add(searchItemPrice[index])
                filterMenuImage.add(searchImage[index])
                filterShopIds.add(searchShopIds[index])
                filterStocks.add(searchStocks[index])
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String): Boolean {
                filterMenuItems(p0)

                return true
            }

            override fun onQueryTextChange(p0: String): Boolean {
                filterMenuItems(p0)
                return true
            }
        })
    }

    private fun filterMenuItems(p0: String) {
        filterMenuFood.clear()
        filterMenuItem.clear()
        filterMenuImage.clear()
        filterShopIds.clear()
        filterStocks.clear()

        searchFoodName.forEachIndexed { index, s ->
            if(s.contains(p0.toString(),ignoreCase = true)){
                filterMenuFood.add(s)
                filterMenuItem.add(searchItemPrice[index])
                filterMenuImage.add(searchImage[index])
                filterShopIds.add(searchShopIds[index])
                filterStocks.add(searchStocks[index])
            }
        }

        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val foodRef = databaseReference.child("menu")

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()
                searchFoodName.clear()
                searchItemPrice.clear()
                searchImage.clear()
                searchShopIds.clear()
                searchStocks.clear()

                for (foodSnapshot in snapshot.children) {
                    val id = foodSnapshot.key
                    for (food in foodSnapshot.children) {
                        val menuItem = food.getValue(AllMenu::class.java)
                        if(id != null){
                            searchShopIds.add(id)
                        }
                        menuItem?.let {
                            menuItems.add(menuItem)
                        }
                    }
                }

                for(each in menuItems){
                    each.foodName?.let {
                        searchFoodName.add(each.foodName)
                    }
                    each.foodPrice?.let {
                        searchItemPrice.add(each.foodPrice)
                    }
                    each.foodImage?.let {
                        searchImage.add(each.foodImage)
                    }
                    each.stocks?.let {
                        searchStocks.add(each.stocks!!)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
            }
        })

        adapter = MenuBottomSheetAdapter(filterMenuFood,filterMenuItem,filterMenuImage,filterStocks,this@SearchFragment,requireContext())
        binding.searchRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.searchRecycler.adapter = adapter

        showAllMenu()

        setupSearchView()
    }

    companion object {

    }

    override fun onClick(position: Int) {
//        val intent = Intent(requireContext(), DetailsActivity::class.java)
//        intent.putExtra("detailsFood",filterMenuFood[position])
//        intent.putExtra("detailsImage",filterMenuImage[position])
//        requireContext().startActivity(intent)
        val cartItem = CartItem(filterShopIds[position],filterMenuFood[position],filterMenuItem[position],filterMenuImage[position],1)
        val userId = auth.currentUser!!.uid

        databaseReference.child("user").child(userId).child("cartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(requireContext(),"Item added to Cart",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
        }

    }
}