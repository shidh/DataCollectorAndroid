package de.mpg.mpdl.www.datacollector.app.Model.ImejiModel;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by allen on 17/04/15.
 */
public class MetaData {

    @Expose
    private Object value;
    // "text": "Item 1"

    // "number": 12

    // GeoLocation
    //            "name": "Amalienstr. 33 D-80799 München",
    //            "longitude": 11.57648,
    //            "latitude": 48.147899
    @Expose
    private String statementUri;
    //"statementUri": "http://dev-faces.mpdl.mpg.de/imeji/statement/reIk3qTBpcOxyjKb",

    @Expose
    private String typeUri;
    //"typeUri": "http://imeji.org/terms/metadata#geolocation",

    @Expose
    private List<LabelsImeji> labels;
    //json = "{ \"collectionId\" : \"Qwms6Gs040FBS264\"}";
    //            "language": "en",
    //            "value": "title"



    public Object getValue() {
        return value;
    }

    public void setValue(Object genericValue) {
        this.value = genericValue;
    }

    public String getStatementUri() {
        return statementUri;
    }

    public void setStatementUri(String statementUri) {
        this.statementUri = statementUri;
    }

    public String getTypeUri() {
        return typeUri;
    }

    public void setTypeUri(String typeUri) {
        this.typeUri = typeUri;
    }

    public List<LabelsImeji> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelsImeji> labels) {
        this.labels = labels;
    }


}


