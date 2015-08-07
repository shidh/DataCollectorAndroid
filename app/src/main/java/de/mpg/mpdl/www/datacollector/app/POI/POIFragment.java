package de.mpg.mpdl.www.datacollector.app.POI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Event.LocationChangedEvent;
import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.Model.MetaDataLocal;
import de.mpg.mpdl.www.datacollector.app.Model.POI;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.Retrofit.MetaDataConverter;
import de.mpg.mpdl.www.datacollector.app.Retrofit.RetrofitClient;
import de.mpg.mpdl.www.datacollector.app.Retrofit.StringConverter;
import de.mpg.mpdl.www.datacollector.app.utils.DeviceStatus;
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
    private View rootView;
    private String username = DeviceStatus.username;
    private String password = DeviceStatus.password;
    String queryKeyword = DeviceStatus.queryKeyword;
    MapView mMapView;
    private GoogleMap googleMap;

    private static Gson gson = new GsonBuilder()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private Location currentLocation;
    private double latitude = 48.147899;
    private double longitude = 11.57648;
    private String title = "";
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
            hidePDialog();

            // here get the string of Metadata Json
            for (DataItem item : dataList) {
                if(item != null) {
                    //convertMetaData(item);
                    MetaDataLocal metaDataLocal = MetaDataConverter.
                            metaDataToMetaDataLocal(item.getMetadata());

                    item.setMetaDataLocal(metaDataLocal);
                    //metaDataLocal.save();
                    //item.save();

                    //TODO
                    //1: zoom in and show data markers
                    //2: or click the POI marker then zoom in and show data markers

                    //click marker show pictures
                    latitude = item.getMetaDataLocal().getLatitude();
                    longitude = item.getMetaDataLocal().getLongitude();
                    title = item.getMetaDataLocal().getTitle();



                 Picasso.with(getActivity())
                        .load(item.getThumbnailUrl())
                        .resize(80,80)
                        .centerCrop()
                        .into(new Target(){
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // create marker
                        MarkerOptions marker = new MarkerOptions().position(
                                new LatLng(latitude, longitude)).title(title);
                        Log.v(LOG_TAG, title);
                        marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        //mainLayout.setBackground(new BitmapDrawable(getActivity().getResources(), bitmap));
                        googleMap.addMarker(marker);

                    }

                    @Override
                    public void onBitmapFailed(final Drawable errorDrawable) {
                        Log.d("TAG", "FAILED");
                        // create marker
                        MarkerOptions marker = new MarkerOptions().position(
                                new LatLng(latitude, longitude)).title(title);
                        // Changing marker icon
                        marker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                        googleMap.addMarker(marker);

                    }

                    @Override
                    public void onPrepareLoad(final Drawable placeHolderDrawable) {
                        Log.d("TAG", "Prepare Load");
                    }
                });
                    //marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                }
            }

        }

        @Override
        public void failure(RetrofitError error) {
            Log.v(LOG_TAG, String.valueOf(error.getResponse().getStatus()));
            Log.v(LOG_TAG, String.valueOf(error));
            Log.v(LOG_TAG, "get poi members failed");
            DeviceStatus.showSnackbar(rootView, "update poi members failed");
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
            //updatePoi("Allen");
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
        rootView = inflater.inflate(R.layout.fragment_section_poi, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        //googleMap.setBuildingsEnabled(true);

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
        //updatePoi("Allen");
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



    private void updatePoi(String query){
        RetrofitClient.getPOIsByQuery(query, callbackAlbum, username, password);
    }
    private void getPoiById(String poiId){
        RetrofitClient.getPOIById(poiId, callbackAlbum, username, password, new StringConverter());
    }



    private void getDataItemFroPoi(String poiId){
        RetrofitClient.getPoiMembers(poiId, callbackMember, username, password);
    }

    private void moveCamera(GoogleMap map, LatLng target){
        LatLng SYDNEY = new LatLng(-33.88,151.21);
        LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);

        target = SYDNEY;
        // Move the camera instantly to Sydney with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


        target = MOUNTAIN_VIEW;
        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(target)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



}

