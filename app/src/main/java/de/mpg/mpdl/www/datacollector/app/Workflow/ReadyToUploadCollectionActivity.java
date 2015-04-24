package de.mpg.mpdl.www.datacollector.app.Workflow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.SectionList.CustomListAdapter;
import de.mpg.mpdl.www.datacollector.app.SectionList.DetailActivity;
import de.mpg.mpdl.www.datacollector.app.SettingsActivity;
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
    public CustomListAdapter adapter;
    ListView listView;


    public TypedFile typedFile;
    String json;
    List<DataItem> itemList = new ArrayList<DataItem>();
    DataItem item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        Log.e(LOG_TAG, "start onCreate~~~");
        setContentView(R.layout.fragment_section_list);

//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new ListSectionFragment())
//                    .commit();
//        }

        dataList = new Select()
                .from(DataItem.class)
                .where("isLocal = ?", 1)
                .execute();

//        dataItem = new Select()
//                .from(DataItem.class)
//                .executeSingle();
        Log.v(LOG_TAG, dataList.get(0).getLocalPath());

        //Log.v("data1: ", dataList.get(0).getLocalPath());
        //Log.v("metatadata1: ", String.valueOf(dataList.get(0).getMetaDataLocal().getAccuracy()));
        Log.v("metatadata1: ", "onCreateView");
        adapter =  new CustomListAdapter(this, dataList);

        listView = (ListView) findViewById(R.id.item_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                DataItem dataItem = (DataItem) adapter.getItem(position);
                //Context context = getActivity();
                int duration = Toast.LENGTH_SHORT;
                showToast(dataItem.getCollectionId());

                Intent showDetailIntent = new Intent(ReadyToUploadCollectionActivity.this,
                        DetailActivity.class);
                //showDetailIntent.putExtra(Intent.EXTRA_SUBJECT, dataItem);
                //showDetailIntent.setData();
                //startService(showDetailIntent);
                ReadyToUploadCollectionActivity.this.startActivity(showDetailIntent);
            }
        });

        listView.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.menu_section_list, menu);

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



    //TODO
    // Edit and delete the list
//        @Subscribe
//        public void OnGetNewItemFromUser(GetNewItemFromUserEvent event){
//            itemList = event.itemList;
//            Log.v(LOG_TAG, "Got a new data item: " +
//                    itemList.get(itemList.size()-1).getMetaDataLocal().getTitle());
//        }

        private void upload(){
//            typedFile = new TypedFile("multipart/form-data", new File(photoFilePath));
//            json = "{ \"collectionId\" : \"Qwms6Gs040FBS264\"}";
//
//            item = new DataItem();
//            RetrofitClient.uploadItem(typedFile, json, callback, username, password);
        }

        public void showToast(String message) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }


}
