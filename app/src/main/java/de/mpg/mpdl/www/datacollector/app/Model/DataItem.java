package de.mpg.mpdl.www.datacollector.app.Model;

import java.util.ArrayList;

/**
 * Created by allen on 01/04/15.
 */
public class DataItem {

    private String id;
    private String filename;
    private String createdDate;
    private String fileUrl;
    private String webResolutionUrlUrl;
    private String thumbnailUrl;

    private User createdBy;
    private ArrayList<MetaData> metadata;
    private String collectionId;

    public DataItem(){

    }

    public DataItem(String id, String filename, String createdDate, String fileUrl, String webResolutionUrlUrl, String thumbnailUrl, User createdBy, ArrayList<MetaData> metadata, String collectionId) {
        this.id = id;
        this.filename = filename;
        this.createdDate = createdDate;
        this.fileUrl = fileUrl;
        this.webResolutionUrlUrl = webResolutionUrlUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.createdBy = createdBy;
        this.metadata = metadata;
        this.collectionId = collectionId;
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getWebResolutionUrlUrl() {
        return webResolutionUrlUrl;
    }

    public void setWebResolutionUrlUrl(String webResolutionUrlUrl) {
        this.webResolutionUrlUrl = webResolutionUrlUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
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
