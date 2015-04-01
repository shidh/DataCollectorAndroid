package de.mpg.mpdl.www.datacollector.app.Model;

import java.util.Collection;
import java.util.Date;

/**
 * Created by allen on 01/04/15.
 */
public class DataItem {
    private String itermId;
    private String fileName;
    private String desc;
    private Date date;
    private String dataPath;
    private String dataPathWeb;
    private String dataPathThumbnail;

    private User createdBy;
    private MetaData metadata;
    private Collection whichCollection;


}
