package de.mpg.mpdl.www.datacollector.app.SectionList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.R;
import de.mpg.mpdl.www.datacollector.app.SettingsActivity;


//using import android.app.Fragment;
public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String FORECAST_SHARE_HASHTAG = " #DataCollectorApp";
        private String itemName;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Get the provider and hold onto it to set/change the share intent.
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // Attach an intent to this ShareActionProvider.  You can update this at any time,
            // like when the user selects a new piece of data they might like to share.
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
                } else {
                    Log.d(LOG_TAG, "Share Action Provider is null?");
                }
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    itemName + FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                itemName = intent.getStringExtra(Intent.EXTRA_TEXT);
                ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_image);

                //TextView title = (TextView) rootView.findViewById(R.id.detail_title);
                //TextView accuracy = (TextView) rootView.findViewById(R.id.detail_title);
                //TextView user = (TextView) rootView.findViewById(R.id.detail_author);

                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();

                Point size = new Point();
                display.getSize(size);

                Log.v(size.x / 2 + " ", size.y / 2 + "");

                DataItem item = new Select().
                                from(DataItem.class).
                                where("filename = ?", itemName).executeSingle();

                Log.v(LOG_TAG, item.getWebResolutionUrlUrl());
                Log.v(LOG_TAG, item.getMetaDataLocal().getAddress());
                Log.v(LOG_TAG, String.valueOf(item.getMetaDataLocal().getAccuracy()));
                //Log.v(LOG_TAG, String.valueOf(item.getMetaDataLocal().getTags().get(0)));
                //title.setText(item.getMetaDataLocal().getAddress());

                Picasso.with(getActivity())
                        .load(item.getWebResolutionUrlUrl())
                        .resize(size.x, size.y-150)
                        .centerCrop()
                        .into(imageView);
                //accuracy.setText(item.getMetaDataLocal().getAccuracy()+"");
                //user.setText(item.getMetaDataLocal().getCreator());

            }

            return rootView;



        }
    }
}
