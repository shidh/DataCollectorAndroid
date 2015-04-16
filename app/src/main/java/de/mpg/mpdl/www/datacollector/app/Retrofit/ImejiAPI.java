package de.mpg.mpdl.www.datacollector.app.Retrofit;

import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;
import de.mpg.mpdl.www.datacollector.app.Model.User;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Created by allen on 01/04/15.
 */
public interface ImejiAPI {
    //You can use rx.java for sophisticated composition of requests
    //@GET("/users/{user}")
    //public Observable<User> fetchUser(@Path("user") String user);

    //or you can just get your model if you use json api
    @GET("/users/{user}")
    public User fetchUser(@Path("user") String user);

    //or if there are some special cases you can process your response manually
    //@GET("/users/{user}")
    //public Response fetchUsers(@Path("user") String user);

    public static final String BASE_URL = "https://api.yourdomain.com";

    @POST("/login")
    User basicLogin();

    //get all items
    //http://dev-faces.mpdl.mpg.de/imeji/rest/items
    @GET("/items")
    List<DataItem> getItems();

    @GET("/items")
    void getItems(Callback<List<DataItem>> callback);

    //get one item by itemId
    @GET("/items/{itemId}")
    List<DataItem> getItemById(@Path("itemId") String itemId, Callback<Response> callback);

    //@POST("/items")
    //void postItem(@Body DataItem item, Callback<DataItem> callback);

    @Multipart
    @POST("/items")
    void postItem(@Part("file") TypedFile file,
                  @Part("json") String json,
                  Callback<String> cb);

    //get all users
    @GET("/users")
    void getUsers(Callback<List<User>> callback);

    //get one User by userId
    //http://dev-faces.mpdl.mpg.de/imeji/rest/users/ju1rYDIm1EFE1f5
    @GET("/users/{userId}")
    List<User> getUserById(@Path("userId") String userId, Callback<Response> callback);





}
