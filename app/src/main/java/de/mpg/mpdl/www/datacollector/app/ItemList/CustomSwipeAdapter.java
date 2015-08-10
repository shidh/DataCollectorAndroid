package de.mpg.mpdl.www.datacollector.app.ItemList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.R;

/**
 * Created by allen on 29/05/15.
 */
public class CustomSwipeAdapter extends BaseSwipeAdapter {
    private Activity activity;
    private List<DataItem> dataItems;

    public CustomSwipeAdapter(Activity activity, List<DataItem> dataItems) {
        this.activity = activity;
        this.dataItems = dataItems;
    }

    //return the `SwipeLayout` resource id in your listview | gridview item layout.
    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    //render a new item layout.
    @Override
    public View generateView(int position, ViewGroup parent) {
        View v = LayoutInflater.from(activity).inflate(R.layout.list_swipe_view_item, null);
        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.delete));
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(activity, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

        //bottom view of the cell
        v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "delete clicked", Toast.LENGTH_SHORT).show();
                //TODO
                //do something to delete the dataItems based on position and refresh the listview
            }
        });

        v.findViewById(R.id.star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "star clicked", Toast.LENGTH_SHORT).show();
            }
        });

        v.findViewById(R.id.magnifier).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "search clicked", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    /*fill values to your item layout returned from `generateView`.
      The position param here is passed from the BaseAdapter's 'getView()*/
    @Override
    public void fillValues(int position, View convertView) {
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_cell_thumbnail_swipe);
        TextView title = (TextView) convertView.findViewById(R.id.list_item_cell_title_swipe);
        TextView fileName = (TextView) convertView.findViewById(R.id.list_item_user_swipe);
        TextView date = (TextView) convertView.findViewById(R.id.list_item_cell_date_swipe);

        if(dataItems.size()>0) {
            // getting item data for the row
            DataItem m = dataItems.get(position);

            // thumbnail image
            Picasso.with(activity)
                    .load(m.getThumbnailUrl())
                    .into(imageView);
            //Log.v("getThumbnailUrl ",m.getThumbnailUrl());

            // title
            title.setText(m.getMetaDataLocal().getTitle());

            // user
            //artist.setText(m.getCreatedBy().getFamilyName());
            fileName.setText(m.getMetaDataLocal().getCreator());

            // date
            date.setText(String.valueOf(m.getCreatedDate()).split("\\+")[0]);
        }
    }

    @Override
    public int getCount() {
        return dataItems.size();
    }

    @Override
    public Object getItem(int position) {
        return dataItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
