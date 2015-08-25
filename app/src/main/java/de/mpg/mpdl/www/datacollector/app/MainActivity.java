package de.mpg.mpdl.www.datacollector.app;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.FlatButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.skd.androidrecording.audio.AudioPlaybackManager;
import com.skd.androidrecording.audio.AudioRecordingThread;
import com.skd.androidrecording.video.PlaybackHandler;
import com.squareup.otto.Produce;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.Locale;

import de.mpg.mpdl.www.datacollector.app.Event.LocationChangedEvent;
import de.mpg.mpdl.www.datacollector.app.Event.OttoSingleton;
import de.mpg.mpdl.www.datacollector.app.ItemList.ItemListFragment;
import de.mpg.mpdl.www.datacollector.app.POI.POIFragment;
import de.mpg.mpdl.www.datacollector.app.Workflow.MetadataFragment;
import de.mpg.mpdl.www.datacollector.app.Workflow.WorkflowSectionFragment;
import de.mpg.mpdl.www.datacollector.app.utils.DeviceStatus;


//public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        WorkflowSectionFragment.OnLocationUpdatedListener,
        MetadataFragment.OnFragmentInteractionListener {

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    //new ui
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    Boolean wouldShow = false;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    NavigationView navigation;
    EditText usernameView;
    EditText passwordView;
    String username;
    String password;
    SharedPreferences mPrefs;

    private View rootView;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    WorkflowSectionFragment workflow;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;


    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 2000; // Update location every 5 sec
    private static int FATEST_INTERVAL = 1000; // 2 sec
    private static int DISPLACEMENT = 0; // 2 meters
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    private AudioRecordingThread recordingThread;

    private PlaybackHandler playbackHandler;
    private AudioPlaybackManager playbackManager;
    private TextView lblLocation;
    private RatingBar ratingView;

    public FloatingActionButton bottomCenterButton;
    public SubActionButton subActionButtonCamera;
    public SubActionButton subActionButtonPic;
    public SubActionButton subActionButtonAudio;
    public SubActionButton subActionButtonVideo;
    public SubActionButton subActionButtonText;
    public SubActionButton subActionButtonGPS;
    public SubActionButton subActionButtonSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActiveAndroid.initialize(this);


        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (code != ConnectionResult.SUCCESS) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(code, this, 1);
            dialog.show();
        }

        setContentView(R.layout.activity_main);
        initInstances();
        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        // For Location
        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
        int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
        int redActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
        int redActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin);
        int redActionMenuRadius = getResources().getDimensionPixelSize(R.dimen.red_action_menu_radius);
        int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new));

        FloatingActionButton.LayoutParams starParams = new FloatingActionButton.
                LayoutParams(redActionButtonSize, redActionButtonSize);
        starParams.setMargins(redActionButtonMargin,
                redActionButtonMargin,
                redActionButtonMargin,
                redActionButtonMargin);
        fabIconNew.setLayoutParams(starParams);


        FloatingActionButton.LayoutParams fabIconStarParams = new FloatingActionButton.
                LayoutParams(redActionButtonContentSize, redActionButtonContentSize);
        fabIconStarParams.setMargins(redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin);

        bottomCenterButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew, fabIconStarParams)
                .setBackgroundDrawable(R.drawable.button_action_red_selector)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER)
                .setLayoutParams(starParams)
                .build();

        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder lCSubBuilder = new SubActionButton.Builder(this);
        lCSubBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue_selector));

        FrameLayout.LayoutParams blueContentParams = new FrameLayout.
                LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        blueContentParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);
        lCSubBuilder.setLayoutParams(blueContentParams);
        // Set custom layout params
        FrameLayout.LayoutParams blueParams = new FrameLayout.
                LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        lCSubBuilder.setLayoutParams(blueParams);

        ImageView lcIconCamera = new ImageView(this);
        ImageView lcIconPic = new ImageView(this);
        ImageView lcIconVideo = new ImageView(this);
        ImageView lcIconGPS = new ImageView(this);
        ImageView lcIconAudio = new ImageView(this);
        ImageView lcIconSave = new ImageView(this);
        ImageView lcIconText = new ImageView(this);

        lcIconCamera.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera));
        lcIconPic.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_picture));
        lcIconVideo.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video));
        lcIconGPS.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_location_found));
        lcIconAudio.setImageDrawable(getResources().getDrawable(R.drawable.mic_white));
        lcIconSave.setImageDrawable(getResources().getDrawable(R.drawable.save_white));
        //lcIconText.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_chat));

        subActionButtonCamera = lCSubBuilder.setContentView(lcIconCamera, blueContentParams).build();
        subActionButtonPic = lCSubBuilder.setContentView(lcIconPic, blueContentParams).build();
        subActionButtonAudio = lCSubBuilder.setContentView(lcIconAudio, blueContentParams).build();
        subActionButtonVideo = lCSubBuilder.setContentView(lcIconVideo, blueContentParams).build();
        //subActionButtonText = lCSubBuilder.setContentView(lcIconText, blueContentParams).build();
        subActionButtonGPS = lCSubBuilder.setContentView(lcIconGPS, blueContentParams).build();
        subActionButtonSave = lCSubBuilder.setContentView(lcIconSave, blueContentParams).build();

        // Build another menu with custom options
        final FloatingActionMenu bottomCenterMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(subActionButtonCamera)
                .addSubActionView(subActionButtonPic)
                .addSubActionView(subActionButtonAudio)
                        //.addSubActionView(subActionButtonGPS)
                .addSubActionView(subActionButtonVideo)
                        //.addSubActionView(subActionButtonText)
                .addSubActionView(subActionButtonSave)
                .setRadius(redActionMenuRadius)
                .setStartAngle(-180)
                .setEndAngle(0)
                .attachTo(bottomCenterButton)
                .build();


        // Listen menu open and close events to animate the button content view
        bottomCenterMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });
    }


    private void initInstances() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
        //tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
        //tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(mSectionsPagerAdapter);
        //viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        if (tab.getPosition() != 0) {
                            hideButtons();
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        if (tab.getPosition() != 0) {
                            showButtons();
                        }
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);

                    }
                });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world) {
//            @Override
//            public void onDrawerClosed(View view) {
//                invalidateOptionsMenu();
//                if(wouldShow){
//                    showButtons();
//                    wouldShow = false;
//                }
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                invalidateOptionsMenu();
//                if(bottomCenterButton.getVisibility() == View.VISIBLE){
//                    wouldShow = true;
//                    hideButtons();
//                }
//            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset == 0) {
                    // drawer closed
                    if(wouldShow){
                        showButtons();
                        wouldShow = false;
                    }
                    invalidateOptionsMenu();
                } else {
                    // started opening
                    if(bottomCenterButton.getVisibility() == View.VISIBLE){
                        wouldShow = true;
                        hideButtons();
                    }
                    invalidateOptionsMenu();
                }
                super.onDrawerSlide(drawerView, slideOffset);
            }

        };

        drawerLayout.setDrawerListener(drawerToggle);
        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        navigation = (NavigationView) findViewById(R.id.navigation);
        usernameView  = (EditText) navigation.findViewById(R.id.username);
        passwordView  = (EditText) navigation.findViewById(R.id.password);

        mPrefs = this.getSharedPreferences("myPref", 0);
        usernameView.setText(mPrefs.getString("username", ""));
        passwordView.setText(mPrefs.getString("password", ""));

        FlatButton saveButton = (FlatButton) navigation.findViewById(R.id.confirm_save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean cancel = false;
                View focusView = null;

                username = usernameView.getText().toString();
                password = passwordView.getText().toString();

                usernameView.setError(null);
                passwordView.setError(null);

                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(username)) {
                    usernameView.setError(getString(R.string.error_field_required));
                    focusView = usernameView;
                    cancel = true;
                }else if (TextUtils.isEmpty(password)) {
                    passwordView.setError(getString(R.string.error_field_required));
                    focusView = passwordView;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error, focus the first form field with an error.
                    focusView.requestFocus();
                } else {
                    usernameView.setEnabled(false);
                    passwordView.setEnabled(false);

                    mPrefs = getSharedPreferences("myPref", 0);
                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putString("username", username).apply();
                    mEditor.putString("password", password).apply();
                    DeviceStatus.showSnackbar(rootView, "Saved Successfully");
                    drawerLayout.closeDrawers();
                }


            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {

            mGoogleApiClient.connect();
        }
