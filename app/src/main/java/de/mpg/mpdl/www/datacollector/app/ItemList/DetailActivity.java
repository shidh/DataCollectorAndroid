package de.mpg.mpdl.www.datacollector.app.ItemList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
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
import uk.co.senab.photoview.PhotoViewAttacher;


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

        private String itemName;
        private PhotoViewAttacher mAttacher;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);
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

                mAttacher = new PhotoViewAttacher(imageView);
                //mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //mAttacher.setScaleType(ImageView.ScaleType.CENTER);
                //mAttacher.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

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
                        .resize(size.x, size.y - 150)
                        //.placeholder(R.drawable.progress_animation)
                        .centerCrop()
                        .into(imageView);


                //mAttacher.update();


                //accuracy.setText(item.getMetaDataLocal().getAccuracy()+"");
                //user.setText(item.getMetaDataLocal().getCreator());

            }

            return rootView;



        }
    }
}