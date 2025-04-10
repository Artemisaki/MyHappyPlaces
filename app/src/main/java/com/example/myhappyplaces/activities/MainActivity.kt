package com.example.myhappyplaces.activities

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhappyplaces.R
import com.example.myhappyplaces.adapters.HappyPlacesAdapter
import com.example.myhappyplaces.database.DatabaseHandler
import com.example.myhappyplaces.models.HappyPlaceModel
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
        happyPlaceList: ArrayList<HappyPlaceModel>){

        val rvHappyPlacesList = findViewById<RecyclerView>(R.id.rv_happy_places_list)
        rvHappyPlacesList.layoutManager =
            LinearLayoutManager(this)
        rvHappyPlacesList.setHasFixedSize(true)

        val placesAdapter = HappyPlacesAdapter(this,happyPlaceList)
        rvHappyPlacesList.adapter = placesAdapter
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
    }
}