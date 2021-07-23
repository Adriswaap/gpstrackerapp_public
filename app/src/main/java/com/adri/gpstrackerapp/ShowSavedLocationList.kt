package com.adri.gpstrackerapp

import android.R.*
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.adri.gpstrackerapp.databinding.ActivityShowSavedLocationListBinding
import java.lang.Exception

class ShowSavedLocationList : AppCompatActivity() {
    lateinit var binding : ActivityShowSavedLocationListBinding
    lateinit var locationsList : TextView
    lateinit var geocoder : Geocoder



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowSavedLocationListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationsList = binding.locationsListItemsTextview
        geocoder = Geocoder(this)

//        var list = Constants.LOCATION_ARRAY
         var finalString = ""

        for(location in Constants.LOCATION_LIST){
            println(location)
            finalString = finalString + location.toString() + "\n\n"
        }
        locationsList.text = finalString



    }
}


