package de.mpg.mpdl.www.datacollector.app.Workflow.UploadView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.dd.CircularProgressButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Produce;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Event.OttoSingleton;
import de.mpg.mpdl.www.datacollector.app.Event.UploadEvent;
import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.ItemImeji;
import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.Organization;
import de.mpg.mpdl.www.datacollector.app.Model.POI;
import de.mpg.mpdl.www.datacollector.app.Model.User;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.Retrofit.MetaDataConverter;
import de.mpg.mpdl.www.datacollector.app.Retrofit.RetrofitClient;
import de.mpg.mpdl.www.datacollector.app.utils.DeviceStatus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by allen on 21/04/15.
 */
public class ReadyToUploadCollectionActivity extends FragmentActivity {
    private final String LOG_TAG = ReadyToUploadCollectionActivity.class.getSimpleName();

    public static final String ARG_SECTION_NUMBER = "section_number";
    public ArrayAdapter<String> mForecastAdapter;
    private List<DataItem> dataList = new ArrayList<DataItem>();
    private DataItem dataItem;
    public GridImageAdapter adapter;
    ListView listView;
    private View rootView;
    private MenuItem upload;
    private String collectionID = DeviceStatus.collectionID;
    private String username = DeviceStatus.username;
    private String password = DeviceStatus.password;

    private CircularProgressButton processButton;
    private static Gson gson = new GsonBuilder()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    List<String> itemIds = new ArrayList<String>();

    public TypedFile typedFile;
    String json;
    List<DataItem> itemList = new ArrayList<DataItem>();
    DataItem item;



    // callback for Item uploading
    Callback<ItemImeji> callback = new Callback<ItemImeji>() {
        @Override
        @Produce
        public void success(ItemImeji dataItem, Response response) {
            //adapter =  new CustomListAdapter(getActivity(), dataList);
            //listView.setAdapter(adapter);
            DeviceStatus.showSnackbar(rootView, "Upload data Successfully");
            Log.v(LOG_TAG, dataItem.getId() + ":" + dataItem.getFilename());
            itemIds.add(dataItem.getId());

            new Delete().from(DataItem.class).where("filename = ?", dataItem.getFilename()).execute();

            List<DataItem> tempList =  dataList;
            for(int i = 0; i<dataList.size(); i++){
                DataItem d = tempList.get(i);
                dataList.remove(d);
            }

            //You cannot modify, add/remove, a List while iterating through it.
            //The foreach loop you are using creates an Iterator object in the background.
            // Use a regular for loop if you'd like to modify the list.

//            for (DataItem item: dataList){
//                //if(item.getFilename().equals(dataItem.getFilename())){
//                    dataList.remove(item);
//                //}
//            }
            adapter.notifyDataSetChanged();

            //begin to upload only when all the dataItem are uploaded
            if(new Select()
                    .from(DataItem.class)
                    .where("isLocal = ?", true)
                    .execute().size()<1){
                //upload a POI as Album on Imeji
                RetrofitClient.createPOI(createNewPOI(), callbackPoi, username, password);

                //TODO produce a message event to third-party fragment to display the POI on map
                OttoSingleton.getInstance().post(
                        new UploadEvent(response.getStatus()));
            }
            processButton.setProgress(100); // set progress to 100 or -1 to indicate complete or error state
        }

        @Override
        public void failure(RetrofitError error) {
            DeviceStatus.showSnackbar(rootView, "Upload data Failed");
            if (error == null || error.getResponse() == null) {
                OttoSingleton.getInstance().post(new UploadEvent(null));
            } else {
                OttoSingleton.getInstance().post(
                        new UploadEvent(error.getResponse().getStatus()));
            }
            //String jsonBody =  new String(((TypedByteArray)error.getResponse().getBody()).getBytes());
            //Log.v(LOG_TAG, jsonBody);
            Log.v(LOG_TAG, String.valueOf(error));
            processButton.setProgress(-1); // set progress to 0 to switch back to normal state

        }
    };

    // callback for POI uploading
    Callback<POI> callbackPoi = new Callback<POI>() {

        @Override
        public void success(POI poi, Response response) {
            DeviceStatus.showSnackbar(rootView, "Upload POI success!");
            Log.v(LOG_TAG, poi.getId());

            TypedString typedString = new TypedString(gson.toJson(itemIds));

            Log.v("json typedString : ", typedString.toString());
            RetrofitClient.linkItems(poi.getId(), typedString, username, password, callbackLink);

        }

        @Override
        public void failure(RetrofitError error) {

            DeviceStatus.showSnackbar(rootView, "Upload POI Failed");
            Log.v(LOG_TAG, String.valueOf(error.getResponse().getStatus()));
            Log.v(LOG_TAG, String.valueOf(error));

//            RestError restError = (RestError) error.getBodyAs(RestError.class);
//
//            if (restError != null)
//                failure(error);
//            else
//            {
//                //failure(new RetrofitError(error.getMessage()));
//            }
//            Log.v(LOG_TAG, String.valueOf(restError.getCode()));
//            Log.v(LOG_TAG,restError.getStrMessage());

        }
    };

