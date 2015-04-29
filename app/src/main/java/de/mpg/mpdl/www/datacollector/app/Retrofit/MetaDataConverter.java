package de.mpg.mpdl.www.datacollector.app.Retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.GenericValue;
import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.GeoLocationImeji;
import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.LabelsImeji;
import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.MetaData;
import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.MetaDataUpload;
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
        Log.v(LOG_TAG, json);

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


    public static List<MetaDataUpload> metaDataLocalToMetaDataList(MetaDataLocal metaDataLocal){
        List<MetaDataUpload> metaDataList = new ArrayList<MetaDataUpload>();

        Log.v(LOG_TAG + metaDataLocal, gson.toJson(metaDataLocal));


        //for title
        MetaDataUpload m1 = new MetaDataUpload();
        GenericValue<String> titleValue = new GenericValue<String>(metaDataLocal.getTitle());
        //m.setValue(new TextImeji(metaDataLocal.getTitle()));
        List<LabelsImeji> labels1 = new ArrayList<LabelsImeji>();
        labels1.add(new LabelsImeji("en", "title"));
        m1.setLabels(labels1);
        m1.setTypeUri("http://imeji.org/terms/metadata#text");
        m1.setStatementUri("http://dev-faces.mpdl.mpg.de/imeji/statement/8Di1zD7GzAGIgUaY");
        m1.setValue(titleValue);
        metaDataList.add(m1);

        //for creator
        MetaDataUpload m2 = new MetaDataUpload();
        GenericValue<String> creatorValue = new GenericValue<String>(metaDataLocal.getCreator());
        m2.setValue(creatorValue);
        //m.setValue(new TextImeji(metaDataLocal.getCreator()));
        m2.setStatementUri("http://dev-faces.mpdl.mpg.de/imeji/statement/B7bSBqhGySbxCM8Q");
        m2.setTypeUri("http://imeji.org/terms/metadata#text");

        List<LabelsImeji> labels2 = new ArrayList<LabelsImeji>();
        labels2.add(new LabelsImeji("en", "author"));
        m2.setLabels(labels2);
        metaDataList.add(m2);

        //for location
        MetaDataUpload m3 = new MetaDataUpload();
        GeoLocationImeji geo = new GeoLocationImeji(metaDataLocal.getAddress(),
                metaDataLocal.getLatitude(), metaDataLocal.getLongitude());
        GenericValue<GeoLocationImeji> geoValue = new GenericValue<GeoLocationImeji>(geo);
        m3.setValue(geoValue);
        //m.setValue(geo);
        m3.setStatementUri("http://dev-faces.mpdl.mpg.de/imeji/statement/2_FHehws3iKrRDAX");
        m3.setTypeUri("http://imeji.org/terms/metadata#geolocation");
        List<LabelsImeji> labels3 = new ArrayList<LabelsImeji>();
        labels3.add(new LabelsImeji("en", "location"));

        m3.setLabels(labels3);
        metaDataList.add(m3);

        //for accuracy
        MetaDataUpload m4 = new MetaDataUpload();
        GenericValue<Double> accuracyValue = new GenericValue<Double>(metaDataLocal.getAccuracy());
        m4.setValue(accuracyValue);
        //m.setValue(new NumberImeji(metaDataLocal.getAccuracy()));
        m4.setStatementUri("http://dev-faces.mpdl.mpg.de/imeji/statement/reIk3qTBpcOxyjKb");
        m4.setTypeUri("http://imeji.org/terms/metadata#number");
        List<LabelsImeji> labels4 = new ArrayList<LabelsImeji>();
        labels4.add(new LabelsImeji("en", "accuracy"));
        m4.setLabels(labels4);
        metaDataList.add(m4);

        //for deviceID
        MetaDataUpload m5 = new MetaDataUpload();
        GenericValue<String> deviceValue = new GenericValue<String>(metaDataLocal.getDeviceID());
        m5.setValue(deviceValue);
        //m.setValue(new TextImeji(metaDataLocal.getDeviceID()));
        m5.setStatementUri("http://dev-faces.mpdl.mpg.de/imeji/statement/IqPfT4dfewwmB2aN");
        m5.setTypeUri("http://imeji.org/terms/metadata#text");

        List<LabelsImeji> labels5 = new ArrayList<LabelsImeji>();
        labels5.add(new LabelsImeji("en", "deviceID"));
        m5.setLabels(labels5);
        metaDataList.add(m5);

        //for tags
        for(String tag : metaDataLocal.getTags() ) {
            MetaDataUpload mTag = new MetaDataUpload();
            GenericValue<String> tagValue = new GenericValue<String>(tag);
            mTag.setValue(tagValue);

            //m.setValue(new TextImeji(tag));
            mTag.setStatementUri("http://dev-faces.mpdl.mpg.de/imeji/statement/bWK8zwhEtNL259a");
            mTag.setTypeUri("http://imeji.org/terms/metadata#text");

            List<LabelsImeji> labels6 = new ArrayList<LabelsImeji>();
            labels6.add(new LabelsImeji("en", "tags"));
            mTag.setLabels(labels6);
            metaDataList.add(mTag);
        }
        Log.v(LOG_TAG, gson.toJson(metaDataList));
        return metaDataList;
    }

}
