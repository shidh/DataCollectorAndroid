package de.mpg.mpdl.www.datacollector.app.SectionList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.Model.ImejiCollection;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.SectionList.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class CollectionListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private List<ImejiCollection> dataList = new ArrayList<ImejiCollection>();
    private final String LOG_TAG = ItemListFragment.class.getSimpleName();



    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    View rootView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private CollectionSwipeAdapter adapter;

    // TODO: Rename and change types of parameters
    public static CollectionListFragment newInstance() {
        CollectionListFragment fragment = new CollectionListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
        Log.v(LOG_TAG, "start onCreate~~~");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataList = new Select()
                .from(ImejiCollection.class)
                .execute();
        adapter = new CollectionSwipeAdapter(getActivity(), dataList);

        //TODO try to change the cell view
        rootView = inflater.inflate(R.layout.fragment_collection, container, false);
        //rootView = inflater.inflate(R.layout.fragment_section_list_swipe, container, false);
        //delete = (Button) rootView.findViewById(R.id.delete);
        mListView = (ListView) rootView.findViewById(android.R.id.list);
        //listView = (SwipeMenuListView) rootView.findViewById(R.id.listView);
        mListView.setAdapter(adapter);


        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return rootView;
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
        Log.v(LOG_TAG, "start onDestroy~~~");
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.v(LOG_TAG, "onAttach");

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
            //TODO updateCollection();
            // /updateDataItem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);


            DataItem dataItem = (DataItem) adapter.getItem(position);
            //Context context = getActivity();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getActivity(), dataItem.getCollectionId(), duration);
            toast.show();

//            Intent showDetailIntent = new Intent(getActivity(), DetailActivity.class);
//            showDetailIntent.putExtra(Intent.EXTRA_TEXT, dataItem.getFilename());
//
//            showDetailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//            startActivity(showDetailIntent);

        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
