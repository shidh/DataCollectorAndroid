package de.mpg.mpdl.www.datacollector.app.Model.ImejiModel;

import com.google.gson.annotations.Expose;

/**
 * Created by allen on 29/04/15.
 */
public class TextImeji {
    @Expose
    private String text;

    public TextImeji(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
