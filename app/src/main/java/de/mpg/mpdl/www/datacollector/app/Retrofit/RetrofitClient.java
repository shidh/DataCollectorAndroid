package de.mpg.mpdl.www.datacollector.app.Retrofit;

import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.CollectionLocal;
import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.ItemImeji;
import de.mpg.mpdl.www.datacollector.app.Model.POI;
import de.mpg.mpdl.www.datacollector.app.utils.DeviceStatus;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by allen on 07/04/15.
 */
public class RetrofitClient {
    private final String LOG_TAG = RetrofitClient.class.getSimpleName();
    private static final String REST_SERVER = DeviceStatus.BASE_URL;



    public static void login(String username, String password){
        ImejiAPI imejiAPI = ServiceGenerator.
                createService(ImejiAPI.class, REST_SERVER, username, password);
        imejiAPI.basicLogin();
    }


    /*
    for items
     */
    public static void getItems(Callback<List<DataItem>> callback,
                                String username,
                                String password) {
        // Create a very simple REST adapter which points the API end point

        ImejiAPI imejiAPI = ServiceGenerator.createService(ImejiAPI.class, REST_SERVER, username, password);

        // Fetch and print a list of the items to this library.
        imejiAPI.getItems(callback);
    }
    public static void uploadItem(TypedFile typedFile,
                                  String json,
                                  Callback<ItemImeji> callback,
                                  String username,
                                  String password) {
        ImejiAPI imejiAPI = ServiceGenerator.
            createService(ImejiAPI.class, REST_SERVER, username, password);
        imejiAPI.postItem(typedFile, json, callback);
    }


    public static void deleteItem(String itemId,
                                  Callback<Response> callback,
                                  String username,
                                  String password) {
        ImejiAPI imejiAPI = ServiceGenerator.createService(ImejiAPI.class, REST_SERVER,
                username, password);
        imejiAPI.deleteItemById(itemId, callback);
    }



    /*
        for collection
     */
    public static void getCollections(Callback<List<CollectionLocal>> callback,
                                String username,
                                String password) {
        ImejiAPI imejiAPI = ServiceGenerator.createService(ImejiAPI.class, REST_SERVER, username, password);

        // Fetch and print a list of the items to this library.
        imejiAPI.getCollections(callback);
    }

    public static void getCollectionItems(String collectionId,
                                      Callback<List<DataItem>> callback,
                                      String username,
                                      String password) {
        ImejiAPI imejiAPI = ServiceGenerator.createService(ImejiAPI.class, REST_SERVER, username, password);

        // Fetch and print a list of the items to this library.
        imejiAPI.getCollectionItems(collectionId, callback);
    }


    /*
        for Album(POI)
     */
    public static void getPOIs(Callback<List<POI>> callback,
                               String username,
                               String password) {
        ImejiAPI imejiAPI = ServiceGenerator.createService(ImejiAPI.class, REST_SERVER,
                username, password);
        imejiAPI.getPOIs(callback);
    }

    public static void getPOIsByQuery(String query,
                                      Callback<List<POI>> callback,
                                      String username,
                                      String password) {
        ImejiAPI imejiAPI = ServiceGenerator.createService(ImejiAPI.class, REST_SERVER,
                username, password);
        imejiAPI.getPOIsByQuery(query, callback);
    }

    public static void getPOIById(String poiId,
                                  Callback<List<POI>> callback,
                                  String username,
                                  String password,
                                  StringConverter converter) {
        ImejiAPI imejiAPI = ServiceGenerator.createService(ImejiAPI.class, REST_SERVER,
                username, password, converter);
        imejiAPI.getPOIById(poiId, callback);
    }


    public static void createPOI( POI poi,
                                  Callback<POI> callback,
                                  String username,
                                  String password) {
        ImejiAPI imejiAPI = ServiceGenerator.
                createService(ImejiAPI.class, REST_SERVER, username, password);
        imejiAPI.postPOI(poi, callback);
    }


    public static void getPoiMembers(String albumId,
                                     Callback<List<DataItem>> callback,
                                     String username,
                                     String password) {
        ImejiAPI imejiAPI = ServiceGenerator.createService(ImejiAPI.class, REST_SERVER,
                username, password);
        imejiAPI.getPoiMembers(albumId, callback);
    }

    public static void linkItems(String albumId,
                                 TypedString body,
                                 String username,
                                 String password,
                                 Callback<List<String>> callback) {
        ImejiAPI imejiAPI = ServiceGenerator.createService(ImejiAPI.class, REST_SERVER,
                username, password);
        imejiAPI.linkItems(albumId, body, callback);
    }



}
