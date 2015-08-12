package de.mpg.mpdl.www.datacollector.app.Workflow.UploadView;

import android.content.Context;
import android.graphics.Bitmap;
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

        Log.v(size.x / 2 + " ", size.y / 2 + "");

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
        final DataItem m = dataItems.get(position);

        if(size.x > size.y){
            grid.setLayoutParams(new GridView.LayoutParams(size.x/2, size.y*2/3));
        }else{
            grid.setLayoutParams(new GridView.LayoutParams(size.x/2, size.y/3));
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Log.v("localPath: ",m.getLocalPath());
        final String filePath = m.getLocalPath();
        File itemFile = new File(filePath);
//        Picasso.with(mContext)
//                .load(imgFile)
//                .resize(imageView.getWidth(),imageView.getHeight())
//                //.resize(size.x/2-10, size.y/2-10)
//                .into(imageView);

        if(itemFile.exists()) {
            if(m.getMetaDataLocal() != null) {
                if (m.getMetaDataLocal().getType().equals("image")
                        || m.getMetaDataLocal().getType().equals("video")) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    Bitmap myBitmap = BitmapFactory.decodeFile(m.getLocalPath(), options);

                    options.inSampleSize = calculateInSampleSize(options, 100, 100);
                    options.inJustDecodeBounds = false;

                    myBitmap = BitmapFactory.decodeFile(filePath, options);
                    imageView.setImageBitmap(myBitmap);
                } else if (m.getMetaDataLocal().getType().equals("audio")) {
                    imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play));
                }
                title.setText(m.getMetaDataLocal().getTitle());
            }
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
