package com.example.myhappyplaces.activities

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myhappyplaces.R
import com.example.myhappyplaces.models.HappyPlaceModel


class HappyPlaceDetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_happy_place_detail)

        var happyPlaceDetailModel: HappyPlaceModel?=null

        if(intent.hasExtra(MainActivity.EXTRA_PLACES_DETAILS)){
            happyPlaceDetailModel = intent.getSerializableExtra(
                MainActivity.EXTRA_PLACES_DETAILS) as HappyPlaceModel

        }

        val toolbarHappyPlaceBar = findViewById<Toolbar>(R.id.toolbar_happy_place_detail)
        val ivPlaceImage = findViewById<AppCompatImageView>(R.id.iv_place_image)
        val tvDescription = findViewById<TextView>(R.id.tv_description)
        val tvLocation = findViewById<TextView>(R.id.tv_location)
        if(happyPlaceDetailModel!= null){
            setSupportActionBar(toolbarHappyPlaceBar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlaceDetailModel.title

            toolbarHappyPlaceBar.setNavigationOnClickListener {
                onBackPressed()
            }

            ivPlaceImage.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            tvDescription.text = happyPlaceDetailModel.description
            tvLocation.text = happyPlaceDetailModel.location

        }
    }
}