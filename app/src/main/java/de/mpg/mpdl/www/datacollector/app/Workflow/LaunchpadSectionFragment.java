package de.mpg.mpdl.www.datacollector.app.Workflow;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.Retrofit.RetrofitClient;
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
public class LaunchpadSectionFragment extends Fragment {

    // Attributes for starting the intent and used by onActivityResult
    private static final int INTENT_ENABLE_GPS = 1000;
    private static final int INTENT_ENABLE_NET = 1001;
    private static final int INTENT_RECOVER_FROM_AUTH_ERROR = 1003;
    private static final int INTENT_RECOVER_FROM_PLAY_SERVICES_ERROR = 1004;
    private static final int INTENT_TAKE_PHOTO = 1005;
    private final String LOG_TAG = LaunchpadSectionFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";

    private String username = "shi@mpdl.mpg.de";
    private String password = "allen";

    TypedFile typedFile;
    String json;
    DataItem item;
    /*
     * After the intent to take a picture finishes we need to wait for
     * location information thereafter in order to save the data.
     */

    public Boolean takeAnotherPhoto;
    private String photoFilePath;
    DeviceStatus status;
    View rootView;
    private ImageView imageView;
    private RatingBar ratingView;
    private TextView lblLocation;
    private ImageView btnStartLocationUpdates;

    OnLocationUpdatedListener mCallback;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnLocationUpdatedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onLocationViewClicked(ImageView btnStartLocationUpdates);
    }

    Callback<String> callback = new Callback<String>() {
        @Override
        public void success(String item, Response response) {
            //adapter =  new CustomListAdapter(getActivity(), dataList);
            //listView.setAdapter(adapter);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getActivity(), "Upload data Successfully", duration);
            toast.show();
        }

        @Override
        public void failure(RetrofitError error) {
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
        Log.d(LOG_TAG, "onCreate");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");

        status = new DeviceStatus(getActivity());
        rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        ratingView = (RatingBar) rootView.findViewById(R.id.ratingBar);

        lblLocation = (TextView) rootView.findViewById(R.id.accuracy);
        btnStartLocationUpdates = (ImageView) rootView.findViewById(R.id.btnLocationUpdates);

        ratingView.setIsIndicator(true);
        // Taking a Photo activity.
        rootView.findViewById(R.id.takePhoto)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takePhoto();
                    }
                });


        // Demonstration of navigating to external activities.
        rootView.findViewById(R.id.demo_external_activity)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Create an intent that asks the user to pick a photo, but using
                        // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                        // the application from the device home screen does not return
                        // to the external activity.
                        Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
                        externalActivityIntent.setType("image/*");
                        externalActivityIntent.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivity(externalActivityIntent);
                    }
                });

        // Demonstration of a collection-browsing activity.
        rootView.findViewById(R.id.send)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Intent intent = new Intent(getActivity(), CollectionActivity.class);
                        //startActivity(intent);
                        upload();
                    }
                });

//        rootView.findViewById(R.id.section_number)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(getActivity(), CollectionActivity.class);
//                        startActivity(intent);
//                    }
//                });


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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_ENABLE_GPS) {
            if (!status.isGPSEnabled()) {
                Toast.makeText(getActivity(), R.string.problem_no_gps,
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        } else if (requestCode == INTENT_ENABLE_NET) {
            if (!status.isNetworkEnabled()) {
                Toast.makeText(getActivity(), R.string.problem_no_net,
                        Toast.LENGTH_SHORT).show();
            }
        } else if ((requestCode == INTENT_RECOVER_FROM_AUTH_ERROR || requestCode == INTENT_RECOVER_FROM_PLAY_SERVICES_ERROR)
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
                    System.out.println(photoFilePath);

                }

                addImageToGallery(photoFilePath);
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the photo capture
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // set GPS settings
//    public void onEventMainThread() {
//        // User touched the dialog's positive button
//        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivityForResult(intent, INTENT_ENABLE_GPS);
//    }

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

            //Toast.makeText(getActivity(), photoFilePath, Toast.LENGTH_LONG).show();
            System.out.println(photoFilePath);
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

    private void upload(){
        typedFile = new TypedFile("multipart/form-data", new File(photoFilePath));
        json = "{ \"collectionId\" : \"gapMbfdN2i6hAtMf\"}";

        RetrofitClient.uploadItem(typedFile, json, callback, username, password);
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
}