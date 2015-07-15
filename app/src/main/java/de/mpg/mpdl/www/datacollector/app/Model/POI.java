package de.mpg.mpdl.www.datacollector.app.Model;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by allen on 01/04/15.
 */

//@Table(name = "POI")
public class POI {

    @Expose
    private String id;

    @Expose
    //@Column(name = "title")
    private String title;

    @Expose
    //@Column(name = "description")
    private String description;

    @Expose
    //@Column(name = "contributors")
    private List<User> contributors;

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getContributors() {
        return contributors;
    }

    public void setContributors(List<User> contributors) {
        this.contributors = contributors;
    }

    @Override
    public String toString() {
        return "POI{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", contributors=" + contributors +
                '}';
    }
}
