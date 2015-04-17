package de.mpg.mpdl.www.datacollector.app.Model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by allen on 01/04/15.
 */
public class POI {
    private Date date;
    private String poiId;
    private String title;
    private User createdBy;
    private ArrayList<DataItem>items;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public ArrayList<DataItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<DataItem> items) {
        this.items = items;
    }

}
