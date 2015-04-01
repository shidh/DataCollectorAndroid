package de.mpg.mpdl.www.datacollector.app.Model;

import java.util.ArrayList;

/**
 * Created by allen on 01/04/15.
 */
public class MetaData {
    private String metadataId;
    private String title;
    private String address;
    private double latitude;
    private double longitude;
    private double accuracy;

    private String deviceID;
    private ArrayList<String> tags;
    private String creator;
    private DataItem whichItem;
}
