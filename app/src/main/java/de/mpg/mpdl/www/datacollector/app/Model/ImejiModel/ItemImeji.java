package de.mpg.mpdl.www.datacollector.app.Model.ImejiModel;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by allen on 07/05/15.
 */
public class ItemImeji {

    @Expose
    private String id;

    @Expose
    private String filename;

    @Expose
    private ArrayList<MetaData> metadata;

    @Expose
    private String collectionId;


    public ItemImeji(){
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ArrayList<MetaData> getMetadata() {
        return metadata;
    }

    public void setMetadata(ArrayList<MetaData> metadata) {
        this.metadata = metadata;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
}
