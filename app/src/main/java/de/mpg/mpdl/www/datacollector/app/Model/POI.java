package de.mpg.mpdl.www.datacollector.app.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;

/**
 * Created by allen on 01/04/15.
 */

@Table(name = "POI")
public class POI extends Model{

    @Column(name = "date")
    private Date date;

    @Column(name = "title")
    private String title;

    @Column(name = "createdBy")
    private User createdBy;

    public List<DataItem> items() {
        return getMany(DataItem.class, "DataItem");
    }
    //private ArrayList<DataItem>items;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

//    public ArrayList<DataItem> getItems() {
//        return items;
//    }
//
//    public void setItems(ArrayList<DataItem> items) {
//        this.items = items;
//    }

}
