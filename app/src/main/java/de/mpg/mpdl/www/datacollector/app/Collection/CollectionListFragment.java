package de.mpg.mpdl.www.datacollector.app.Collection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.CollectionLocal;
import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.Model.MetaDataLocal;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.Retrofit.MetaDataConverter;
import de.mpg.mpdl.www.datacollector.app.Retrofit.RetrofitClient;
import de.mpg.mpdl.www.datacollector.app.utils.DeviceStatus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A fragment representing a list of Collections.
 */
public class CollectionListFragment extends Fragment{
        //implements AbsListView.OnItemClickListener {
    private List<CollectionLocal> collectionListLocal = new ArrayList<CollectionLocal>();
    private CollectionLocal collectionLocal = new CollectionLocal();
    private final String LOG_TAG = CollectionListFragment.class.getSimpleName();
    private ProgressDialog pDialog;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    private View rootView;
    private String collectionID = DeviceStatus.collectionID;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private CollectionGridAdaptor adapter;
    private SharedPreferences mPrefs;
    private String username;
    private String password;

    private static Gson gson = new GsonBuilder()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    Callback<List<CollectionLocal>> callback = new Callback<List<CollectionLocal>>() {
        @Override
        public void success(List<CollectionLocal> dataList, Response response) {
            //load all data from imeji
            //adapter =  new CustomListAdapter(getActivity(), dataList);

            ActiveAndroid.beginTransaction();
            try {
                collectionListLocal.clear();
                for(CollectionLocal collection : dataList){
//                    Log.v(LOG_TAG, "collection title: " + String.valueOf(collection.getTitle()));

//                    if (collection.id.equals(collectionID)) {
//                        getCollectionItems(collection.id);
//                        collectionLocal = collection;
//                    }
                    getCollectionItems(collection.id);
                    collectionLocal = collection;

                    collectionListLocal.add(collection);
                    collection.save();

                }
                ActiveAndroid.setTransactionSuccessful();
            } finally{
                ActiveAndroid.endTransaction();
                //load local data only
                //adapter =  new CustomListAdapter(getActivity(), dataListLocal);


            }

//            Log.v(LOG_TAG, "get list OK");
            if(pDialog != null) {
                pDialog.hide();
            }
        }

        @Override
        public void failure(RetrofitError error) {
//            Log.v(LOG_TAG, "get list failed");
            Log.v(LOG_TAG, error.toString());
//            showToast("update data failed");
        }
    };

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
                    if (item.getCollectionId().equals(collectionID)) {

                        MetaDataLocal metaDataLocal = MetaDataConverter.
                                metaDataToMetaDataLocal(item.getMetadata());
                        //metaDataLocal.save();
                        item.setMetaDataLocal(metaDataLocal);
                        dataListLocal.add(item);
                    }else{
                        dataListLocal.add(item);
                    }

                }
                ActiveAndroid.setTransactionSuccessful();
            } finally{
                ActiveAndroid.endTransaction();

                collectionLocal.setItems(dataListLocal);
                //collectionLocal.save();

                adapter.notifyDataSetChanged();

            }

//            Log.v(LOG_TAG, "get list OK");

        }

        @Override
        public void failure(RetrofitError error) {
//            Log.v(LOG_TAG, "get list failed");
//            Log.v(LOG_TAG, error.toString());
            showToast("update data failed");
        }
    };
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CollectionListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPrefs = getActivity().getSharedPreferences("myPref", 0);
        username = mPrefs.getString("username","");
        password = mPrefs.getString("password","");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        collectionListLocal = new Select()
                .from(CollectionLocal.class)
                .execute();
//        Log.v(LOG_TAG,"size: "+ collectionListLocal.size()+"");


        //TODO try to change the cell view
        rootView = inflater.inflate(R.layout.fragment_collection_grid, container, false);
        //rootView = inflater.inflate(R.layout.fragment_section_list_swipe, container, false);
        //delete = (Button) rootView.findViewById(R.id.delete);
        mListView = (StaggeredGridView) rootView.findViewById(R.id.collection_list);
        //listView = (SwipeMenuListView) rootView.findViewById(R.id.listView);
        adapter = new CollectionGridAdaptor(getActivity(), collectionListLocal);
        mListView.setAdapter(adapter);


        // Set OnItemClickListener so we can be notified on item clicks
        //mListView.setOnItemClickListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CollectionLocal dataCollection = (CollectionLocal) adapter.getItem(position);
                Intent showItemsIntent = new Intent(getActivity(), CollectionDetailActivity.class);
                showItemsIntent.putExtra(Intent.EXTRA_TEXT, dataCollection.id);

                startActivity(showItemsIntent);
            }
        });

        return rootView;
    }



    @Override
    public void onStart() {
        super.onStart();
        updateCollection();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

            updateCollection();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void updateCollection(){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        RetrofitClient.getCollections(callback, username, password);
    }

    private void getCollectionItems(String collectionId){
        RetrofitClient.getCollectionItems(collectionId, callbackItems, username, password);
    }



    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(String id);
//    }



}
