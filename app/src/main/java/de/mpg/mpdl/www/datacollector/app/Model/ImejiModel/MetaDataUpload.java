package de.mpg.mpdl.www.datacollector.app.Model.ImejiModel;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by allen on 29/04/15.
 */
public class MetaDataUpload {

    @Expose
    private GenericValue value;
    // "text": "Item 1"

    // "number": 12

    // GeoLocation
    //            "name": "Amalienstr. 33 D-80799 München",
    //            "longitude": 11.57648,
    //            "latitude": 48.147899
    @Expose
    private String statementUri;

    @Expose
    private String typeUri;

    @Expose
    private List<LabelsImeji> labels;


    public GenericValue getValue() {
        return value;
    }

    public void setValue(GenericValue genericValue) {
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
