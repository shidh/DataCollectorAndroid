package de.mpg.mpdl.www.datacollector.app.Workflow;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.mpg.mpdl.www.datacollector.app.AsyncTask.GetAddressByCoordinatesTask;
import de.mpg.mpdl.www.datacollector.app.Event.GetAddressEvent;
import de.mpg.mpdl.www.datacollector.app.Event.LocationChangedEvent;
import de.mpg.mpdl.www.datacollector.app.Event.MetadataIsReadyEvent;
import de.mpg.mpdl.www.datacollector.app.Event.OttoSingleton;
import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.Model.MetaDataLocal;
import de.mpg.mpdl.www.datacollector.app.Model.User;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.utils.DeviceStatus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by allen on 08/04/15.
 */


/**
 * A fragment that launches other parts of the demo application.
 */
public class WorkflowSectionFragment extends Fragment {

    // Attributes for starting the intent and used by onActivityResult
    private static final int INTENT_ENABLE_GPS = 1000;
    private static final int INTENT_ENABLE_NET = 1001;
    private static final int INTENT_RECOVER_FROM_AUTH_ERROR = 1003;
    private static final int INTENT_RECOVER_FROM_PLAY_SERVICES_ERROR = 1004;
    private static final int INTENT_TAKE_PHOTO = 1005;
    private static final int INTENT_PICK_PHOTO = 1006;

    private final String LOG_TAG = WorkflowSectionFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";

    private TypedFile typedFile;
    private String json;
    private List<DataItem> itemList = new ArrayList<DataItem>();
    private DataItem item = new DataItem();
    private MetaDataLocal meta = new MetaDataLocal();
    private Location currentLocation;

    /*
     * After the intent to take a picture finishes we need to wait for
     * location information thereafter in order to save the data.
     */

    public Boolean takeAnotherPhoto;
    private String photoFilePath;
    private String fileName;
    private DeviceStatus status;
    private View rootView;
    private ImageView imageView;
    private RatingBar ratingView;
    private TextView lblLocation;
    private ImageView btnStartLocationUpdates;
    private User user;
    private MenuItem poi_list;

    private OnLocationUpdatedListener mCallback;


    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnLocationUpdatedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onLocationViewClicked(ImageView btnStartLocationUpdates);

        public void replaceFragment(MetadataFragment fragment);
    }

    Callback<DataItem> callback = new Callback<DataItem>() {
        @Override
        public void success(DataItem dataItem, Response response) {
            //adapter =  new CustomListAdapter(getActivity(), dataList);
            //listView.setAdapter(adapter);
            showToast( "Upload data Successfully");
            Log.v(LOG_TAG, dataItem.getCollectionId());
            Log.v(LOG_TAG, String.valueOf(dataItem.getMetadata()));

        }

        @Override
        public void failure(RetrofitError error) {
            showToast( "Upload data Failed");
            Log.v(LOG_TAG, error.toString());

        }
    };


    public RatingBar getRatingView() {
        return ratingView;
    }

    public void setRatingView(RatingBar ratingView) {
        this.ratingView = ratingView;
    }


    public TextView getLblLocation() {
        return lblLocation;
    }

    public ImageView getBtnStartLocationUpdates() {
        return btnStartLocationUpdates;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_TAG, "onAttach");

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnLocationUpdatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLocationUpdatedListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
//        boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
//        double myDouble = savedInstanceState.getDouble("myDouble");
//        int myInt = savedInstanceState.getInt("MyInt");
//        String myString = savedInstanceState.getString("MyString");
        user = new User();

        setHasOptionsMenu(true);
        Log.d(LOG_TAG, "onCreate");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");

        rootView = inflater.inflate(R.layout.fragment_section_workflow, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        ratingView = (RatingBar) rootView.findViewById(R.id.ratingBar);
        ratingView.setIsIndicator(true);
        lblLocation = (TextView) rootView.findViewById(R.id.accuracy);
        btnStartLocationUpdates = (ImageView) rootView.findViewById(R.id.btnLocationUpdates);

        //TODO
        //Make the takePhoto button and Gallery Button to one
        //Ask user to chose when clicked.

        // Taking a Photo activity.
        rootView.findViewById(R.id.takePhoto)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takePhoto();
                    }
                });


        // Open Photo Lib by navigating to external activities.
        rootView.findViewById(R.id.gallery_activity)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Create an intent that asks the user to pick a photo, but using
                        // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                        // the application from the device home screen does not return
                        // to the external activity.

                        Intent gallery = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        gallery.setType("image/*");
                        gallery.setAction(Intent.ACTION_GET_CONTENT);
                        //gallery.addFlags(
                        //        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivityForResult(gallery, INTENT_PICK_PHOTO);
                    }
                });

        // Save the data in the local storage.
        rootView.findViewById(R.id.save)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Intent intent = new Intent(getActivity(), CollectionActivity.class);
                        //startActivity(intent);


                        //MetadataFragment frag=new MetadataFragment();
                        //mCallback.replaceFragment(frag);

