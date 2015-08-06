package de.mpg.mpdl.www.datacollector.app.Workflow;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.skd.androidrecording.audio.AudioPlaybackManager;
import com.skd.androidrecording.audio.AudioRecordingHandler;
import com.skd.androidrecording.audio.AudioRecordingThread;
import com.skd.androidrecording.video.PlaybackHandler;
import com.skd.androidrecording.visualizer.VisualizerView;
import com.skd.androidrecording.visualizer.renderer.BarGraphRenderer;
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
import de.mpg.mpdl.www.datacollector.app.MainActivity;
import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.Model.MetaDataLocal;
import de.mpg.mpdl.www.datacollector.app.Model.User;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.Workflow.UploadView.ReadyToUploadCollectionActivity;
import de.mpg.mpdl.www.datacollector.app.utils.DeviceStatus;
import de.mpg.mpdl.www.datacollector.app.utils.StorageUtils;
import retrofit.mime.TypedFile;

/**
 * Created by allen on 08/04/15.
 */


/**
 * A fragment that launches other parts of the demo application.
 */
public class WorkflowSectionFragment extends Fragment{

    // Attributes for starting the intent and used by onActivityResult
    private static final int INTENT_ENABLE_GPS = 1000;
    private static final int INTENT_ENABLE_NET = 1001;
    private static final int INTENT_RECOVER_FROM_AUTH_ERROR = 1003;
    private static final int INTENT_RECOVER_FROM_PLAY_SERVICES_ERROR = 1004;
    private static final int INTENT_TAKE_PHOTO = 1005;
    private static final int INTENT_PICK_PHOTO = 1006;
    private static final int INTENT_PICK_VIDEO = 1007;
    private static final int INTENT_PICK_AUDIO = 1007;
    private static final int INTENT_PICK_DATA = 1008;


    private final String LOG_TAG = WorkflowSectionFragment.class.getSimpleName();
    public static final String ARG_SECTION_NUMBER = "section_number";
    private String collectionID = DeviceStatus.collectionID;

    private TypedFile typedFile;
    private String json;
    private List<DataItem> itemList = new ArrayList<DataItem>();
    private DataItem item = new DataItem();
    private MetaDataLocal meta = new MetaDataLocal();
    private Location currentLocation;
    private static Gson gson = new GsonBuilder()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    /*
     * After the intent to take a picture finishes we need to wait for
     * location information thereafter in order to save the data.
     */

    private String filePath;
    private String fileName;
    //private String audioFileName;
    private DeviceStatus status;
    private View rootView;
    private ImageView imageView;
    private RatingBar ratingView;
    private TextView lblLocation;
    private ImageView btnStartLocationUpdates;
    private VisualizerView visualizerView;
    private AudioRecordingThread recordingThread;
    private boolean startRecording = true;
    private boolean readyToPlay = false;

    //private Button recordBtn, playBtn;


    private User user;
    private MenuItem poi_list;
    private FloatingActionButton bottomCenterButton;

    private OnLocationUpdatedListener mCallback;

    private AudioPlaybackManager playbackManager;

    private PlaybackHandler playbackHandler = new PlaybackHandler() {
        @Override
        public void onPreparePlayback() {
//            ((MainActivity)getActivity()).runOnUiThread(new Runnable() {
//                public void run() {
                    playbackManager.showMediaController();
//                }
//            });
        }
    };


    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnLocationUpdatedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onLocationViewClicked(ImageView btnStartLocationUpdates);

