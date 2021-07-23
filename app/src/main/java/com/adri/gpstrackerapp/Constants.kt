package com.adri.gpstrackerapp

import android.location.Location

object Constants {
    val FAST_UPDATE_INTERVAL : Long = 2
    val NORMAL_UPDATE_INTERVAL : Long = 10
    val PERMISSION_FINE_LOCATION : Int = 321

    var LOCATION_LIST = ArrayList<Location>()

}