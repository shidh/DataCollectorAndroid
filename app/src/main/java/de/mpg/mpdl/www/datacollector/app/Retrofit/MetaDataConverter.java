package de.mpg.mpdl.www.datacollector.app.Retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.Imeji.GenericValue;
import de.mpg.mpdl.www.datacollector.app.Model.Imeji.GeoLocationImeji;
import de.mpg.mpdl.www.datacollector.app.Model.Imeji.LabelsImeji;
import de.mpg.mpdl.www.datacollector.app.Model.Imeji.MetaData;
import de.mpg.mpdl.www.datacollector.app.Model.MetaDataLocal;

/**
 * Created by allen on 28/04/15.
 */
public class MetaDataConverter {
    private static final String LOG_TAG = MetaDataConverter.class.getSimpleName();
    private static Gson gson = new GsonBuilder()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static MetaDataLocal metaDataToMetaDataLocal(List<MetaData> metaDataList){
        MetaDataLocal metaDataLocal = new MetaDataLocal();
        List<String> tags = new ArrayList<String>();
        // here get the string of Metadata Json
        String json = gson.toJson(metaDataList);
        try {
            JSONArray jsonArray = new JSONArray(json);

            for(int i = 0; i < jsonArray.length(); i++ ){
                JSONObject meta = jsonArray.getJSONObject(i);
                //String type = meta.getString("typeUri").split("#")[1];
                String label = meta.getString("labels").split("\"")[3];
                //String statementUri = meta.getString("statementUri");

                if(label.equals("title")){
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setTitle(value.getString("text"));
                } else if(label.equals("author")){
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setCreator(value.getString("text"));
                } else if(label.equals("accuracy")){
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setAccuracy(value.getDouble("number"));

                } else if(label.equals("deviceID")){
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setDeviceID(value.getString("text"));
                } else if(label.equals("location")){
                    //"value":{"name":"Amalienstr. 33 D-80799 München","longitude":11.57648,"latitude":48.147899}
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setAddress(value.getString("name"));
                    metaDataLocal.setLatitude(value.getDouble("latitude"));
                    metaDataLocal.setLongitude(value.getDouble("longitude"));
                } else if(label.equals("tags")){
                    JSONObject value = (JSONObject) meta.get("value");
                    tags.add(value.getString("text"));
                }
            }
            metaDataLocal.setTags(tags);
            //metaDataLocal.save();
            //dataItem.setMetaDataLocal(metaDataLocal);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            Log.v(LOG_TAG, gson.toJson(metaDataLocal));
            return metaDataLocal;
        }
    }


    public static List<MetaData> metaDataLocalToMetaDataList(MetaDataLocal metaDataLocal){
        List<MetaData> metaDataList = new ArrayList<MetaData>();

        Log.v(LOG_TAG + metaDataLocal, gson.toJson(metaDataLocal));


        //for title
        MetaData m1 = new MetaData();
        GenericValue<String> titleValue = new GenericValue<String>(metaDataLocal.getTitle());
        //m.setValue(new TextImeji(metaDataLocal.getTitle()));
        m1.setLabels(new LabelsImeji("en", "title"));
        m1.setTypeUri("http://imeji.org/terms/metadata#text");
        m1.setStatementUri("http://dev-faces.mpdl.mpg.de/imeji/statement/8Di1zD7GzAGIgUaY");
        m1.setValue(titleValue);
        metaDataList.add(m1);

        //for creator
        MetaData m2 = new MetaData();
        GenericValue<String> creatorValue = new GenericValue<String>(metaDataLocal.getCreator());
        m2.setValue(creatorValue);
        //m.setValue(new TextImeji(metaDataLocal.getCreator()));
        m2.setStatementUri("http://dev-faces.mpdl.mpg.de/imeji/statement/B7bSBqhGySbxCM8Q");
        m2.setTypeUri("http://imeji.org/terms/metadata#text");
        m2.setLabels(new LabelsImeji("en", "author"));
        metaDataList.add(m2);

        //for location
        MetaData m3 = new MetaData();
        GeoLocationImeji geo = new GeoLocationImeji(metaDataLocal.getAddress(),
                metaDataLocal.getLatitude(), metaDataLocal.getLongitude());
        GenericValue<GeoLocationImeji> geoValue = new GenericValue<GeoLocationImeji>(geo);
        m3.setValue(geoValue);
        //m.setValue(geo);
        m3.setStatementUri("http://dev-faces.mpdl.mpg.de/imeji/statement/2_FHehws3iKrRDAX");
        m3.setTypeUri("http://imeji.org/terms/metadata#geolocation");
        m3.setLabels(new LabelsImeji("en", "location"));
        metaDataList.add(m3);

        //for accuracy
        MetaData m4 = new MetaData();
        GenericValue<Double> accuracyValue = new GenericValue<Double>(metaDataLocal.getAccuracy());
        m4.setValue(accuracyValue);
        //m.setValue(new NumberImeji(metaDataLocal.getAccuracy()));
        m4.setStatementUri("http://dev-faces.mpdl.mpg.de/imeji/statement/reIk3qTBpcOxyjKb");
        m4.setTypeUri("http://imeji.org/terms/metadata#number");
        m4.setLabels(new LabelsImeji("en", "accuracy"));
        metaDataList.add(m4);

        //for deviceID
        MetaData m5 = new MetaData();
        GenericValue<String> deviceValue = new GenericValue<String>(metaDataLocal.getDeviceID());
        m5.setValue(deviceValue);
        //m.setValue(new TextImeji(metaDataLocal.getDeviceID()));
        m5.setStatementUri("http://dev-faces.mpdl.mpg.de/imeji/statement/IqPfT4dfewwmB2aN");
        m5.setTypeUri("http://imeji.org/terms/metadata#text");
        m5.setLabels(new LabelsImeji("en", "deviceID"));
        metaDataList.add(m5);

        //for tags
        for(String tag : metaDataLocal.getTags() ) {
            MetaData mTag = new MetaData();
            GenericValue<String> tagValue = new GenericValue<String>(tag);
            mTag.setValue(tagValue);

            //m.setValue(new TextImeji(tag));
            mTag.setStatementUri("http://dev-faces.mpdl.mpg.de/imeji/statement/bWK8zwhEtNL259a");
            mTag.setTypeUri("http://imeji.org/terms/metadata#text");
            mTag.setLabels(new LabelsImeji("en", "tags"));
            metaDataList.add(mTag);
        }
        Log.v(LOG_TAG, gson.toJson(metaDataList));
        return metaDataList;
    }

}
