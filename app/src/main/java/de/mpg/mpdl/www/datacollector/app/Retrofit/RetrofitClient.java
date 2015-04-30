package de.mpg.mpdl.www.datacollector.app.Retrofit;

import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import retrofit.Callback;
import retrofit.mime.TypedFile;

/**
 * Created by allen on 07/04/15.
 */
public class RetrofitClient {
    private final String LOG_TAG = RetrofitClient.class.getSimpleName();
    private static final String REST_SERVER = "https://dev-faces.mpdl.mpg.de/imeji/rest/";

    public static void getItems(Callback<List<DataItem>> callback) {
        // Create a very simple REST adapter which points the GitHub API
        // endpoint.

        ImejiAPI imejiAPI = ServiceGenerator.createService(ImejiAPI.class, REST_SERVER);

        // Fetch and print a list of the items to this library.
        imejiAPI.getItems(callback);
    }

    public static void login(String username, String password){
        ImejiAPI imejiAPI = ServiceGenerator.
                createService(ImejiAPI.class, REST_SERVER, username, password);
        imejiAPI.basicLogin();
    }

//    public static void uploadItem(Callback<DataItem> callback,
//                                  String username,
//                                  String password,
//                                  DataItem item) {
//        ImejiAPI imejiAPI = ServiceGenerator.
//                createService(ImejiAPI.class, REST_SERVER, username, password);
//        imejiAPI.postItem(item, callback);
//    }
    public static void uploadItem(TypedFile typedFile,
                                  String json,
                                  Callback<DataItem> callback,
                                  String username,
                                  String password) {
        ImejiAPI imejiAPI = ServiceGenerator.
            createService(ImejiAPI.class, REST_SERVER, username, password);
        imejiAPI.postItem(typedFile, json, callback);
}

    public static void createPOI(TypedFile typedFile,
                                  String json,
                                  Callback<DataItem> callback,
                                  String username,
                                  String password) {
        ImejiAPI imejiAPI = ServiceGenerator.
                createService(ImejiAPI.class, REST_SERVER, username, password);
        //imejiAPI.postItem(typedFile, json, callback);
    }


}
