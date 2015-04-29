package de.mpg.mpdl.www.datacollector.app.Model.ImejiModel;

import com.google.gson.annotations.Expose;

/**
 * Created by allen on 17/04/15.
 */
public class GenericValue <T> {
    @Expose
    private T value;

    public GenericValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void showType() {
        System.out.println("The type of T: " + value.getClass().getName());
    }
}
