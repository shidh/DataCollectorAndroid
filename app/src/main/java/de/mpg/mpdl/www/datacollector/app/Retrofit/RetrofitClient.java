package de.mpg.mpdl.www.datacollector.app.Retrofit;

import com.squareup.okhttp.OkHttpClient;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by allen on 07/04/15.
 */
public class RetrofitClient {
    private final String LOG_TAG = RetrofitClient.class.getSimpleName();
    private static final String REST_SERVER = "http://dev-faces.mpdl.mpg.de/imeji/rest/";

    public static void getItems(Callback<List<DataItem>> callback) {
        // Create a very simple REST adapter which points the GitHub API
        // endpoint.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setWriteTimeout(3000, TimeUnit.MILLISECONDS);
        OkClient okClient = new OkClient(okHttpClient);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(okClient)
                .setEndpoint(REST_SERVER)
                .build();

        // Create an instance of our imeji API interface.
        ImejiAPI imejiAPI = restAdapter.create(ImejiAPI.class);

        // Fetch and print a list of the items to this library.
        imejiAPI.getItems(callback);
    }





}
