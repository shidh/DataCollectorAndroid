package de.mpg.mpdl.www.datacollector.app.Collection;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.CollectionLocal;
import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.R;

/**
 * Created by allen on 12/06/15.
 */
public class CollectionGridAdaptor extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<CollectionLocal> dataItems;

    public CollectionGridAdaptor(Activity c, List<CollectionLocal> dataItems) {
        this.activity = c;
        this.dataItems = dataItems;
    }

    public int getCount() {
        return dataItems.size();
    }

    public Object getItem(int position) {
        return dataItems.get(position);
        //return null;
    }

    public long getItemId(int position) {
        return position;
        //return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        Log.v(size.x / 2 + " ", size.y / 2 + "");

        if(convertView==null){
            grid = new View(activity);
            LayoutInflater inflater= (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.collection_item_cell, parent, false);
        }else{
            grid = convertView;
        }


        ImageView imageView = (ImageView) grid.findViewById(R.id.collection_first_item);
        TextView title = (TextView) grid.findViewById(R.id.collection_title);
        //TextView date = (TextView) grid.findViewById(R.id.upload_item_cell_date);
        ImageView imageView1 = (ImageView) grid.findViewById(R.id.collection_first_item1);
        ImageView imageView2 = (ImageView) grid.findViewById(R.id.collection_first_item2);
        ImageView imageView3 = (ImageView) grid.findViewById(R.id.collection_first_item3);
        ImageView imageView4 = (ImageView) grid.findViewById(R.id.collection_first_item4);
        ImageView imageView5 = (ImageView) grid.findViewById(R.id.collection_first_item5);

        if (size.x > size.y) {
            grid.setLayoutParams(new GridView.LayoutParams(size.x / 2, 220));
        } else {
            grid.setLayoutParams(new GridView.LayoutParams(size.x / 2, 220));
        }

        if(dataItems.size()>0) {
            // getting item data for the row
            CollectionLocal collection = dataItems.get(position);
            Log.v("Adaptor##", collection.getTitle());

            if(collection.getItems()!=null) {



                //TODO get the first or last item m from collection
                if(collection.getItems().size()>0) {
                    DataItem m = collection.getItems().get(0);
                    Log.v("Adaptor##", m.getThumbnailUrl());

                    //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Picasso.with(activity)
                            .load(m.getThumbnailUrl())
                                    //.resize(imageView.getWidth(), imageView.getHeight())
                                    //.resize(size.x/2-10, size.y/2-10)
                            .into(imageView);

                    if(collection.getItems().size()>1) {
                        DataItem m1 = collection.getItems().get(1);
                        Picasso.with(activity)
                                .load(m1.getThumbnailUrl())
                                .into(imageView1);
                    }if(collection.getItems().size()>2) {
                        DataItem m2 = collection.getItems().get(2);
                        Picasso.with(activity)
                                .load(m2.getThumbnailUrl())
                                .into(imageView2);
                    }if(collection.getItems().size()>3) {
                        DataItem m3 = collection.getItems().get(3);
                        Picasso.with(activity)
                                .load(m3.getThumbnailUrl())
                                .into(imageView3);
                    }if(collection.getItems().size()>4) {
                        DataItem m4 = collection.getItems().get(4);
                        Picasso.with(activity)
                                .load(m4.getThumbnailUrl())
                                .into(imageView4);
                    }if(collection.getItems().size()>5) {
                        DataItem m5 = collection.getItems().get(5);
                        Picasso.with(activity)
                                .load(m5.getThumbnailUrl())
                                .into(imageView5);
                    }

                }
            }
            title.setText(collection.getTitle());

        }
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //date.setText(dateFormat.format(new Date()));
        return grid;

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
