package de.mpg.mpdl.www.datacollector.app.AsyncTask;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.otto.Produce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.mpg.mpdl.www.datacollector.app.Event.GetAddressEvent;
import de.mpg.mpdl.www.datacollector.app.Event.OttoSingleton;

/**
 * Created by allen on 26/04/15.
 */

/*AsyncTask<String, Void, String[]>
  String:   as the input， in our case is latlng
  Void:     is the percentage of the background job， can be called onProgressUpdate() in Main UI
  String[]: as the output result
  *
  */
public class GetAddressByCoordinatesTask extends AsyncTask<Double, Void, String> {

    private final String LOG_TAG = GetAddressByCoordinatesTask.class.getSimpleName();
    private final String GOOGLE_API = "https://maps.googleapis.com/maps/api/geocode/json?";

    @Override
    protected String doInBackground(Double... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }

        // Will contain the raw JSON response as a string.
        String googleAddressJsonStr = null;


        try {
            // Construct the URL for the GoogleMap query
            final String GOOGLEAPI_BASE_URL = GOOGLE_API;

            final String QUERY_PARAM = "latlng";

            //latlng=48.147899,11.57648
            Uri builtUri = Uri.parse(GOOGLEAPI_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0]+","+params[1])
                    .build();

            URL url = new URL(builtUri.toString());

            //Log.v(LOG_TAG, "Built URI " + builtUri.toString());
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            googleAddressJsonStr = buffer.toString();
            //Log.v(LOG_TAG, "Json String is: "+googleAddressJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        //parse the Json String
        try {
            return getAddressFromJson(googleAddressJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @Produce
    protected void onPostExecute(String result) {
        if (result != null) {
            //mForecastAdapter.clear();
            Log.v(LOG_TAG, result);

            GetAddressEvent event = new GetAddressEvent(result);
            OttoSingleton.getInstance().post(event);
        }
    }


    private String getAddressFromJson(String googleAddressJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "results";
        final String ADDRESS = "formatted_address";
        JSONObject addressJson = new JSONObject(googleAddressJsonStr);
        JSONArray addressArray = addressJson.getJSONArray(OWM_LIST);

        JSONObject firstAddressResult = addressArray.getJSONObject(0);

        String formatted_address = firstAddressResult.getString(ADDRESS);
        //Log.v(LOG_TAG, formatted_address);
        return formatted_address;
    }

}
