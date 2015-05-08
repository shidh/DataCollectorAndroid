package de.mpg.mpdl.www.datacollector.app.POI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Subscribe;

import java.util.List;

import de.mpg.mpdl.www.datacollector.app.AsyncTask.GetAddressByCoordinatesTask;
import de.mpg.mpdl.www.datacollector.app.Event.LocationChangedEvent;
import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.Model.MetaDataLocal;
import de.mpg.mpdl.www.datacollector.app.Model.POI;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.Retrofit.MetaDataConverter;
import de.mpg.mpdl.www.datacollector.app.Retrofit.RetrofitClient;
import de.mpg.mpdl.www.datacollector.app.Retrofit.StringConverter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by allen on 15/04/15.
 */

/**
 * A dummy fragment representing a section of the app, but that simply displays dummy text.
 */
public class POIFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";
    private final String LOG_TAG = POIFragment.class.getSimpleName();
    private ProgressDialog pDialog;

    private String username = "shi@mpdl.mpg.de";
    private String password = "allen";
    String poiId = "XNdKJWI70TliDG4g";
    String queryKeyword = "DataCollector";
    MapView mMapView;
    private GoogleMap googleMap;

    private static Gson gson = new GsonBuilder()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private Location currentLocation;
    private double latitude = 48.147899;
    private double longitude = 11.57648;
    CameraPosition cameraPosition;

    Callback<List<POI>> callbackAlbum = new Callback<List<POI>>() {

        @Override
        public void success(List<POI> pois, Response response) {
            Log.v(LOG_TAG, "get poi OK");
            Log.v(LOG_TAG, gson.toJson(pois));
            Log.v(LOG_TAG, String.valueOf(response.getStatus()));

            for (POI poi : pois) {
                Log.v(LOG_TAG, poi.getId());

                getDataItemFroPoi(poi.getId());
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.v(LOG_TAG, String.valueOf(error));
            Log.v(LOG_TAG, String.valueOf(error.getResponse()));

            Log.v(LOG_TAG, "get poi failed");

        }
    };


    Callback<List<DataItem>> callbackMember = new Callback<List<DataItem>>() {
        @Override
        public void success(List<DataItem> dataList, Response response) {
            //adapter =  new CustomListAdapter(getActivity(), dataList);
            Log.v(LOG_TAG, "get poi members OK");
            Log.v(LOG_TAG, gson.toJson(dataList));

            // here get the string of Metadata Json
            for (DataItem item : dataList) {
                //convertMetaData(item);
                MetaDataLocal metaDataLocal = MetaDataConverter.
                        metaDataToMetaDataLocal(item.getMetadata());

                item.setMetaDataLocal(metaDataLocal);
//                metaDataLocal.save();
//                item.save();

                latitude = item.getMetaDataLocal().getLatitude();
                longitude = item.getMetaDataLocal().getLongitude();

                // create marker
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(latitude, longitude)).
                        title(item.getMetaDataLocal().getTitle());
                Log.v(LOG_TAG, item.getMetaDataLocal().getTitle());

                // Changing marker icon
                marker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                googleMap.addMarker(marker);
            }

            showToast("got new POI members");

        }

        @Override
        public void failure(RetrofitError error) {
            Log.v(LOG_TAG, String.valueOf(error.getResponse().getStatus()));
            Log.v(LOG_TAG, String.valueOf(error));
            Log.v(LOG_TAG, "get poi members failed");
            showToast("update poi members failed");
        }
    };



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.v(LOG_TAG, "onAttach");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        //TODO
        //updateDataItem(String AlbumId);
        //updatePoi(queryKeyword);
        Log.v(LOG_TAG, "start onCreate~~~");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "start onStart~~~");


    }

    @Override
    public void onResume(){
        super.onResume();
        Log.v(LOG_TAG, "start onResume~~~");


    }
    @Override
    public void onPause(){
        super.onPause();
        Log.v(LOG_TAG, "start onPause~~~");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
        Log.v(LOG_TAG, "start onDestroy~~~");
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_section_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            //TODO
            //updateDataItem(String AlbumId);
            updatePoi(queryKeyword);

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading...");
            pDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }





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
        //MarkerOptions marker = new MarkerOptions().position(
        //        new LatLng(latitude, longitude)).title("This is a POI");

        // Changing marker icon
        //marker.icon(BitmapDescriptorFactory
        //        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        //marker.draggable();
        //googleMap.setOnMarkerClickListener();

        // adding marker
        //googleMap.addMarker(marker);

        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        updatePoi(queryKeyword);

        return rootView;

    }


    @Subscribe
    public void onGetNewLocationFromGPS(LocationChangedEvent event){
        currentLocation = event.location;

        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(12).build();

        googleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        //getAddressByCoordinates(event.location.getLatitude(), event.location.getLongitude());

    }

    public void getAddressByCoordinates(double latitude, double longitude){
        GetAddressByCoordinatesTask fetchTask = new GetAddressByCoordinatesTask();
        fetchTask.execute(latitude, longitude);
    }


    private void updatePoi(String query){
        RetrofitClient.getPOIsByQuery(query, callbackAlbum, username, password);
    }
    private void getPoiById(String poiId){
        RetrofitClient.getPOIById(poiId, callbackAlbum, username, password, new StringConverter());
    }



    private void getDataItemFroPoi(String poiId){
        RetrofitClient.getPoiMembers(poiId, callbackMember, username, password);
    }




    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}

