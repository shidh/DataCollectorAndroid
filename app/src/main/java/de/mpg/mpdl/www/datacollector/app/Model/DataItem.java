package de.mpg.mpdl.www.datacollector.app.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by allen on 01/04/15.
 */
@Table(name = "DataItem")
public class DataItem extends Model {
//    @Expose
//    @Column(name = "DataId")
//    private String id;

    @Expose
    @Column(name = "filename")
    private String filename;

    @Expose
    @Column(name = "createdDate")
    private String createdDate;

    @Expose
    @Column(name = "fileUrl")
    private String fileUrl;

    @Expose
    @Column(name = "webResolutionUrlUrl")
    private String webResolutionUrlUrl;

    @Expose
    @Column(name = "thumbnailUrl")
    private String thumbnailUrl;

    @Expose
    @Column(name = "createdBy")
    private User createdBy;

    @Expose
    private ArrayList<MetaData> metadata;

    @Column(name = "metaData")
    private MetaDataLocal metaDataLocal;

    @Expose
    @Column(name = "collectionId")
    private String collectionId;


    @Column(name = "poi")
    private POI poi;

    public DataItem(String filename, String createdDate, String fileUrl, String webResolutionUrlUrl,
                    String thumbnailUrl, User createdBy, ArrayList<MetaData> metadata,
                    String collectionId) {
        this.filename = filename;
        this.createdDate = createdDate;
        this.fileUrl = fileUrl;
        this.webResolutionUrlUrl = webResolutionUrlUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.createdBy = createdBy;
        this.metadata = metadata;
        this.collectionId = collectionId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getWebResolutionUrlUrl() {
        return webResolutionUrlUrl;
    }

    public void setWebResolutionUrlUrl(String webResolutionUrlUrl) {
        this.webResolutionUrlUrl = webResolutionUrlUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public ArrayList<MetaData> getMetadata() {
        return metadata;
    }

    public void setMetadata(ArrayList<MetaData> metadata) {
        this.metadata = metadata;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public MetaDataLocal getMetaDataLocal() {
        return metaDataLocal;
    }

    public void setMetaDataLocal(MetaDataLocal metaDataLocal) {
        this.metaDataLocal = metaDataLocal;
    }


    private void convertMetaData(){
        // here get the string of Metadata Json
        Gson gson = new Gson();
        String json = gson.toJson(this.getMetadata());
        try {
            //Json String to Object
            //JSONObject object = (JSONObject) new JSONTokener(json).nextValue();

            JSONArray jsonArray = new JSONArray(json);

            //String query = object.getString("query");
            //JSONArray locations = object.getJSONArray("locations");
            JSONObject meta = jsonArray.getJSONObject(0);
            //for(){}
            String type = meta.getString("typeUri").split("#")[1];
            String label = meta.getString("labels").split("\"")[3];
            String statementUri = meta.getString("statementUri");

            if(type == "geolocation") {
                GeoLocation value = (GeoLocation) meta.get("value");
                //Log.v(LOG_TAG, value.toString());

                //solution
                Type fooType = new TypeToken<GenericValue<GeoLocation>>() {
                }.getType();
                gson.toJson(value, fooType);
                //gson.fromJson(json, fooType);
            }else if(type == "number") {
                Double value = (Double) meta.get("value");
                //solution
                Type fooType = new TypeToken<GenericValue<Double>>() {
                }.getType();
                gson.toJson(value, fooType);
                //gson.fromJson(json, fooType);
            }else{
                JSONObject value = (JSONObject) meta.get("value");

//                    Type fooType = new TypeToken<GenericValue<String>>() {
//                    }.getType();
//                    gson.toJson(value, fooType);
                //gson.toJson(value);

                //Log.v(LOG_TAG, gson.toJson(value, fooType));
                //gson.fromJson(json, fooType);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
