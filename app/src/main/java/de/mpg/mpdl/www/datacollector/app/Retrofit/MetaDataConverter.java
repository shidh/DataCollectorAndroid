package de.mpg.mpdl.www.datacollector.app.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.GeoLocationImeji;
import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.LabelsImeji;
import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.MetaData;
import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.NumberImeji;
import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.TextImeji;
import de.mpg.mpdl.www.datacollector.app.Model.MetaDataLocal;
import de.mpg.mpdl.www.datacollector.app.utils.DeviceStatus;

/**
 * Created by allen on 28/04/15.
 */
public class MetaDataConverter {
    private static final String LOG_TAG = MetaDataConverter.class.getSimpleName();

    public static final String BASE_StatementUri = DeviceStatus.BASE_StatementUri;
    private static Gson gson = new GsonBuilder()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static MetaDataLocal metaDataToMetaDataLocal(List<MetaData> metaDataList) {
        MetaDataLocal metaDataLocal = new MetaDataLocal();
        List<String> tags = new ArrayList<String>();
        // here get the string of Metadata Json
        String json = gson.toJson(metaDataList);
        //Log.v(LOG_TAG, json);

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject meta = jsonArray.getJSONObject(i);
                //String type = meta.getString("typeUri").split("#")[1];
                //String label = meta.getString("labels").split("\"")[3];
                Type collectionType = new TypeToken<List<LabelsImeji>>(){}.getType();
                List<LabelsImeji> labels = gson.fromJson(meta.getString("labels"), collectionType);
                //Log.v(LOG_TAG, labels.get(0).getValue());

                String label = labels.get(0).getValue();
                if (label.equals("title")) {
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setTitle(value.getString("text"));
                } else if (label.equals("author")) {
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setCreator(value.getString("text"));
                } else if (label.equals("accuracy")) {
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setAccuracy(value.getDouble("number"));

                } else if (label.equals("deviceID")) {
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setDeviceID(value.getString("text"));
                } else if (label.equals("location")) {
                    //"value":{"name":"Amalienstr. 33 D-80799 München","longitude":11.57648,"latitude":48.147899}
                    JSONObject value = (JSONObject) meta.get("value");
                    metaDataLocal.setAddress(value.getString("name"));
                    metaDataLocal.setLatitude(value.getDouble("latitude"));
                    metaDataLocal.setLongitude(value.getDouble("longitude"));
                } else if (label.equals("tags")) {
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
            //Log.v(LOG_TAG, gson.toJson(metaDataLocal));
            return metaDataLocal;
        }
    }


