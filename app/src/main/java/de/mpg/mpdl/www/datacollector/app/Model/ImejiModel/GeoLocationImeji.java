package de.mpg.mpdl.www.datacollector.app.Model.ImejiModel;

import com.google.gson.annotations.Expose;

/**
 * Created by allen on 17/04/15.
 */
public class GeoLocationImeji {

    @Expose
    private String name;

    @Expose
    private double latitude;

    @Expose
    private double longitude;

    public GeoLocationImeji(String name, double latitude, double longitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
