package de.mpg.mpdl.www.datacollector.app.Workflow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.mpg.mpdl.www.datacollector.app.R;

/**
 * Created by allen on 08/04/15.
 */

/**
 * A fragment that launches other parts of the demo application.
 */
public class LaunchpadSectionFragment extends Fragment {
    public int TAKE_PHOTO_CODE = 0;
    public static int count=0;


    private static final int INTENT_TAKE_PHOTO = 1005;
    // The photo file where the camera stores the taken photo temporarily to
    // create an entry in "poiPhotos"
    private String photoFilePath;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/DataCollectorPicFolder/";
        File newDir = new File(dir);
        newDir.mkdirs();

        // Taking a Photo activity.
        rootView.findViewById(R.id.takePhoto)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // here,counter will be incremented each time,and the picture taken
                        // by camera will be stored as 1.jpg,2.jpg and likewise.
                        count++;
                        String file = dir+count+".jpg";
                        File newFile = new File(file);
                        try {
                            newFile.createNewFile();
                        } catch (IOException e) {}

                        Uri outputFileUri = Uri.fromFile(newFile);

                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
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
        rootView.findViewById(R.id.section_number)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), CollectionActivity.class);
                        startActivity(intent);
                    }
                });

        return rootView;
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

            // Create the storage directory if it does not exist
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                photoFilePath = null;
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.problem_create_file,
                    Toast.LENGTH_LONG).show();
        }
    }
}