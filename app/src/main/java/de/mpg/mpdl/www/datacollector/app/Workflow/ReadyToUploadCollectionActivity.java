package de.mpg.mpdl.www.datacollector.app.Workflow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.SectionList.CustomListAdapter;
import de.mpg.mpdl.www.datacollector.app.SectionList.DetailActivity;
import de.mpg.mpdl.www.datacollector.app.SettingsActivity;

/**
 * Created by allen on 21/04/15.
 */
public class ReadyToUploadCollectionActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new ListSectionFragment())
//                    .commit();
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_detail, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ListSectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";
        private ProgressDialog pDialog;
        public ArrayAdapter<String> mForecastAdapter;
        private List<DataItem> dataList = new ArrayList<DataItem>();
        public CustomListAdapter adapter;
        ListView listView;
        View rootView;
        private final String LOG_TAG = ListSectionFragment.class.getSimpleName();


        public ListSectionFragment() {
            setHasOptionsMenu(true);
        }


        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_section_list, menu);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            adapter =  new CustomListAdapter(getActivity(), dataList);

            rootView = inflater.inflate(R.layout.fragment_section_list, container, false);
            listView = (ListView) rootView.findViewById(R.id.item_list);

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

        public void showToast(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }

    }
}
