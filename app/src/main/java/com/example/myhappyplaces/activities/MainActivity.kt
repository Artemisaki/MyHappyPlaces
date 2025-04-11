package com.example.myhappyplaces.activities

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhappyplaces.R
import com.example.myhappyplaces.adapters.HappyPlacesAdapter
import com.example.myhappyplaces.database.DatabaseHandler
import com.example.myhappyplaces.models.HappyPlaceModel
import com.example.myhappyplaces.utils.SwipeToEditCallback
import com.example.myhappyplaces.utils.SwipeToDeleteCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAddHappyPlace = findViewById<FloatingActionButton>(R.id.fabAddHappyPlace)

        btnAddHappyPlace.setOnClickListener {

            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getHappyPlacesListFromLocalDB()
    }

    private fun setupHappyPlacesRecyclerView(
        happyPlaceList: ArrayList<HappyPlaceModel>) {

        val rvHappyPlacesList = findViewById<RecyclerView>(R.id.rv_happy_places_list)
        rvHappyPlacesList.layoutManager =
            LinearLayoutManager(this)
        rvHappyPlacesList.setHasFixedSize(true)

        val placesAdapter = HappyPlacesAdapter(this, happyPlaceList)
        rvHappyPlacesList.adapter = placesAdapter

        placesAdapter.setOnClickListener(object : HappyPlacesAdapter.OnClickListener {
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)

                intent.putExtra(EXTRA_PLACES_DETAILS, model)
                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val rvHappyPlacesList = findViewById<RecyclerView>(R.id.rv_happy_places_list)
                val adapter = rvHappyPlacesList.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(
                    this@MainActivity,
                    viewHolder.adapterPosition,
                    ADD_PLACE_ACTIVITY_REQUEST_CODE
                )
            }
        }


        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rvHappyPlacesList)


        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val rvHappyPlacesList = findViewById<RecyclerView>(R.id.rv_happy_places_list)
                val adapter = rvHappyPlacesList.adapter as HappyPlacesAdapter

                adapter.removeAt(viewHolder.adapterPosition)

                getHappyPlacesListFromLocalDB()

            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rvHappyPlacesList)


    }

    private fun getHappyPlacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList : ArrayList<HappyPlaceModel>  =dbHandler.getHappyPlacesList()
        val rvHappyPlacesList = findViewById<RecyclerView>(R.id.rv_happy_places_list)
        val tvNoRecordsAvailable = findViewById<TextView>(R.id.tv_no_records_available)

        if(getHappyPlaceList.size > 0){
            rvHappyPlacesList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE
            setupHappyPlacesRecyclerView(getHappyPlaceList)
        }else{
            rvHappyPlacesList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getHappyPlacesListFromLocalDB()
            }else{
                Log.e("Activity", "Cancelled or Back pressed")
            }
        }
    }


    companion object{
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_PLACES_DETAILS = "extra_place_details"
    }
}