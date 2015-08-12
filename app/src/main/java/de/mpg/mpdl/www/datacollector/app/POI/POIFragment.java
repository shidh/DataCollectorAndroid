package de.mpg.mpdl.www.datacollector.app.POI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
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
public class POIFragment extends Fragment implements GoogleMap.OnMarkerClickListener,
                                                     GoogleMap.OnInfoWindowClickListener {
    public static final String ARG_SECTION_NUMBER = "section_number";
    private final String LOG_TAG = POIFragment.class.getSimpleName();
    private ProgressDialog pDialog;
    private View rootView;
    private String username = DeviceStatus.username;
    private String password = DeviceStatus.password;
    private String queryKeyword = DeviceStatus.queryKeyword;

    private Location currentLocation;
    private MapView mMapView;
    private GoogleMap googleMap;
    private CameraPosition cameraPosition;

    private Marker mLastSelectedMarker;


    private static Gson gson = new GsonBuilder()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private double latitude = 48.147899;
    private double longitude = 11.57648;
    private String title = "";
    private String fileUrl = "";
    Callback<List<POI>> callbackAlbum = new Callback<List<POI>>() {
        @Override
        public void success(List<POI> pois, Response response) {
            for (POI poi : pois) {
                getDataItemFroPoi(poi.getId());
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.v(LOG_TAG, String.valueOf(error.getResponse()));
            Log.v(LOG_TAG, "get poi failed");

        }
    };


    Callback<List<DataItem>> callbackMember = new Callback<List<DataItem>>() {
        @Override
        public void success(List<DataItem> dataList, Response response) {
            hidePDialog();

            List<DataItem> dataListLocal = new ArrayList<DataItem>();
            for (DataItem item : dataList) {
                if (item != null) {
                    MetaDataLocal metaDataLocal = MetaDataConverter.
                            metaDataToMetaDataLocal(item.getMetadata());
                    if (metaDataLocal != null) {
                        item.setMetaDataLocal(metaDataLocal);
                        dataListLocal.add(item);
                    }
                }
            }

            addMarkersToMap(dataListLocal);
        }

        @Override
        public void failure(RetrofitError error) {
            Log.v(LOG_TAG, String.valueOf(error));
            Log.v(LOG_TAG, "get poi members failed");
            DeviceStatus.showSnackbar(rootView, "update poi members failed");
        }
    };



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
        updatePoi(queryKeyword);
        mMapView.onResume();

    }
    @Override
    public void onPause(){
        super.onPause();
        mMapView.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
        mMapView.onDestroy();

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

        onMapReady(googleMap);

        return rootView;

    }


    @Subscribe
    public void onGetNewLocationFromGPS(LocationChangedEvent event){
        currentLocation = event.location;

        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(12).build();

        googleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }



    private void updatePoi(String query){
        RetrofitClient.getPOIsByQuery(query, callbackAlbum, username, password);
    }

    private void getDataItemFroPoi(String poiId){
        RetrofitClient.getPoiMembers(poiId, callbackMember, username, password);
    }

    private void getPoiById(String poiId){
        RetrofitClient.getPOIById(poiId, callbackAlbum, username, password, new StringConverter());
    }


    public void onMapReady(GoogleMap map) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);


        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity()));

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Map with POIs");



        currentLocation = googleMap.getMyLocation();

        if(currentLocation != null){
            // latitude and longitude
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
        }

        LatLng latlng = new LatLng(latitude, longitude);
        cameraPosition = new CameraPosition.Builder()
                .target(latlng)
                .zoom(12)
                .build();

        //Log.v(LOG_TAG, latitude + ":" + longitude);

//        // Move the camera instantly to location with a zoom of 15.
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
//
//        // Zoom in, animating the camera.
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
//
//
//        googleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));


        moveCamera(googleMap, latlng, 12, null);
        updatePoi(queryKeyword);

    }

    //
    // Marker related listeners.
    //

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // This causes the marker at Perth to bounce into position when it is clicked.
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 2 * t);

                if (t > 0.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });

        final GoogleMap.CancelableCallback callback = new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }

            @Override
            public void onCancel() {
                Log.v(LOG_TAG, "cancel");

            }
        };

        handler.postDelayed(new Runnable() {
            public void run() {
                moveCamera(googleMap, marker.getPosition(), 18, callback);
            }
        }, 1000);

        mLastSelectedMarker = marker;



        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //DeviceStatus.showSnackbar(rootView, "Click Info Window");
        marker.hideInfoWindow();
        marker.showInfoWindow();
    }


    private void addMarkersToMap(List<DataItem> dataList) {
        for (DataItem item : dataList) {
            if (item != null) {
                MetaDataLocal metaDataLocal = item.getMetaDataLocal();
                if(metaDataLocal != null) {
                    final double markerLatitude = metaDataLocal.getLatitude();
                    final double markerLongitude = metaDataLocal.getLongitude();
                    final String title = metaDataLocal.getTitle();
                    final String fileUrl = item.getWebResolutionUrlUrl();

                    Picasso.with(getActivity())
                            .load(item.getThumbnailUrl())
                            .resize(80, 80)
                            .centerCrop()
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    // create marker
                                    MarkerOptions marker = new MarkerOptions()
                                                            .position(new LatLng(markerLatitude, markerLongitude))
                                                            .snippet(fileUrl)
                                                            .title(title);
                                    marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                    googleMap.addMarker(marker);
                                }

                                @Override
                                public void onBitmapFailed(final Drawable errorDrawable) {
                                    // create marker
                                    MarkerOptions marker = new MarkerOptions().position(
                                            new LatLng(markerLatitude, markerLongitude)).title(title);
                                    // Changing marker icon
                                    marker.icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                    googleMap.addMarker(marker);
                                }

                                @Override
                                public void onPrepareLoad(final Drawable placeHolderDrawable) {
                                    //Log.d("TAG", "Prepare Load");
                                }
                            });
                }
            }

        }

    }

    private boolean checkReady() {
        if (googleMap == null) {
            DeviceStatus.showSnackbar(rootView, getString(R.string.map_not_ready));
            return false;
        }
        return true;
    }

    /** Called when the Clear button is clicked. */
    public void onClearMap(View view) {
        if (!checkReady()) {
            return;
        }
        googleMap.clear();
    }

    /** Called when the Reset button is clicked. */
    public void onResetMap(View view) {
        if (!checkReady()) {
            return;
        }
        // Clear the map because we don't want duplicates of the markers.
        googleMap.clear();
        updatePoi(queryKeyword);
    }


    private void moveCamera(GoogleMap map, LatLng target, int zoomLevel, GoogleMap.CancelableCallback mapCallback){
        // Move the camera instantly to target with a zoom of 15.
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        //map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on target and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(target)             // Sets the center of the map to target
                .zoom(zoomLevel)                   // Sets the zoom
                //.bearing(90)                // Sets the orientation of the camera to east
                .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, mapCallback);
        }


    }

