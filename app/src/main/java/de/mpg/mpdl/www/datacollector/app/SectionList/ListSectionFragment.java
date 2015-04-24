package de.mpg.mpdl.www.datacollector.app.SectionList;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.Model.MetaDataLocal;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.Retrofit.ImejiAPI;
import de.mpg.mpdl.www.datacollector.app.Retrofit.RetrofitClient;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


//import de.mpg.mpdl.www.datacollector.app.SectionList.DetailActivity;

//import de.mpg.mpdl.www.datacollector.app.SectionList.DetailActivity;

/**
 * Created by allen on 02/04/15.
 */
public class ListSectionFragment extends Fragment {
    /**
     * Encapsulates fetching the forecast and displaying it as a {@link android.widget.ListView} layout.
     */

    public static final String ARG_SECTION_NUMBER = "section_number";
    private ProgressDialog pDialog;
    public ArrayAdapter<String> mForecastAdapter;
    private List<DataItem> dataList = new ArrayList<DataItem>();
    public CustomListAdapter adapter;
    ListView listView;
    View rootView;
    private final String LOG_TAG = ListSectionFragment.class.getSimpleName();


    Callback<List<DataItem>> callback = new Callback<List<DataItem>>() {
        @Override
        public void success(List<DataItem> dataList, Response response) {
            adapter =  new CustomListAdapter(getActivity(), dataList);

            ActiveAndroid.beginTransaction();
            try {
                // here get the string of Metadata Json
                for (DataItem item : dataList) {
                    if (item.getCollectionId().equals("Qwms6Gs040FBS264")) {
                        convertMetaData(item);
                        Log.v(LOG_TAG, String.valueOf(item.getFilename()));
                        Log.v(LOG_TAG, String.valueOf(item.getMetaDataLocal().getTitle()));
                        Log.v(LOG_TAG, String.valueOf(item.getMetaDataLocal().getAccuracy()));
                        item.save();
                    }
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally{
                ActiveAndroid.endTransaction();
            }

            listView.setAdapter(adapter);
            pDialog.hide();
            Log.v(LOG_TAG, "get list OK");

            showToast("get list OK");

        }

        @Override
        public void failure(RetrofitError error) {
            Log.v(LOG_TAG, "get list failed");
            Log.v(LOG_TAG, error.toString());

            showToast("get list failed");
        }
    };

    public ListSectionFragment() {
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        updateDataItem();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume(){
        super.onResume();

    }
    @Override
    public void onPause(){
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
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
            //updateWeather();
            updateDataItem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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


        adapter =  new CustomListAdapter(getActivity(), dataList);

        rootView = inflater.inflate(R.layout.fragment_section_list, container, false);
        listView = (ListView) rootView.findViewById(R.id.item_list);
        //listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                DataItem dataItem = (DataItem) adapter.getItem(position);
                //Context context = getActivity();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity(), dataItem.getCollectionId(), duration);
                toast.show();

                Intent showDetailIntent = new Intent(getActivity(), DetailActivity.class);
                //showDetailIntent.putExtra(Intent.EXTRA_SUBJECT, dataItem);
                //showDetailIntent.setData();
                //startService(showDetailIntent);
                startActivity(showDetailIntent);
            }
        });

        //listView.setOnScrollListener();
        return rootView;
    }


//    private void updateWeather() {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        // then you use
//        String locationFromSetting = prefs.getString(getString(R.string.pref_location_key),
//                getString(R.string.pref_location_default));
//
//        FetchItemTask fetchTask = new FetchItemTask();
//        fetchTask.execute(locationFromSetting);
//    }

    private void updateDataItem(){
        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        RetrofitClient.getItems(callback);
        pDialog.setMessage("Loading...");
        pDialog.show();
    }





    /*AsyncTask<String, Void, String[]>
      String:   as the input， in our case is location
      Void:     is the percentage of the background job， can be called onProgressUpdate() in Main UI
      String[]: as the output result
      *
      */
    public class FetchItemTask extends AsyncTask<String, Void, List<DataItem>> {

        private final String LOG_TAG = FetchItemTask.class.getSimpleName();
        private static final String REST_SERVER = "http://dev-faces.mpdl.mpg.de/imeji/rest/";

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


    public void convertMetaData(DataItem dataItem){

        MetaDataLocal metaDataLocal = new MetaDataLocal();
        List<String> tags = new ArrayList<String>();
        // here get the string of Metadata Json
        Gson gson = new Gson();
        String json = gson.toJson(dataItem.getMetadata());
        try {
            JSONArray jsonArray = new JSONArray(json);

            for(int i = 0; i < jsonArray.length(); i++ ){
                JSONObject meta = jsonArray.getJSONObject(i);
                //String type = meta.getString("typeUri").split("#")[1];
                String label = meta.getString("labels").split("\"")[3];
                //String statementUri = meta.getString("statementUri");

                if(label.equals("title")){
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setTitle(value.getString("text"));
                } else if(label.equals("author")){
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setCreator(value.getString("text"));
                } else if(label.equals("accuracy")){
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setAccuracy(value.getDouble("number"));

                } else if(label.equals("deviceID")){
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setDeviceID(value.getString("text"));
                } else if(label.equals("location")){
                    //"value":{"name":"Amalienstr. 33 D-80799 München","longitude":11.57648,"latitude":48.147899}
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setAddress(value.getString("name"));
                    metaDataLocal.setLatitude(value.getDouble("latitude"));
                    metaDataLocal.setLongitude(value.getDouble("longitude"));
                } else if(label.equals("tags")){
                    JSONObject value = (JSONObject) meta.get("value");
                    tags.add(value.getString("text"));
                }
            }
            metaDataLocal.setTags(tags);
            //metaDataLocal.setWhichItem(dataItem);
            metaDataLocal.save();

            dataItem.setMetaDataLocal(metaDataLocal);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}