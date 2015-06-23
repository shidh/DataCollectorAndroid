package de.mpg.mpdl.www.datacollector.app.SectionList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.okhttp.OkHttpClient;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.Model.MetaDataLocal;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.Retrofit.ImejiAPI;
import de.mpg.mpdl.www.datacollector.app.Retrofit.MetaDataConverter;
import de.mpg.mpdl.www.datacollector.app.Retrofit.RetrofitClient;
import de.mpg.mpdl.www.datacollector.app.Workflow.UploadView.ReadyToUploadCollectionActivity;
import de.mpg.mpdl.www.datacollector.app.utils.DeviceStatus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


/**
 * Created by allen on 02/04/15.
 */
public class ItemListFragment extends Fragment {
    /**
     * Encapsulates fetching the forecast and displaying it as a {@link android.widget.ListView} layout.
     */

    public static final String ARG_SECTION_NUMBER = "section_number";
    private ProgressDialog pDialog;
    private List<DataItem> dataList = new ArrayList<DataItem>();
    //public CustomListAdapter adapter;
    public  CustomSwipeAdapter adapter;
    //SwipeMenuListView listView;
    private ListView listView;
    private View rootView;
    private final String LOG_TAG = ItemListFragment.class.getSimpleName();
    private String collectionID = DeviceStatus.collectionID;
    private String username = DeviceStatus.username;
    private String password = DeviceStatus.password;

    private static final int INTENT_PICK_DATA = 1008;


    private static Gson gson = new GsonBuilder()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    Callback<List<DataItem>> callback = new Callback<List<DataItem>>() {
        @Override
        public void success(List<DataItem> dataList, Response response) {
            //load all data from imeji
            //adapter =  new CustomListAdapter(getActivity(), dataList);
            List<DataItem> dataListLocal = new ArrayList<DataItem>();

            ActiveAndroid.beginTransaction();
            try {
                // here get the string of Metadata Json
                for (DataItem item : dataList) {
                    if (item.getCollectionId().equals(collectionID)) {
                        //convertMetaData(item);

                        MetaDataLocal metaDataLocal = MetaDataConverter.
                                metaDataToMetaDataLocal(item.getMetadata());
                        Log.v(LOG_TAG, gson.toJson(metaDataLocal));
                        metaDataLocal.save();
                        item.setMetaDataLocal(metaDataLocal);
                        Log.v(LOG_TAG, String.valueOf(item.getFilename()));
                        Log.v(LOG_TAG, String.valueOf(item.getMetaDataLocal().getTitle()));
                        dataListLocal.add(item);
                        item.save();
                    }
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally{
                ActiveAndroid.endTransaction();
                //load local data only
                //adapter =  new CustomListAdapter(getActivity(), dataListLocal);

                adapter =  new CustomSwipeAdapter(getActivity(), dataListLocal);
                listView.setAdapter(adapter);

            }

            if(pDialog != null) {
                pDialog.hide();
            }
            Log.v(LOG_TAG, "get list OK");


        }

        @Override
        public void failure(RetrofitError error) {
            Log.v(LOG_TAG, "get list failed");
            Log.v(LOG_TAG, error.toString());
            showToast("update data failed");
        }
    };




    Callback<Response> callbackDel = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            List<DataItem> dataListLocal = new ArrayList<DataItem>();
            //dataList.remove();
            adapter =  new CustomSwipeAdapter(getActivity(), dataList);
            listView.setAdapter(adapter);


            Log.v(LOG_TAG, "remove item" + response + "OK");
        }

        @Override
        public void failure(RetrofitError error) {
            Log.v(LOG_TAG, "delete item failed");
            Log.v(LOG_TAG, error.toString());
        }
    };


    public ItemListFragment() {
    }


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
        //updateDataItem();
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
            updateDataItem();
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
        Log.v(LOG_TAG, "start onCreateView~~~");

//        List<String> dataList = new ArrayList<String>(Arrays.asList(dummyData));
        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.

//        mForecastAdapter = new ArrayAdapter<String>(
//                getActivity(), // The current context (this activity)
//                R.layout.list_item_cell, // The name of the layout.
//                R.id.list_item_forecast_textview, // The ID of the textview to populate.
//                //R.id.thumbnail,
//                new ArrayList());


        dataList = new Select()
                .from(DataItem.class)
                .where("isLocal != ?", 1)
                .execute();

        //adapter =  new CustomListAdapter(getActivity(), dataList);
        adapter =  new CustomSwipeAdapter(getActivity(), dataList);


        //TODO try to change the cell view
        rootView = inflater.inflate(R.layout.fragment_section_list, container, false);
        //rootView = inflater.inflate(R.layout.fragment_section_list_swipe, container, false);
        //delete = (Button) rootView.findViewById(R.id.delete);
        listView = (ListView) rootView.findViewById(R.id.item_list);
        //listView = (SwipeMenuListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToListView(listView);

        // set creator
        //listView.setMenuCreator(creator);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //WorkflowSectionFragment newFragment = new WorkflowSectionFragment();
                //newFragment.show(getActivity().getSupportFragmentManager(), "showWorkflow");
                Intent intent = new Intent(getActivity(), ReadyToUploadCollectionActivity.class);
                startActivity(intent);

//                Intent pickFile = new Intent(Intent.ACTION_PICK);
//                pickFile.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(pickFile, INTENT_PICK_DATA);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                DataItem dataItem = (DataItem) adapter.getItem(position);
                //Context context = getActivity();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity(), dataItem.getCollectionId(), duration);
                toast.show();

                Intent showDetailIntent = new Intent(getActivity(), DetailActivity.class);
                showDetailIntent.putExtra(Intent.EXTRA_TEXT, dataItem.getFilename());

                showDetailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                startActivity(showDetailIntent);
            }
        });

//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //deleteDataItem();
//                showToast("delete from"+ LOG_TAG);
//            }
//        });
        //listView.setOnScrollListener();
        return rootView;
    }



    private void updateDataItem(){
        // Showing progress dialog before making http request
        RetrofitClient.getItems(callback, username, password);
        //pDialog = new ProgressDialog(getActivity());
        //pDialog.setMessage("Loading...");
        //pDialog.show();
    }

    private void deleteDataItem(String itemId){
        // Showing progress dialog before making http request
        RetrofitClient.deleteItem(itemId, callbackDel, username, password);
    }






    /*AsyncTask<String, Void, String[]>
      String:   as the input， in our case is location
      Void:     is the percentage of the background job， can be called onProgressUpdate() in Main UI
      String[]: as the output result
      *
      */
    public class FetchItemTask extends AsyncTask<String, Void, List<DataItem>> {

        private final String LOG_TAG = FetchItemTask.class.getSimpleName();
        private static final String REST_SERVER = DeviceStatus.BASE_URL;

        @Override
        protected List<DataItem> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setWriteTimeout(3000, TimeUnit.MILLISECONDS);
            OkClient okClient = new OkClient(okHttpClient);

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setClient(okClient)
                    .setEndpoint(REST_SERVER)
                    .build();

            ImejiAPI imejiAPI = restAdapter.create(ImejiAPI.class);

            List<DataItem> items = new ArrayList<DataItem>();
            items = imejiAPI.getItems();
            List<String> ids = new ArrayList<String>();
            for(DataItem item:items){
                Log.v(LOG_TAG, item.getFilename());
                Log.v(LOG_TAG, item.getFileUrl());
                ids.add(item.getCollectionId());
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<DataItem> result) {
            hidePDialog();
            if (result != null) {
                dataList = result;
                adapter.notifyDataSetChanged();
                // New data is back from the server.  Hooray!
            }
        }

    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }



}