//                       Fragment frag = getActivity().getSupportFragmentManager().
//                                findFragmentByTag("android:switcher:"
//                                + R.id.pager + ":" + getActivity().mViewPager.getCurrentItem());


                        //upload();
                        if(currentLocation != null){
                            meta.setAccuracy(currentLocation.getAccuracy());
                            meta.setLatitude(currentLocation.getLatitude());
                            meta.setLongitude(currentLocation.getLongitude());
                            //meta.setAddress(getAddressByCoordinates(currentLocation.getLatitude(),
                            //        currentLocation.getLongitude()));

                            AskMetadataFragment newFragment = new AskMetadataFragment();
                            newFragment.show(getActivity().getSupportFragmentManager(), "askMetadata");
                        } else{
                            //TODO
                            //make a dialog to ask user open gps or save photo without geo information
                            showToast("Please open the GPS by clicking the marker on top left");
                        }
                    }
                });

        //Don't show the save button before user taken a photo
        rootView.findViewById(R.id.save).setVisibility(View.GONE);


        rootView.findViewById(R.id.btnLocationUpdates)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onLocationViewClicked(btnStartLocationUpdates);
                    }
                });

        return rootView;
    }

      @Override
      public void onActivityCreated(Bundle savedInstanceState) {
          super.onActivityCreated(savedInstanceState);
          Log.d(LOG_TAG, "onActivityCreated");
      }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }



    @Override
    public void onResume() {
        super.onResume();
        OttoSingleton.getInstance().register(this);
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.v(LOG_TAG, "onSaveInstanceState");
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean("MyBoolean", true);
        savedInstanceState.putDouble("myDouble", 1.9);
        savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("MyString", "Welcome back to Android");
    }



    @Override
    public void onPause() {
        super.onPause();
        OttoSingleton.getInstance().unregister(this);
        Log.d(LOG_TAG, "onPasue");

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "start onDestroy~~~");
    }

    // for the POI_list icon in menu_launchpad
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_launchpad, menu);
        poi_list = menu.findItem(R.id.POI_list);

        if(new Select()
                .from(DataItem.class)
                .where("isLocal = ?", 1)
                .execute().size() >0) {
            poi_list.setIcon(getResources().getDrawable(R.drawable.action_uploadlist_blue));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = menuItem.getItemId();
        if (id == R.id.POI_list) {
            //updateWeather();
            //updateDataItem();
            showToast("hi from list icon");
            Intent intent = new Intent(getActivity(), ReadyToUploadCollectionActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_ENABLE_GPS) {
            if (!DeviceStatus.isGPSEnabled(getActivity())) {
                Toast.makeText(getActivity(), R.string.problem_no_gps,
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        } else if (requestCode == INTENT_ENABLE_NET) {
            if (!DeviceStatus.isNetworkEnabled(getActivity())) {
                Toast.makeText(getActivity(), R.string.problem_no_net,
                        Toast.LENGTH_SHORT).show();
            }
        } else if ((requestCode == INTENT_RECOVER_FROM_AUTH_ERROR ||
                requestCode == INTENT_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == getActivity().RESULT_OK) {
			/*
			 * Receiving a result that follows a GoogleAuthException, try auth
			 * again
			 */
            //getUsername();
        } else if (requestCode == INTENT_TAKE_PHOTO) {
            if (resultCode == getActivity().RESULT_OK) {
                takeAnotherPhoto = true;

                File imgFile = new File(photoFilePath);
                if(imgFile.exists()){
                    Picasso.with(getActivity())
                            .load(imgFile)
                            .resize(imageView.getWidth(), imageView.getHeight())
                            .into(imageView);
                }

                addImageToGallery(photoFilePath);
                rootView.findViewById(R.id.save).setVisibility(View.VISIBLE);
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the photo capture
            }
        } else if (requestCode == INTENT_PICK_PHOTO){
            if (resultCode == getActivity().RESULT_OK) {
                Uri imageUri = data.getData();
                Picasso.with(getActivity())
                        .load(imageUri)
                        .resize(imageView.getWidth(), imageView.getHeight())
                        .into(imageView);
                rootView.findViewById(R.id.save).setVisibility(View.VISIBLE);

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the photo picking
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Subscribe
    public void onGetMetadataFromUser(MetadataIsReadyEvent event) {
        user.setCompleteName("Allen");
        user.save();
        meta.setTags(event.tags);
        Log.v(LOG_TAG, event.tags.get(0));
        meta.setTitle(meta.getTags().get(0)+"@"+meta.getAddress());

        meta.setCreator(user.getCompleteName());
        //add a dataItem to the list on the top of view
        item.setCollectionId("Qwms6Gs040FBS264");
        item.setLocalPath(photoFilePath);
        item.setMetaDataLocal(meta);
        item.setLocal(1);
        item.setCreatedBy(user);
        item.setFilename(fileName);

        Log.v(LOG_TAG, item.getMetaDataLocal().getTitle());

        meta.save();
        item.save();
        Log.v(LOG_TAG, item.getFilename());
        itemList.add(item);

        //change the icon of the view
        poi_list.setIcon(getResources().getDrawable(R.drawable.action_uploadlist_blue));
    }

    @Subscribe
    public void onGetNewLocationFromGPS(LocationChangedEvent event){
        currentLocation = event.location;

        getAddressByCoordinates(event.location.getLatitude(), event.location.getLongitude());
    }


    // Take a photo using an intent
    private void takePhoto() {

        // Create an intent to take a picture
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Start the image capture Intent
        if (takePhotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            // Create a file to save the photo
            createPhotoFile();
            // Continue only if the file was successfully created
            if (photoFilePath != null) {
                // Set the image file name
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(photoFilePath)));
                startActivityForResult(takePhotoIntent, INTENT_TAKE_PHOTO);
            }

        }
    }

    // Create a file for saving the photo
    private void createPhotoFile() {

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            String photoFileName = getString(R.string.app_name) + "_"
                    + timeStamp;
            File storageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    getString(R.string.app_name));
            photoFilePath = new File(storageDir.getPath(), photoFileName
                    + ".jpg").getPath();
            fileName = photoFileName + ".jpg";

            //Toast.makeText(getActivity(), photoFilePath, Toast.LENGTH_LONG).show();
            Log.v(LOG_TAG, photoFilePath);
            // Create the storage directory if it does not exist
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                photoFilePath = null;
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.problem_create_file,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void addImageToGallery(String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

//    private void upload(){
//        typedFile = new TypedFile("multipart/form-data", new File(photoFilePath));
//        json = "{ \"collectionId\" : \"Qwms6Gs040FBS264\"}";
//
//        item = new DataItem();
//        RetrofitClient.uploadItem(typedFile, json, callback, username, password);
//    }


    private MetaDataLocal setCollectMetaData(){
        MetaDataLocal meta = new MetaDataLocal();
        typedFile = new TypedFile("multipart/form-data", new File(photoFilePath));
        json = "{ \"collectionId\" : \"Qwms6Gs040FBS264\"}";

        //TODO   deal with POI id
        //meta.setDeviceID();
        return meta;
    }

    public String encodeBae64(String src){
        // Sending side
        byte[] data = null;
        try {
            data = src.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return base64;
    }

    /**
     * Shows a toast message.
     */
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void getAddressByCoordinates(double latitude, double longitude){
        GetAddressByCoordinatesTask fetchTask = new GetAddressByCoordinatesTask();
        fetchTask.execute(latitude, longitude);
    }

    @Subscribe
    public void OnGetAddressEvent(GetAddressEvent event){
        String address = " ";
        if(event != null){
            address = event.address;
        }
        meta.setAddress(address);
    }
}