    public static List<MetaData> metaDataLocalToMetaDataList(MetaDataLocal metaDataLocal) {
        List<MetaData> metaDataList = new ArrayList<MetaData>();

        //Log.v(LOG_TAG + metaDataLocal, gson.toJson(metaDataLocal));


        //for title
        MetaData m1 = new MetaData();
        //GenericValue<String> titleValue = new GenericValue<String>(metaDataLocal.getTitle());
        m1.setValue(new TextImeji(metaDataLocal.getTitle()));
        List<LabelsImeji> labels1 = new ArrayList<LabelsImeji>();
        labels1.add(new LabelsImeji("en", "title"));
        m1.setLabels(labels1);
        m1.setTypeUri("http://imeji.org/terms/metadata#text");
        m1.setStatementUri(BASE_StatementUri+"iNUP1SRHR9OSZGy");
        //m1.setValue(titleValue);
        metaDataList.add(m1);

        //for creator
        MetaData m2 = new MetaData();
        //GenericValue<String> creatorValue = new GenericValue<String>(metaDataLocal.getCreator());
        //m2.setValue(creatorValue);
        m2.setValue(new TextImeji(metaDataLocal.getCreator()));
        m2.setStatementUri(BASE_StatementUri+"_HTF9UJTnH4SvZRr");
        m2.setTypeUri("http://imeji.org/terms/metadata#text");

        List<LabelsImeji> labels2 = new ArrayList<LabelsImeji>();
        labels2.add(new LabelsImeji("en", "author"));
        m2.setLabels(labels2);
        metaDataList.add(m2);

        //for location
        MetaData m3 = new MetaData();
        GeoLocationImeji geo = new GeoLocationImeji(metaDataLocal.getAddress(),
                metaDataLocal.getLatitude(), metaDataLocal.getLongitude());
        //GenericValue<GeoLocationImeji> geoValue = new GenericValue<GeoLocationImeji>(geo);
        m3.setValue(geo);
        //m.setValue(geo);
        m3.setStatementUri(BASE_StatementUri+"vxKbKv5mNxKjueid");
        m3.setTypeUri("http://imeji.org/terms/metadata#geolocation");
        List<LabelsImeji> labels3 = new ArrayList<LabelsImeji>();
        labels3.add(new LabelsImeji("en", "location"));

        m3.setLabels(labels3);
        metaDataList.add(m3);

        //for accuracy
        MetaData m4 = new MetaData();
        //GenericValue<Double> accuracyValue = new GenericValue<Double>(metaDataLocal.getAccuracy());
        //m4.setValue(accuracyValue);
        m4.setValue(new NumberImeji(metaDataLocal.getAccuracy()));
        m4.setStatementUri(BASE_StatementUri+"DCAGIb3A1pcu1Nmj");
        m4.setTypeUri("http://imeji.org/terms/metadata#number");
        List<LabelsImeji> labels4 = new ArrayList<LabelsImeji>();
        labels4.add(new LabelsImeji("en", "accuracy"));
        m4.setLabels(labels4);
        metaDataList.add(m4);

        //for deviceID
        MetaData m5 = new MetaData();
        //GenericValue<String> deviceValue = new GenericValue<String>(metaDataLocal.getDeviceID());
        //m5.setValue(deviceValue);
        m5.setValue(new TextImeji(metaDataLocal.getDeviceID()));
        m5.setStatementUri(BASE_StatementUri+"ntXvuGrRf_705f_");
        m5.setTypeUri("http://imeji.org/terms/metadata#text");

        List<LabelsImeji> labels5 = new ArrayList<LabelsImeji>();
        labels5.add(new LabelsImeji("en", "deviceID"));
        m5.setLabels(labels5);
        metaDataList.add(m5);

        //for tags
        if(metaDataLocal.getTags() != null){
            //TODO
            //a bug for selecting by ActiveAndroid
            //just do a if check for now
            for (String tag : metaDataLocal.getTags()) {
                MetaData mTag = new MetaData();
                //GenericValue<String> tagValue = new GenericValue<String>(tag);
                //mTag.setValue(tagValue);

                mTag.setValue(new TextImeji(tag));
                mTag.setStatementUri(BASE_StatementUri+"VwC_f_NcbS8vQhjV");
                mTag.setTypeUri("http://imeji.org/terms/metadata#text");

                List<LabelsImeji> labels6 = new ArrayList<LabelsImeji>();
                labels6.add(new LabelsImeji("en", "tags"));
                mTag.setLabels(labels6);
                metaDataList.add(mTag);
            }
        } else{
            MetaData mTag = new MetaData();
            //GenericValue<String> tagValue = new GenericValue<String>(tag);
            //mTag.setValue(tagValue);

            mTag.setValue(new TextImeji("test"));
            mTag.setStatementUri(BASE_StatementUri+"VwC_f_NcbS8vQhjV");
            mTag.setTypeUri("http://imeji.org/terms/metadata#text");

            List<LabelsImeji> labels6 = new ArrayList<LabelsImeji>();
            labels6.add(new LabelsImeji("en", "tags"));
            mTag.setLabels(labels6);
            metaDataList.add(mTag);
        }
        //Log.v(LOG_TAG, gson.toJson(metaDataList));
        return metaDataList;
    }
}