    // callback for POI linking
    Callback<List<String>> callbackLink = new Callback<List<String>>() {
        @Override
        public void success(List<String> strings, Response response) {
            Log.v(LOG_TAG, gson.toJson(strings));
        }

        @Override
        public void failure(RetrofitError error) {
            Log.v(LOG_TAG, String.valueOf(error.getResponse().getStatus()));
            Log.v(LOG_TAG, String.valueOf(error));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        Log.e(LOG_TAG, "start onCreate~~~");
        //setContentView(R.layout.fragment_section_list);
        setContentView(R.layout.activity_upload_gridview);
        rootView = getWindow().getDecorView().findViewById(android.R.id.content);

//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new ListSectionFragment())
//                    .commit();
//        }

        dataList = new Select()
                .from(DataItem.class)
                .where("isLocal = ?", true)
                .execute();



        if (dataList == null) {
            DeviceStatus.showSnackbar(rootView, "Go back to get some data");
        }

        adapter = new GridImageAdapter(this, dataList);
        GridView gridview = (GridView) findViewById(R.id.grid_view);
        registerForContextMenu(gridview);

        gridview.setAdapter(adapter);

        // Edit and delete the list
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DataItem item = dataList.get((int) id);
                DeviceStatus.showSnackbar(rootView, item.getFilename() + "\n" + "Long press to delete.");

                if(item.getMetaDataLocal() != null) {
                    if(item.getMetaDataLocal().getType().equals("audio")){
                        audioPlayer(item.getLocalPath());
                    }
                }
            }
        });

//        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view,
//                                           final int position, long id) {
//                new Delete().from(DataItem.class).
//                        where("filename = ?", dataList.get(position).getFilename()).execute();
//                dataList.remove(position);
//                adapter.notifyDataSetChanged();
//                return false;
//            }
//        });
        processButton = (CircularProgressButton) findViewById(R.id.btnWithText);
        processButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(dataList!=null && dataList.size()>0) {
                            upload(dataList);
                            processButton.setIndeterminateProgressMode(true); // turn on indeterminate progress
                            processButton.setProgress(50); // set progress > 0 & < 100 to display indeterminate progress
                        }else{
                            DeviceStatus.showSnackbar(rootView, "Nothing to upload :)");
                        }
                    }
                });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Are you sure to delete?");
        AdapterView.AdapterContextMenuInfo cmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add(1, cmi.position, 0, "Delete");
        menu.add(2, cmi.position, 0, "Cancel");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Delete")){
            new Delete().from(DataItem.class).
                        where("filename = ?", dataList.get(item.getItemId()).getFilename()).execute();
            dataList.remove(item.getItemId());
            adapter.notifyDataSetChanged();
            Log.v("", String.valueOf(item.getItemId()));
        }
        else if(item.getTitle().equals("Cancel")){
            Log.v("", String.valueOf(item.getItemId()));
        }
        else {
            return false;

        }
        // Return false to allow normal context menu processing to proceed,
        //        true to consume it here.
        return true;
    }

    @Override
        protected void onStart() {
            super.onStart();
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
            Log.e(LOG_TAG, "start onResume~~~");
        }

        // save the current status
        @Override
        protected void onPause() {
            super.onPause();
            Log.e(LOG_TAG, "start onPause~~~");
        }

        @Override
        protected void onStop() {
            super.onStop();
            Log.e(LOG_TAG, "start onStop~~~");
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            Log.e(LOG_TAG, "start onDestroy~~~");
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
//            getMenuInflater().inflate(R.menu.menu_ready_to_update, menu);
//            upload = menu.findItem(R.id.upload);
            return true;
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

//            if (id == R.id.upload) {
//                //createNewPOI();
//                upload(dataList);
//                return true;
//            }

            return super.onOptionsItemSelected(item);
        }



        private POI createNewPOI() {
            List<User> contributors = new ArrayList<User>();
            User user = new User();
            List<Organization> orgs = new ArrayList<Organization>();
            Organization org = new Organization();
            org.setName("TUM");
            org.setDescription("OpenGridMap");
            org.setCity("Munich");
            org.setCountry("Germany");
            orgs.add(org);

            user.setFamilyName("Shi");
            user.setGivenName("Allen");
            user.setOrganizations(orgs);
            contributors.add(user);

            POI poi = new POI();
            poi.setTitle("DataCollector");
            poi.setDescription("just test");
            poi.setContributors(contributors);
            Log.v(LOG_TAG,poi.toString());
            return poi;
        }

        private void upload(List<DataItem> iList) {
            String jsonPart1 = "\"collectionId\" : \"" +
                    collectionID +
                    "\"";

            for (DataItem item : iList) {
                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                item.getMetaDataLocal().setDeviceID("1");
                typedFile = new TypedFile("multipart/form-data", new File(item.getLocalPath()));

                String jsonPart2 = "\"metadata\": " +
                        gson.toJson(MetaDataConverter.metaDataLocalToMetaDataList(item.getMetaDataLocal()));

                json = "{" + jsonPart1 + "," + jsonPart2 + "}";
                //json ="{" + jsonPart1  +"}";

                Log.v(LOG_TAG, json);
                RetrofitClient.uploadItem(typedFile, json, callback, username, password);
            }
        }


        public void audioPlayer(String path){
            //set up MediaPlayer
            MediaPlayer mp = new MediaPlayer();

            try {
                mp.setDataSource(path);
                mp.prepare();
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}
