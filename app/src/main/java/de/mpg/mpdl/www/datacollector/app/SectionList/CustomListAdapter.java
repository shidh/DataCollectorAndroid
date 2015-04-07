package de.mpg.mpdl.www.datacollector.app.SectionList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.R;

/**
 * Created by allen on 06/04/15.
 */
public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<DataItem> dataItems;
    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<DataItem> dataItems) {
        this.activity = activity;
        this.dataItems = dataItems;
    }

    @Override
    public int getCount() {
        return dataItems.size();
    }

    @Override
    public Object getItem(int location) {
        return dataItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item_forecast, null);

        //if (imageLoader == null)
        //    imageLoader = AppController.getInstance().getImageLoader();
        // NetworkImageView thumbNail = (NetworkImageView) convertView
        //         .findViewById(R.id.thumbnail);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.list_item_forecast_textview);
        TextView artist = (TextView) convertView.findViewById(R.id.artist);
        TextView date = (TextView) convertView.findViewById(R.id.date);

        // getting item data for the row
        DataItem m = dataItems.get(position);

        // thumbnail image
        //thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
        Picasso.with(activity)
                .load(m.getThumbnailUrl())
                .into(imageView);

        // title
        title.setText(m.getId());

        // user
        //artist.setText(m.getCreatedBy().getFamilyName());
        artist.setText(m.getFilename());

        // date
        date.setText(String.valueOf(m.getCreatedDate()));

        return convertView;
    }

}