package de.mpg.mpdl.www.datacollector.app.utils;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by allen on 13/04/15.
 */
public class MyLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d("LOCATION CHANGED", location.getLatitude() + "");
            Log.d("LOCATION CHANGED", location.getLongitude() + "");
        }

//        if (locationTraceEnabled != null && locationTraceEnabled) {
//            CustomLocation customLocation = new CustomLocation(location);
//            poiLocationTrace.add(customLocation);
//        }
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /*
     * In response to a request to start updates, send a request to Location
     * Services
     */
//    private void startPeriodicUpdates() {
//
//        // Create a new location parameters object
//        LocationRequest locationRequest = LocationRequest.create();
//
//        // Set the update interval ceiling in milliseconds
//        locationRequest.setFastestInterval(LOCALIZATION_LOWER_LIMIT);
//
//        // Set the update interval in milliseconds
//        locationRequest.setInterval(LOCALIZATION_UPPER_LIMIT);
//
//        // Use high accuracy
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
//    }
//
//    /*
//     * In response to a request to stop updates, send a request to Location
//     * Services
//     */
//    private void stopPeriodicUpdates() {
//
//        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//    }
}
