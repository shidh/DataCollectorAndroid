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

    public static final String username = "collector@mpdl.mpg.de";
    public static final String password = "collector1305";
    public static final String collectionID = "abstSICD0jGVf7B";
    public static final String queryKeyword = "DataCollector";
    public static final String BASE_URL = "https://spot.mpdl.mpg.de/rest/";
    public static final String BASE_StatementUri = "http://spot.mpdl.mpg.de/statement/";


    public static final String Metadata_Title_Id = "3IcFfanvCZrK4EV";
    public static final String Metadata_Author_Id = "qw5CdYt51GYe2xxm";
    public static final String Metadata_Location_Id = "XV3woO_9ppqMU00_";
    public static final String Metadata_Accuracy_Id = "G0Z_PeyEz_1TjEGI";
    public static final String Metadata_Device_Id = "huplxW3OJMDPHJDX";
    public static final String Metadata_Tags_Id = "l34M9BSqMvZx5kbR";

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