        public void replaceFragment(MetadataFragment fragment);


    }


    public RatingBar getRatingView() {
        return ratingView;
    }

    public void setRatingView(RatingBar ratingView) {
        this.ratingView = ratingView;
    }


    public TextView getLblLocation() {
        return lblLocation;
    }

    public VisualizerView getVisualizerView() {
        return visualizerView;
    }

    public void setVisualizerView(VisualizerView visualizerView) {
        this.visualizerView = visualizerView;
    }

    public AudioPlaybackManager getPlaybackManager() {
        return playbackManager;
    }

    public void setPlaybackManager(AudioPlaybackManager playbackManager) {
        this.playbackManager = playbackManager;
    }

    public FloatingActionButton getBottomCenterButton(){
        return bottomCenterButton;
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

        user = new User();
        user.setCompleteName("Allen");
        user.save();
        setHasOptionsMenu(true);
        Log.d(LOG_TAG, "onCreate");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            filePath = savedInstanceState.getString("photoFilePath");
        }

        rootView = inflater.inflate(R.layout.fragment_section_workflow, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        ratingView = (RatingBar) rootView.findViewById(R.id.ratingBar);
        ratingView.setIsIndicator(true);
        Log.v(LOG_TAG, ratingView.getNumStars() + "");
        ratingView.setNumStars(5);
        Log.v(LOG_TAG, ratingView.getNumStars() + "");

        //LayerDrawable stars = (LayerDrawable) ratingView.getProgressDrawable();
        //stars.getDrawable(2).setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);

        lblLocation = (TextView) rootView.findViewById(R.id.accuracy);
        btnStartLocationUpdates = (ImageView) rootView.findViewById(R.id.btnLocationUpdates);


        ((MainActivity)getActivity()).subActionButtonCamera.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //rootView.findViewById(R.id.save).setVisibility(View.VISIBLE);
                        meta.setType("image");
                        takePhoto();

                    }
                });



        ((MainActivity)getActivity()).subActionButtonPic.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent gallery = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        gallery.setType("image/*");
                        gallery.setAction(Intent.ACTION_GET_CONTENT);
                        meta.setType("image");
                        startActivityForResult(gallery, INTENT_PICK_PHOTO);

                    }
                });

        //TODO place a thumbnail for video
        ((MainActivity)getActivity()).subActionButtonVideo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent gallery = new Intent(Intent.ACTION_PICK,
                                MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                        gallery.setType("video/*");
                        gallery.setAction(Intent.ACTION_GET_CONTENT);
                        //gallery.addFlags(
                        //        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivityForResult(gallery, INTENT_PICK_VIDEO);
                    }
                });

        //TODO place a sound wave thumbnail for audio
        ((MainActivity)getActivity()).subActionButtonAudio.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent gallery = new Intent(Intent.ACTION_PICK,
//                                MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
//                        gallery.setType("audio/*");
//                        gallery.setAction(Intent.ACTION_GET_CONTENT);
//                        //gallery.addFlags(
//                        //        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//                        startActivityForResult(gallery, INTENT_PICK_AUDIO);
                        imageView.setVisibility(View.INVISIBLE);
                        //playBtn.setVisibility(View.VISIBLE);
                        record();
                        meta.setType("audio");

                    }
                });

        //onSave()
        ((MainActivity)getActivity()).subActionButtonSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (meta != null && item != null) {
                            if (currentLocation != null) {
                                //showToast("GPS is off");
                            }

                            if(currentLocation !=null) {
                                meta.setAccuracy(currentLocation.getAccuracy());
                                meta.setLatitude(currentLocation.getLatitude());
                                meta.setLongitude(currentLocation.getLongitude());
//                                meta.setAddress(getAddressByCoordinates(currentLocation.getLatitude(),
//                                        currentLocation.getLongitude()));
                            }
                            //remove the tags fragment
                            //AskMetadataFragment newFragment = new AskMetadataFragment();
                            //newFragment.show(getActivity().getSupportFragmentManager(), "askMetadata");

                            item.setFilename(fileName);
                            meta.setTags(null);
                            //meta.setTitle(meta.getTags().get(0)+"@"+meta.getAddress());
                            if(meta.getAddress() == null) {
                                meta.setAddress("unknown address");
                            }

                            meta.setTitle(item.getFilename()+"@"+meta.getAddress());

                            meta.setCreator(user.getCompleteName());

                            //add a dataItem to the list on the top of view
                            item.setCollectionId(collectionID);
                            item.setLocalPath(filePath);
                            item.setMetaDataLocal(meta);
                            item.setLocal(true);
                            item.setCreatedBy(user);

                            meta.save();
                            item.save();

                            itemList.add(item);

                            DataItem dataItem = new Select()
                                    .from(DataItem.class)
                                    .where("isLocal = ?", true)
                                    .executeSingle();
                            meta.save();

                            Log.v(LOG_TAG + "when save", gson.toJson(dataItem));

                            //change the icon of the view
                            poi_list.setIcon(getResources().getDrawable(R.drawable.action_uploadlist_red));
                            //imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));

                            reSetUpView();
                            //reSetUpAudioView();

                        } else {
                            showToast("Please press + button start to work");
                        }
                    }
                });



        rootView.findViewById(R.id.btnLocationUpdates)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onLocationViewClicked(btnStartLocationUpdates);
                    }
                });

//        playBtn = (Button) rootView.findViewById(R.id.playBtn);
//        playBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //play();
//            }
//        });
//        playBtn.setVisibility(View.INVISIBLE);
        visualizerView = (VisualizerView) rootView.findViewById(R.id.visualizerView);
        setupVisualizer();
        visualizerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent evrnt){
                if(readyToPlay){
                    playbackManager.showMediaController();
                }
                return false;
            }
        });

        return rootView;
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
        //bottomCenterButton.setVisibility(View.VISIBLE);

    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        Log.v(LOG_TAG, "onSaveInstanceState");
