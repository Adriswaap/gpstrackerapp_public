package com.adri.gpstrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.adri.gpstrackerapp.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locPos : LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        println(Constants.LOCATION_LIST.size)
        if(Constants.MANY_BOOL){
            for(location in Constants.LOCATION_LIST){
                locPos = LatLng(location.latitude, location.longitude)
                println("Latitude = " + location.latitude + " Longtitude = " + location.longitude)
                mMap.addMarker(MarkerOptions().
                position(LatLng(location.latitude, location.longitude)).
                title("Latitude = " + location.latitude + " Longtitude = " + location.longitude))

            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locPos,15f))
        }else{
            var incoming : Intent = intent
            var lat : Double = incoming.extras?.getDouble("LATITUDE")!!
            var lon : Double = incoming.extras?.getDouble("LONGTITUDE")!!
            locPos = LatLng(lat,lon)
            mMap.addMarker(MarkerOptions().position(locPos).title("One click locationPin"))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locPos, 15f))
        }


        mMap.setOnMarkerClickListener{
            if(it.tag == null)
                it.tag = 0
            var clicks : Int =  Integer.parseInt(it.tag.toString())
            if(clicks == null)
                clicks = 0
            clicks++
            it.tag = clicks
            Toast.makeText(this, "marker " + it.title + " was clicked " + clicks + " times..", Toast.LENGTH_LONG).show()
            true
        }
    }
}