package de.mpg.mpdl.www.datacollector.app.utils;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by allen on 09/04/15.
 */
public class DeviceStatus {

    public static final String username = "shi@mpdl.mpg.de";
    public static final String password = "allen";
    public static final String collectionID = "DCQVKA8esikfRTWi";
    public static final String queryKeyword = "DataCollector";
    public static final String BASE_URL = "https://dev-faces.mpdl.mpg.de/rest/";
    public static final String BASE_StatementUri = "http://dev-faces.mpdl.mpg.de/imeji/statement/";
    // Checks whether the device currently has a network connection
    public static boolean isNetworkEnabled(Activity activity) {
        ConnectivityManager connMgr = (ConnectivityManager)  activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnected();
        } else {
            return false;
        }
    }

    // Check whether the GPS sensor is activated
    public static boolean isGPSEnabled(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isNetworkLocationEnabled(Activity activity){
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static boolean isPassiveLocationEnabled(Activity activity){
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
    }

    public static void showToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }


}
