package de.mpg.mpdl.www.datacollector.app.Workflow.UploadView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Produce;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Event.OttoSingleton;
import de.mpg.mpdl.www.datacollector.app.Event.UploadEvent;
import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.Retrofit.MetaDataConverter;
import de.mpg.mpdl.www.datacollector.app.Retrofit.RetrofitClient;
import de.mpg.mpdl.www.datacollector.app.utils.DeviceStatus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

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
    private MenuItem upload;


    public TypedFile typedFile;
    String json;
    List<DataItem> itemList = new ArrayList<DataItem>();
    DataItem item;

    private String username = "shi@mpdl.mpg.de";
    private String password = "allen";

    Callback<DataItem> callback = new Callback<DataItem>() {
        @Override
        @Produce
        public void success(DataItem dataItem, Response response) {
            //adapter =  new CustomListAdapter(getActivity(), dataList);
            //listView.setAdapter(adapter);
            showToast("Upload data Successfully");
            Log.v(LOG_TAG, dataItem.getFilename());

                //Log.v(LOG_TAG, item.getFilename());
            //TODO upload a new POI
            //upload a POI as Album on Imeji
            //RetrofitClient.createPOI(typedFile, json, callback, username, password);

            //TODO produce a message event to third-party fragment to display the POI on map
            OttoSingleton.getInstance().post(
                    new UploadEvent(response.getStatus()));
            new Delete().from(DataItem.class).where("filename = ?", dataItem.getFilename()).execute();
        }

        @Override
        public void failure(RetrofitError error) {
            showToast("Upload data Failed");
            if (error == null || error.getResponse() == null) {
                OttoSingleton.getInstance().post(new UploadEvent(null));
            } else {
                OttoSingleton.getInstance().post(
                        new UploadEvent(error.getResponse().getStatus()));
            }
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

//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new ListSectionFragment())
//                    .commit();
//        }

        dataList = new Select()
                .from(DataItem.class)
                .where("isLocal = ?", 1)
                .execute();

        if (dataList == null) {
            DeviceStatus.showToast(this, "Go back to get some data");
        }

//        adapter =  new CustomListAdapter(this, dataList);
//        listView = (ListView) findViewById(R.id.item_list);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                DataItem dataItem = (DataItem) adapter.getItem(position);
//                //Context context = getActivity();
//                showToast(dataItem.getCollectionId());
//
//                Intent showDetailIntent = new Intent(ReadyToUploadCollectionActivity.this,
//                        DetailActivity.class);
//                //showDetailIntent.putExtra(Intent.EXTRA_SUBJECT, dataItem);
//                //showDetailIntent.setData();
//                //startService(showDetailIntent);
//                ReadyToUploadCollectionActivity.this.startActivity(showDetailIntent);
//            }
//        });
//      listView.setAdapter(adapter);

        adapter = new GridImageAdapter(this, dataList);
        GridView gridview = (GridView) findViewById(R.id.grid_view);
        gridview.setAdapter(adapter);

        //TODO
        // Edit and delete the list
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                showToast("" + position);
            }
        });

        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                new Delete().from(DataItem.class).
                        where("filename = ?", dataList.get(position).getFilename()).execute();
                dataList.remove(position);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
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
            getMenuInflater().inflate(R.menu.menu_ready_to_update, menu);
            upload = menu.findItem(R.id.upload);
            return true;
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            if (id == R.id.upload) {
                //Intent showSettingIntent = new Intent(this, SettingsActivity.class);
                //startActivity(showSettingIntent);
                createNewPOI();
                upload(dataList);
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        //create a POI locally
        private void createNewPOI() {

        }

        private void upload(List<DataItem> iList) {
            String jsonPart1 = "\"collectionId\" : \"Qwms6Gs040FBS264\"";

            for (DataItem item : iList) {
                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                item.getMetaDataLocal().setDeviceID("1");
                typedFile = new TypedFile("multipart/form-data", new File(item.getLocalPath()));

                String jsonPart2 = "\"metadata\": " + gson.toJson(MetaDataConverter.metaDataLocalToMetaDataList(item.getMetaDataLocal()));

                json = "{" + jsonPart1 + "," + jsonPart2 + "}";
                //json ="{" + jsonPart1  +"}";

                Log.v(LOG_TAG, json);
                RetrofitClient.uploadItem(typedFile, json, callback, username, password);
                //TODO  popup a progress bar to show the uploading
            }
        }

        public void showToast(String message) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }


}