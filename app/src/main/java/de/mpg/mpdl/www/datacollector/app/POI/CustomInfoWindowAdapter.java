package de.mpg.mpdl.www.datacollector.app.POI;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import de.mpg.mpdl.www.datacollector.app.R;

/**
 * Created by allen on 11/08/15.
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
    // "title" and "snippet".
    private final View mWindow;
    //private final View mContents;
    private final Activity activity;

    CustomInfoWindowAdapter(Activity activity) {
        this.activity = activity;
        mWindow = activity.getLayoutInflater().inflate(R.layout.poi_custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, mWindow );
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void render(Marker marker, View view) {

        final ImageView badgeUi = (ImageView) view.findViewById(R.id.info_window_badge);
        TextView titleUi = ((TextView) view.findViewById(R.id.info_window_title));

        String snippet = marker.getSnippet();
        Picasso.with(activity)
                .load(snippet)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.error)
                .resize(360, 360)
                .centerCrop()
                        //.into(badgeUi);
                .into(new Target() {
                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                              badgeUi.setImageBitmap(bitmap);
                          }

                          @Override
                          public void onBitmapFailed(final Drawable errorDrawable) {
                              badgeUi.setImageDrawable(activity.getResources().getDrawable(R.drawable.error));
                          }

                          @Override
                          public void onPrepareLoad(final Drawable placeHolderDrawable) {
                              badgeUi.setImageDrawable(activity.getResources().getDrawable(R.drawable.progress_animation));
                          }
                      }
                );



        String title = marker.getTitle();
        if (title != null) {
            // Spannable string allows us to edit the formatting of the text.
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.blue)), 0, titleText.length(), 0);
            titleUi.setText(titleText);
        } else {
            titleUi.setText("");
        }

    }
}


