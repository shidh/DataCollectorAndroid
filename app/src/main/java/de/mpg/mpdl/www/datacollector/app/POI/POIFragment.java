package de.mpg.mpdl.www.datacollector.app.POI;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import de.mpg.mpdl.www.datacollector.app.AsyncTask.GetAddressByCoordinatesTask;
import de.mpg.mpdl.www.datacollector.app.Event.LocationChangedEvent;
import de.mpg.mpdl.www.datacollector.app.R;

/**
 * Created by allen on 15/04/15.
 */

/**
 * A dummy fragment representing a section of the app, but that simply displays dummy text.
 */
public class POIFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";
    MapView mMapView;
    private GoogleMap googleMap;


    private Location currentLocation;
    private double latitude = 48.147899;
    private double longitude = 11.57648;
    CameraPosition cameraPosition;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_poi, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        googleMap = mMapView.getMap();

        if(currentLocation != null){
            // latitude and longitude
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
        }

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("This is a POI");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        //marker.draggable();

        // adding marker
        googleMap.addMarker(marker);
        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        return rootView;

    }


    @Subscribe
    public void onGetNewLocationFromGPS(LocationChangedEvent event){
        currentLocation = event.location;

        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        getAddressByCoordinates(event.location.getLatitude(), event.location.getLongitude());

    }

    public void getAddressByCoordinates(double latitude, double longitude){
        GetAddressByCoordinatesTask fetchTask = new GetAddressByCoordinatesTask();
        fetchTask.execute(latitude, longitude);
    }
}

