package de.mpg.mpdl.www.datacollector.app.Model.ImejiModel;

import com.google.gson.annotations.Expose;

/**
 * Created by allen on 28/04/15.
 */
public class LabelsImeji {
    @Expose
    private String language;

    @Expose
    private String value;

    public LabelsImeji(String language, String value) {
        this.language = language;
        this.value = value;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }




}
