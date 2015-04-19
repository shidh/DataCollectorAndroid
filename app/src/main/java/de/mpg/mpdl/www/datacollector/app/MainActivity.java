package de.mpg.mpdl.www.datacollector.app;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.Locale;

import de.mpg.mpdl.www.datacollector.app.SectionList.ListSectionFragment;
import de.mpg.mpdl.www.datacollector.app.Workflow.LaunchpadSectionFragment;

//import android.support.v4.app.FragmentTransaction;




//public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        LaunchpadSectionFragment.OnLocationUpdatedListener{

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    LaunchpadSectionFragment workflow;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;


    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 2000; // Update location every 5 sec
    private static int FATEST_INTERVAL = 1000; // 2 sec
    private static int DISPLACEMENT = 0; // 2 meters

    private TextView lblLocation;
    private RatingBar ratingView;
    private ImageView btnStartLocationUpdates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ActiveAndroid.initialize(this);

        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        //final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }





        // For Location
        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

        // Show location button click listener
//        btnShowLocation.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                displayLocation();
//            }
//        });

        // Toggling the periodic location updates
//        btnStartLocationUpdates.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                togglePeriodicLocationUpdates();
//            }
//        });
//



    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {

            mGoogleApiClient.connect();
        }
        Log.e(LOG_TAG, "start onStart~~~");
    }

    // recover the last status
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(LOG_TAG, "start onRestart~~~");
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        Log.e(LOG_TAG, "start onResume~~~");
    }

    // save the current status
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        Log.e(LOG_TAG, "start onPause~~~");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        Log.e(LOG_TAG, "start onStop~~~");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(LOG_TAG, "start onDestroy~~~");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent showSettingIntent = new Intent(this, SettingsActivity.class);
            //showSettingIntent.putExtra(Intent.EXTRA_TEXT, forecast);
            //showDetailIntent.setData();
            //startService(showDetailIntent);
            startActivity(showSettingIntent);

            return true;
        } else if (id == R.id.action_map){
            openPreferredLocationInMap();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    private void openPreferredLocationInMap(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String location = sharedPrefs.getString(
                                getString(R.string.pref_location_key),
                                getString(R.string.pref_location_default));

        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                             .appendQueryParameter("q", location)
                             .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this,  "Couldn't call " + location +
                    ", no receiving apps installed!", duration);
            toast.show();
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment;
            Bundle args = new Bundle();
            switch (position) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    fragment =  new LaunchpadSectionFragment();
                    args.putInt(LaunchpadSectionFragment.ARG_SECTION_NUMBER, position + 1);
                    fragment.setArguments(args);
                    return fragment;

                case 1:
                    fragment =  new ListSectionFragment();
                    args.putInt(ListSectionFragment.ARG_SECTION_NUMBER, position + 1);
                    fragment.setArguments(args);
                    return fragment;

                case 2:
                    // The other sections of the app are dummy placeholders.
                    fragment = new CollectionFragment();
                    args.putInt(CollectionFragment.ARG_SECTION_NUMBER, position + 1);
                    fragment.setArguments(args);
                    return fragment;
                default:
                    // getItem is called to instantiate the fragment for the given page.
                    // Return a PlaceholderFragment (defined as a static inner class below).
                    return new PlaceholderFragment();
             }
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1);
                case 1:
                    return getString(R.string.title_section2);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }






     /*
     * Google api callback methods which needs for  GoogleApiClient
     *
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(LOG_TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        //togglePeriodicLocationUpdates();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }



    // the method which needs to be implemented for LocationListener
    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;
        showToast("Location data updated!");

        // Displaying the new location on UI
        displayLocation();

    }

    //the method for LaunchpadSectionFragment.OnLocationUpdatedListener
    @Override
    public void onLocationViewClicked(ImageView btnStartLocationUpdates) {
        togglePeriodicLocationUpdates(btnStartLocationUpdates );
    }


    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {
        double accuracy = 0.0;
        double longitude = 0.0;
        double latitude = 0.0;
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            accuracy = mLastLocation.getAccuracy();
            Log.v(LOG_TAG, "gps accuracy: "+ accuracy);
            Log.v(LOG_TAG, "gps: "+latitude+" "+longitude+": "+ accuracy);

            //workflow = (LaunchpadSectionFragment) mSectionsPagerAdapter.getItem(0);
            Fragment frag = getSupportFragmentManager().findFragmentByTag("android:switcher:"
                    + R.id.pager + ":" + mViewPager.getCurrentItem());

            // based on the current position, cast the page to the correct fragment
            if (mViewPager.getCurrentItem() == 0 && frag != null) {
                workflow = (LaunchpadSectionFragment) frag;
                        lblLocation = workflow.getLblLocation();

                // Call a method in the LaunchpadSectionFragment to update its content
                double accuracyImprecise = new BigDecimal(accuracy ).
                        setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                double latitudeImprecise = new BigDecimal(latitude ).
                        setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                double longitudeImprecise = new BigDecimal(longitude ).
                        setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                lblLocation.setText("Ac "+accuracyImprecise + "\n"
                        +"La "+latitudeImprecise +"\n"
                        +"Lo "+longitudeImprecise);

                //Log.v(LOG_TAG, String.valueOf(ratingView.getNumStars()));
                //Log.v(LOG_TAG, String.valueOf(ratingView.getRating()));
                ratingView = workflow.getRatingView();
                if(accuracy<11 && accuracy>0){
                    ratingView.setRating((float) 5);
                } else if(accuracy>=11&&accuracy<15){
                    ratingView.setRating((float) 4.5);
                } else if(accuracy>=15&&accuracy<30){
                    ratingView.setRating((float) 4);
                } else if(accuracy>=13&&accuracy<50){
                    ratingView.setRating((float) 3);
                } else{
                    ratingView.setRating((float) 1);
                }
                Log.v(LOG_TAG,"Location view is updated");
            } else{
                Log.v(LOG_TAG,"workflow LaunchpadSectionFragment is null");
            }
        } else {
           showToast("Couldn't get the location, make sure GPS is enabled");
        }
    }


    /**
     * Method to toggle periodic location updates
     * */
    private void togglePeriodicLocationUpdates(ImageView btnStartLocationUpdates) {
        if (!mRequestingLocationUpdates) {
            // Changing the button text
            //btnStartLocationUpdates
            //        .setText(getString(R.string.btn_stop_location_updates));

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();
            Picasso.with(this)
                    .load(R.drawable.marker_green)
                    .into(btnStartLocationUpdates);
            Log.d(LOG_TAG, "Periodic location updates started!");

        } else {
            // Changing the button text
            //btnStartLocationUpdates
            //        .setText(getString(R.string.btn_start_location_updates));

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();
            //btnStartLocationUpdates.setBackgroundColor(Color.BLACK);
            Picasso.with(this)
                    .load(R.drawable.marker)
                    .into(btnStartLocationUpdates);
            Log.d(LOG_TAG, "Periodic location updates stopped!");
        }
    }




    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }





    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 2 meters
    }

    /**
     * Starting the location updates periodically by sending a request to Location
     * Services
     * */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Shows a toast message.
     */
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
