package com.adri.gpstrackerapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.adri.gpstrackerapp.Constants.FAST_UPDATE_INTERVAL
import com.adri.gpstrackerapp.Constants.NORMAL_UPDATE_INTERVAL
import com.adri.gpstrackerapp.Constants.PERMISSION_FINE_LOCATION
import com.adri.gpstrackerapp.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //text elements
    lateinit var latValTxt: TextView
    lateinit var lonValTxt: TextView
    lateinit var altValTxt: TextView
    lateinit var accValTxt: TextView
    lateinit var speValTxt: TextView
    lateinit var senValTxt: TextView
    lateinit var updValTxt: TextView
    lateinit var addValTxt: TextView
    lateinit var wayValTxt: TextView

    //switches
    lateinit var locSwitch: Switch
    lateinit var gpsSwitch: Switch

    //utils
    lateinit var locationCallback : LocationCallback
    lateinit var flpc: FusedLocationProviderClient
    lateinit var locreq: LocationRequest
    lateinit var geocoder: Geocoder

    //location
    lateinit var currentLocation : Location

    //buttons
    lateinit var newWayPointBtn : Button
    lateinit var showListBtn : Button
    lateinit var showMapBtn : Button
    lateinit var dropPinBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //initialize variables
        //text elements
        latValTxt = binding.lattitudeValueText
        lonValTxt = binding.longtitudeValueText
        altValTxt = binding.altitudeValueText
        accValTxt = binding.accuracyValueText
        speValTxt = binding.speedValueText
        senValTxt = binding.sensorText
        updValTxt = binding.locationUpdatesText
        addValTxt = binding.adresValueText
        wayValTxt = binding.waypointsCounterValue
        //switches
        locSwitch = binding.locationUpdateSwitch
        gpsSwitch = binding.gpsSwitch
        //buttons
        newWayPointBtn = binding.newWayPointBtn
        showListBtn = binding.locListBtn
        showMapBtn = binding.mapsBtn
        dropPinBtn = binding.dropPinBtn
        //utils
        geocoder = Geocoder(this)
        locreq = LocationRequest.create()
        locreq.interval = 1000 * NORMAL_UPDATE_INTERVAL
        locreq.fastestInterval = 1000 * FAST_UPDATE_INTERVAL
        locreq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)



        gpsSwitch.setOnClickListener() {
            if (gpsSwitch.isChecked) {
                locreq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                senValTxt.text = "GPS sensors"
            } else {
                locreq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                senValTxt.text = "Towers + WIFI"
            }
        }
        newWayPointBtn.setOnClickListener{
            //get gps location


            //add location to global list
            Constants.LOCATION_LIST.add(currentLocation)

        }

        showListBtn.setOnClickListener{
            val showListIntent = Intent(this, ShowSavedLocationList::class.java)
            startActivity(showListIntent)
        }

        showMapBtn.setOnClickListener {
            val mapsIntent = Intent(this, MapsActivity::class.java)
            startActivity(mapsIntent)
        }

        locSwitch.setOnClickListener {
            if (locSwitch.isChecked) {
                startLocationUpdates()
            } else {
                stopLocationUpdates()
            }
        }

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?){
                locationResult ?: return
                for(location in locationResult.locations){

                    updateUIValues(location)
                }
            }
        }
        updateGPS()
    }//end of OnCreate

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_FINE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS()
                } else run {
                    Toast.makeText(
                        this,
                        "Deze app heeft toestemming nodig om te kunnen functioneren",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }

//    override fun OnResume(){
//        super .onResume()
//        if()
//    }

    private fun updateGPS() {
        //get permmissions before updating stuf for realz

        flpc = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            flpc.lastLocation.addOnSuccessListener { location : Location? ->
                if (location != null) {
                    updateUIValues(location)
                }
            }
        } else {
            //request the permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_FINE_LOCATION
                )
            }
        }
    }

    private fun updateUIValues(location: Location) {
        currentLocation = location
        latValTxt.text = location.latitude.toString()
        lonValTxt.text = location.longitude.toString()
        if (location.hasAltitude()) {
            altValTxt.text = location.altitude.toString()
        } else {
            altValTxt.text = "This device cannot read the Altitude"
        }
        if (location.hasSpeed()) {
            speValTxt.text = location.speed.toString()
        } else {
            speValTxt.text = "This device cannot read the Speed"
        }
        accValTxt.text = location.accuracy.toString()
        try{
            var addresses : List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            addValTxt.text = addresses.get(0).getAddressLine(0)
        }catch (e : Exception){
            addValTxt.text = "Could not find address"
        }

        wayValTxt.text = Constants.LOCATION_LIST.size.toString()

    }

    private fun startLocationUpdates() {
        updValTxt.text = "Location tracking is on"
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            flpc.requestLocationUpdates(locreq,
                locationCallback,
                Looper.getMainLooper())
            updateGPS()
        }
    }

    private fun stopLocationUpdates() {
        updValTxt.text = "Location tracking is off"
        latValTxt.text = "Not tracking location"
        lonValTxt.text = "Not tracking location"
        altValTxt.text = "Not tracking location"
        speValTxt.text = "Not tracking location"
        accValTxt.text = "Not tracking location"
        senValTxt.text = "Not tracking location"
        addValTxt.text = "Not tracking location"

        flpc.removeLocationUpdates(locationCallback)
    }
}



