package de.mpg.mpdl.www.datacollector.app.Collection;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.Model.MetaDataLocal;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.Retrofit.MetaDataConverter;
import de.mpg.mpdl.www.datacollector.app.Retrofit.RetrofitClient;
import de.mpg.mpdl.www.datacollector.app.ItemList.CustomSwipeAdapter;
import de.mpg.mpdl.www.datacollector.app.ItemList.DetailActivity;
import de.mpg.mpdl.www.datacollector.app.SettingsActivity;
import de.mpg.mpdl.www.datacollector.app.Workflow.UploadView.ReadyToUploadCollectionActivity;
import de.mpg.mpdl.www.datacollector.app.utils.DeviceStatus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by allen on 16/06/15.
 */
public class CollectionDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.collection_container, new CollectionItemFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
            startActivity(showSettingIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class CollectionItemFragment extends Fragment {

        private List<DataItem> dataList = new ArrayList<DataItem>();
        public  CustomSwipeAdapter adapter;
        private ListView listView;
        private View rootView;
        private String dataCollectionId;

        private final String LOG_TAG = CollectionItemFragment.class.getSimpleName();
        private String collectionID = DeviceStatus.collectionID;
        private String username = DeviceStatus.username;
        private String password = DeviceStatus.password;


        Callback<List<DataItem>> callbackItems = new Callback<List<DataItem>>() {
            @Override
            public void success(List<DataItem> dataList, Response response) {
                //load all data from imeji
                //adapter =  new CustomListAdapter(getActivity(), dataList);
                List<DataItem> dataListLocal = new ArrayList<DataItem>();

                ActiveAndroid.beginTransaction();
                try {
                    // here get the string of Metadata Json
                    for (DataItem item : dataList) {

                        MetaDataLocal metaDataLocal = MetaDataConverter.
                                metaDataToMetaDataLocal(item.getMetadata());
                        item.setMetaDataLocal(metaDataLocal);
                        dataListLocal.add(item);
                        //item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally{
                    ActiveAndroid.endTransaction();

                    adapter =  new CustomSwipeAdapter(getActivity(), dataListLocal);
                    listView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.v(LOG_TAG, error.toString());
            }
        };




        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                dataCollectionId = intent.getStringExtra(Intent.EXTRA_TEXT);
                getCollectionItems(dataCollectionId);


                //adapter =  new CustomListAdapter(getActivity(), dataList);
                adapter = new CustomSwipeAdapter(getActivity(), dataList);


                rootView = inflater.inflate(R.layout.fragment_section_list, container, false);
                //rootView = inflater.inflate(R.layout.fragment_section_list_swipe, container, false);
                listView = (ListView) rootView.findViewById(R.id.item_list);
                //listView = (SwipeMenuListView) rootView.findViewById(R.id.listView);
                listView.setAdapter(adapter);

                FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
                fab.attachToListView(listView);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ReadyToUploadCollectionActivity.class);
                        startActivity(intent);
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

                        startActivity(showDetailIntent);
                    }
                });
            }
            return rootView;
        }

        private void getCollectionItems(String collectionId){
            RetrofitClient.getCollectionItems(collectionId, callbackItems, username, password);
        }



    }

}
