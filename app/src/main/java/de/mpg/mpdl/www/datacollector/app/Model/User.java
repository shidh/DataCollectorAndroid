package de.mpg.mpdl.www.datacollector.app.Model;

import java.sql.Blob;
import java.util.ArrayList;

/**
 * Created by allen on 01/04/15.
 */
public class User {
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ImejiCollection getCollections() {
        return collections;
    }

    public void setCollections(ImejiCollection collections) {
        this.collections = collections;
    }

    public ArrayList<DataItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<DataItem> items) {
        this.items = items;
    }

    private String desc;
    private String  email;
    private String  familyName;
    private String givenName;
    private Blob image;
    private String password;
    private String userId;
    private ImejiCollection collections;
    private ArrayList<DataItem> items;
}
