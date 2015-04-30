package de.mpg.mpdl.www.datacollector.app.Model.ImejiModel;

import com.google.gson.annotations.Expose;

/**
 * Created by allen on 29/04/15.
 */
public class NumberImeji {

    @Expose
    private Double number;

    public NumberImeji(Double number) {
        this.number = number;
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }
}
