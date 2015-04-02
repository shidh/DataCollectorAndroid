package de.mpg.mpdl.www.datacollector.app.Model;

import java.sql.Blob;
import java.util.ArrayList;

/**
 * Created by allen on 01/04/15.
 */
public class User {
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
