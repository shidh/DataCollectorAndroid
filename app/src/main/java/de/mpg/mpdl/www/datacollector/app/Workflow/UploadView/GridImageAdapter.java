package de.mpg.mpdl.www.datacollector.app.Workflow.UploadView;

import android.content.Context;
import android.graphics.Point;
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

import java.io.File;
import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.R;

/**
 * Created by allen on 02/05/15.
 */
public class GridImageAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<DataItem> dataItems;

    public GridImageAdapter(Context c, List<DataItem> dataItems) {
        this.mContext = c;
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

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        //Log.v(size.x/2+" ",size.y/2+"");

        if(convertView==null){
            grid = new View(mContext);
            LayoutInflater inflater= (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.upload_item_cell, parent, false);
        }else{
            grid = convertView;
        }




        ImageView imageView = (ImageView) grid.findViewById(R.id.upload_item_cell_thumbnail);
        TextView title = (TextView) grid.findViewById(R.id.upload_item_cell_title);
        //TextView date = (TextView) grid.findViewById(R.id.upload_item_cell_date);


        // getting item data for the row
        DataItem m = dataItems.get(position);

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(size.x/2, size.y/2));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }



        File imgFile = new File(m.getLocalPath());
        Picasso.with(mContext)
                .load(imgFile)
                .resize(size.x/2, imageView.getHeight())
                .into(imageView);

        title.setText(m.getMetaDataLocal().getTitle());

        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //date.setText(dateFormat.format(new Date()));
        return grid;

    }
}