//        Log.e(LOG_TAG, "start onStart~~~");
    }

//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
//        boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
//        double myDouble = savedInstanceState.getDouble("myDouble");
//        int myInt = savedInstanceState.getInt("MyInt");
//        String myString = savedInstanceState.getString("MyString");
//    }

    // recover the last status
    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.e(LOG_TAG, "start onRestart~~~");
    }

    @Override
    protected void onResume() {
        super.onResume();

        //checkPlayServices();
        if (mGoogleApiClient != null) {
            // Resuming the periodic location updates
            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }
//        if (isFirstTime()) {
//            // What you do when the Application is Opened First time Goes here
//            DeviceStatus.showSnackbar(rootView, "first blood");
//        }
//        Log.e(LOG_TAG, "start onResume~~~");
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        // Save UI state changes to the savedInstanceState.
//        // This bundle will be passed to onCreate if the process is
//        // killed and restarted.
//        savedInstanceState.putBoolean("MyBoolean", true);
//        savedInstanceState.putDouble("myDouble", 1.9);
//        savedInstanceState.putInt("MyInt", 1);
//        savedInstanceState.putString("MyString", "Welcome back to Android");
//        // etc.
//    }

    // save the current status
    @Override
    protected void onPause() {
        super.onPause();
        //onSaveInstanceState();
        if (mGoogleApiClient != null) {
            if (mRequestingLocationUpdates && mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
            }
        }

//        Log.e(LOG_TAG, "start onPause~~~");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
//        Log.e(LOG_TAG, "start onStop~~~");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //new Delete().from(DataItem.class).execute(); // all records
        //new Delete().from(MetaDataLocal.class).execute(); // all records

        //new Delete().from(DataItem.class).where("isLocal = ?", 1).execute();
//        Log.e(LOG_TAG, "start onDestroy~~~");
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
        }
//          else if (id == R.id.action_map){
//            openPreferredLocationInMap();
//        }
        return super.onOptionsItemSelected(item);
    }


    private void openPreferredLocationInMap() {
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
            Toast toast = Toast.makeText(this, "Couldn't call " + location +
                    ", no receiving apps installed!", duration);
            toast.show();
//            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
            //Log.v(LOG_TAG, position + "");

            switch (position) {
                case 0:
                    // The first section of the app
                    fragment = new WorkflowSectionFragment();
                    args.putInt(WorkflowSectionFragment.ARG_SECTION_NUMBER, position);
                    fragment.setArguments(args);
                    return fragment;

                case 1:
                    fragment = new ItemListFragment();
                    args.putInt(ItemListFragment.ARG_SECTION_NUMBER, position);
                    fragment.setArguments(args);
                    return fragment;

                case 2:
                    // The other sections of the app are dummy placeholders.
                    fragment = new POIFragment();
                    args.putInt(POIFragment.ARG_SECTION_NUMBER, position);
                    fragment.setArguments(args);
                    return fragment;
                default:
                    // getItem is called to instantiate the fragment for the given page.
                    // Return a PlaceholderFragment (defined as a static inner class below).
                    return new WorkflowSectionFragment();
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
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
//                case 3:
//                    return "Collection";
            }
            return null;
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
        //togglePeriodicLocationUpdates(btnStartLocationUpdates);

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
    @Produce
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;
        displayLocation();
        LocationChangedEvent event = new LocationChangedEvent(location);
        OttoSingleton.getInstance().post(event);
    }

    //the method for LaunchpadSectionFragment.OnLocationUpdatedListener
    @Override
    public void onLocationViewClicked(ImageView btnStartLocationUpdates) {
        togglePeriodicLocationUpdates(btnStartLocationUpdates);
    }

    @Override
    public void replaceFragment(MetadataFragment fragment) {
        android.support.v4.app.FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.viewPager, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Method to display the location on UI
     */
    private void displayLocation() {
        double accuracy = 0.0;
        double longitude = 0.0;
        double latitude = 0.0;
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            accuracy = mLastLocation.getAccuracy();

            Fragment frag = getSupportFragmentManager().findFragmentByTag("android:switcher:"
                    + R.id.viewPager + ":" + viewPager.getCurrentItem());

            //TODO mViewPager
            // based on the current position, cast the page to the correct fragment
            if (viewPager.getCurrentItem() == 0 && frag != null) {
                workflow = (WorkflowSectionFragment) frag;
                lblLocation = workflow.getLblLocation();

                // Call a method in the LaunchpadSectionFragment to update its content
                double accuracyImprecise = new BigDecimal(accuracy).
                        setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                double latitudeImprecise = new BigDecimal(latitude).
                        setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                double longitudeImprecise = new BigDecimal(longitude).
                        setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                lblLocation.setText("Ac " + accuracyImprecise + "\n"
                        + "La " + latitudeImprecise + "\n"
                        + "Lo " + longitudeImprecise);

                //Log.v(LOG_TAG, String.valueOf(ratingView.getNumStars()));
                //Log.v(LOG_TAG, String.valueOf(ratingView.getRating()));
                ratingView = workflow.getRatingView();
                if (accuracy < 11 && accuracy > 0) {
                    ratingView.setRating((float) 5);
                } else if (accuracy >= 11 && accuracy < 15) {
                    ratingView.setRating((float) 4.5);
                } else if (accuracy >= 15 && accuracy < 30) {
                    ratingView.setRating((float) 4);
                } else if (accuracy >= 13 && accuracy < 50) {
                    ratingView.setRating((float) 3);
                } else {
                    ratingView.setRating((float) 1);
                }
//                Log.v(LOG_TAG,"Location view is updated");

            } else {
//                workflow = (WorkflowSectionFragment) frag;
//                VisualizerView voiceView = workflow.getVisualizerView();
//                AudioPlaybackManager audioPlaybackManager = workflow.getPlaybackManager();
//                voiceView.setVisibility(View.INVISIBLE);
//                audioPlaybackManager.hideMediaController();
//                Log.v(LOG_TAG,"workflow LaunchpadSectionFragment is null");
            }
        } else {
            DeviceStatus.showSnackbar(rootView, "Couldn't get the location, make sure GPS is enabled");
        }
    }


    /**
     * Method to toggle periodic location updates
     */
    private void togglePeriodicLocationUpdates(ImageView btnStartLocationUpdates) {
        if (mGoogleApiClient.isConnected()) {
            if (!mRequestingLocationUpdates) {

                mRequestingLocationUpdates = true;

                // Starting the location updates
                startLocationUpdates();
                Picasso.with(this)
                        .load(R.drawable.marker_green)
                        .into(btnStartLocationUpdates);
                Log.d(LOG_TAG, "Periodic location updates started!");

            } else {
                mRequestingLocationUpdates = false;

                // Stopping the location updates
                stopLocationUpdates();
                //btnStartLocationUpdates.setBackgroundColor(Color.BLACK);
                Picasso.with(this)
                        .load(R.drawable.marker)
                        .into(btnStartLocationUpdates);
                Log.d(LOG_TAG, "Periodic location updates stopped!");
            }
        } else {
            DeviceStatus.showSnackbar(rootView, "Can not connect the Google Location Service");
            mGoogleApiClient.reconnect();
        }
    }


    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     */
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
     */
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
     */
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


    /***
     * Checks that application runs first time and write flag at SharedPreferences
     *
     * @return true if 1st time
     */
    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();
        }
        return !ranBefore;
    }


    private void hideButtons() {
        bottomCenterButton.setVisibility(View.INVISIBLE);
        subActionButtonCamera.setVisibility(View.INVISIBLE);
        subActionButtonPic.setVisibility(View.INVISIBLE);
        subActionButtonAudio.setVisibility(View.INVISIBLE);
        subActionButtonVideo.setVisibility(View.INVISIBLE);
        //subActionButtonText.setVisibility(View.INVISIBLE);
        subActionButtonGPS.setVisibility(View.INVISIBLE);
        subActionButtonSave.setVisibility(View.INVISIBLE);
    }

    private void showButtons() {
        bottomCenterButton.setVisibility(View.VISIBLE);
        subActionButtonCamera.setVisibility(View.VISIBLE);
        subActionButtonPic.setVisibility(View.VISIBLE);
        subActionButtonAudio.setVisibility(View.VISIBLE);
        subActionButtonVideo.setVisibility(View.VISIBLE);
        //subActionButtonText.setVisibility(View.VISIBLE);
        subActionButtonGPS.setVisibility(View.VISIBLE);
        subActionButtonSave.setVisibility(View.VISIBLE);
    }

}
