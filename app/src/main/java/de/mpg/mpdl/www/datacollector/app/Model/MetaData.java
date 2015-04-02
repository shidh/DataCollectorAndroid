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

//    {
//        "value" : {"text" : "Eclipse in Munich"},
//        "statementUri" : "http://dev-faces.mpdl.mpg.de/imeji/statement/xlzrqmdy5j9lCv7q",
//        "typeUri" : "http://imeji.org/terms/metadata#text",
//        "labels" : [ {
//                    "language" : "en",
//                     "value" : "title"
//                     } ]
//    }
//
//    {
//        "value" : {
//                "name" : "Amalienstrasse 33 80799 Munich",
//                "longitude" : 11.576622,
//                "latitude" : 48.14793
//    },
//        "statementUri" : "http://dev-faces.mpdl.mpg.de/imeji/statement/sKnO49q91ert7hIh",
//        "typeUri" : "http://imeji.org/terms/metadata#geolocation",
//        "labels" : [ {
//                "language" : "en",
//                "value" : "location"
//    } ]
//    }
}