//        // Save UI state changes to the savedInstanceState.
//        // This bundle will be passed to onCreate if the process is
//        // killed and restarted.
//        savedInstanceState.putString("photoFilePath", photoFilePath);
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (savedInstanceState != null) {
//            // Restore last state for checked position.
//            photoFilePath = savedInstanceState.getString("photoFilePath");
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
        OttoSingleton.getInstance().unregister(this);
        Log.d(LOG_TAG, "onPasue");
        //bottomCenterButton.setVisibility(View.INVISIBLE);
        if(playbackManager !=null) {
            playbackManager.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if(playbackManager !=null) {
//            playbackManager.dispose();
//            playbackHandler = null;
//        }
     }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recordStop();
        releaseVisualizer();
        Log.v(LOG_TAG, "start onDestroy~~~");
    }

    // for the POI_list icon in menu_launchpad
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_launchpad, menu);
        poi_list = menu.findItem(R.id.POI_list);

        if(new Select()
                .from(DataItem.class)
                .where("isLocal = ?", true)
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
                //getActivity().finish();
            }
        } else if (requestCode == INTENT_ENABLE_NET) {
            if (!DeviceStatus.isNetworkEnabled(getActivity())) {
                Toast.makeText(getActivity(), R.string.problem_no_net,
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == INTENT_TAKE_PHOTO) {
            Log.v(LOG_TAG+ " resultCode", String.valueOf(resultCode));
            if (resultCode == getActivity().RESULT_OK) {
                //Bitmap photo = (Bitmap) data.getExtras().get("output");
                //imageView.setImageBitmap(photo);

                if(filePath != null) {
                    File imgFile = new File(filePath);
                    if (imgFile.exists()) {
                        imageView.setVisibility(View.VISIBLE);

                        Picasso.with(getActivity())
                                .load(imgFile)
                                .resize(imageView.getWidth(), imageView.getHeight())
                                .into(imageView);
                    }
                    addImageToGallery(filePath);

                }

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the photo capture
            } else{
                takePhoto();
            }
        } else if (requestCode == INTENT_PICK_PHOTO){
            if (resultCode == getActivity().RESULT_OK) {
                Uri imageUri = data.getData();
                Picasso.with(getActivity())
                        .load(imageUri)
                        .resize(imageView.getWidth(), imageView.getHeight())
                        .into(imageView);
                filePath = getRealPathFromURI(imageUri);
                // example /storage/emulated/0/DCIM/Camera/IMG_20150408_170256.jpg
                fileName = filePath.split("\\/")[filePath.split("\\/").length-1];

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the photo picking
            }
        } else if (requestCode == INTENT_PICK_VIDEO){
            if (resultCode == getActivity().RESULT_OK) {
                Uri videoUri = data.getData();
                Picasso.with(getActivity())
                        .load(videoUri)
                        .resize(imageView.getWidth(), imageView.getHeight())
                        .into(imageView);
                filePath = getRealPathFromURI(videoUri);
                // example /storage/emulated/0/DCIM/Camera/IMG_20150408_170256.jpg
                fileName = filePath.split("\\/")[filePath.split("\\/").length-1];

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the photo picking
            }
        } else if (requestCode == INTENT_PICK_AUDIO){
            if (resultCode == getActivity().RESULT_OK) {
                Uri audioUri = data.getData();
                Picasso.with(getActivity())
                        .load(audioUri)
                        .resize(imageView.getWidth(), imageView.getHeight())
                        .into(imageView);
                filePath = getRealPathFromURI(audioUri);
                // example /storage/emulated/0/DCIM/Camera/IMG_20150408_170256.jpg
                fileName = filePath.split("\\/")[filePath.split("\\/").length-1];

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the photo picking
            }
        }else if (requestCode == INTENT_PICK_DATA){
            if (resultCode == getActivity().RESULT_OK) {
                Uri fileUri = data.getData();
                Picasso.with(getActivity())
                        .load(fileUri)
                        .resize(imageView.getWidth(), imageView.getHeight())
                        .into(imageView);
                filePath = getRealPathFromURI(fileUri);
                // example /storage/emulated/0/DCIM/Camera/IMG_20150408_170256.jpg
                fileName = filePath.split("\\/")[filePath.split("\\/").length-1];

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the photo picking
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //@Subscribe
    public void onGetMetadataFromUser(MetadataIsReadyEvent event) {

        meta.setTags(event.tags);
        Log.v(LOG_TAG, event.tags.get(0));
        meta.setTitle(meta.getTags().get(0)+"@"+meta.getAddress());

        meta.setCreator(user.getCompleteName());

        //add a dataItem to the list on the top of view
        item.setCollectionId(collectionID);
        item.setLocalPath(filePath);
        item.setMetaDataLocal(meta);
        item.setLocal(true);
        item.setCreatedBy(user);
        item.setFilename(fileName);

        meta.save();
        item.save();

        Log.v(LOG_TAG, item.getMetaDataLocal().getTags().get(0));
        Log.v(LOG_TAG, item.getFilename());
        itemList.add(item);

        DataItem dataItem = new Select()
                .from(DataItem.class)
                .where("isLocal = ?", true)
                .executeSingle();
        if(dataItem.getMetaDataLocal().getTags() == null){
            meta.save();
        }

        Log.v(LOG_TAG+"when save", gson.toJson(dataItem));

        //change the icon of the view
        poi_list.setIcon(getResources().getDrawable(R.drawable.action_uploadlist_blue));
        //imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));

        reSetUpView();
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
            if (filePath != null) {
                // Set the image file name
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(filePath)));
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
            filePath = new File(storageDir.getPath(), photoFileName
                    + ".jpg").getPath();
            fileName = photoFileName + ".jpg";

            //Toast.makeText(getActivity(), photoFilePath, Toast.LENGTH_LONG).show();
            Log.v(LOG_TAG, filePath);
            // Create the storage directory if it does not exist
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                filePath = null;
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.problem_create_file,
                    Toast.LENGTH_LONG).show();
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
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

    private void reSetUpView(){
        imageView.setVisibility(View.INVISIBLE);
        //rootView.findViewById(R.id.save).setVisibility(View.INVISIBLE);
        item = new DataItem();
        meta = new MetaDataLocal();
    }

    private void reSetUpAudioView(){
        visualizerView.setVisibility(View.INVISIBLE);
        playbackManager.hideMediaController();
        item = new DataItem();
        meta = new MetaDataLocal();
    }

    private void setupVisualizer() {
        Paint paint = new Paint();
        paint.setStrokeWidth(5f);                     //set bar width
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(200, 227, 69, 53)); //set bar color
        BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(2, paint, false);
        visualizerView.addRenderer(barGraphRendererBottom);
    }

    private void releaseVisualizer() {
        visualizerView.release();
        visualizerView = null;
    }

    private void record() {
        if (startRecording) {
            recordStart();
        }
        else {
            recordStop();
        }
    }

    private void recordStart() {
        startRecording();
        startRecording = false;
        //recordBtn.setText(R.string.stopRecordBtn);
        //playBtn.setEnabled(false);
    }

    private void recordStop() {
        stopRecording();
        startRecording = true;
        //recordBtn.setText(R.string.recordBtn);
        //playBtn.setEnabled(true);
    }

    private void startRecording() {
        if(playbackManager !=null) {
            //playbackManager.dispose();
            playbackManager = null;
            //playbackHandler = null;

        }
        readyToPlay = false;
        filePath = StorageUtils.getFileName(true);
        showToast(filePath);
        fileName = filePath.split("\\/")[filePath.split("\\/").length-1];
        //showToast(fileName);

        recordingThread = new AudioRecordingThread(filePath, new AudioRecordingHandler() { //pass file name where to store the recorded audio
            @Override
            public void onFftDataCapture(final byte[] bytes) {
                //TODO
                //start thread on Main Activity?
                ((MainActivity)getActivity()).runOnUiThread(new Runnable() {
                    public void run() {
                        if (visualizerView != null) {
                            visualizerView.updateVisualizerFFT(bytes); //update VisualizerView with new audio portion
                        }
                    }
                });
            }

            @Override
            public void onRecordSuccess() {}

            @Override
            public void onRecordingError() {}

            @Override
            public void onRecordSaveError() {}
        });
        recordingThread.start();
    }

    private void stopRecording() {
        if (recordingThread != null) {
            recordingThread.stopRecording();
            recordingThread = null;
            readyToPlay = true;

            playbackManager = new AudioPlaybackManager(getActivity(), visualizerView, playbackHandler);
            playbackManager.setupPlayback(filePath);
        }
    }
//    private void play() {
//        Intent i = new Intent(AudioRecordingActivity.this, AudioPlaybackActivity.class);
//        i.putExtra(VideoPlaybackActivity.FileNameArg, audioFileName);
//        startActivityForResult(i, 0);
//    }

}