package de.mpg.mpdl.www.datacollector.app.Model;

import com.activeandroid.annotation.Column;
import com.google.gson.annotations.Expose;

import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.ImejiModel.ImejiProfile;

/**
 * Created by allen on 01/04/15.
 */
public class ImejiCollection {

    @Expose
    @Column(name = "title")
    private String title;

    @Expose
    @Column(name = "description")
    private String description;

    @Expose
    //@Column(name = "contributors")
    private List<User> contributors;

    @Expose
    private ImejiProfile profile;


    public String getTitle() {
        return title;
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

    public ImejiProfile getProfile() {
        return profile;
    }

    public void setProfile(ImejiProfile profile) {
        this.profile = profile;
    }
}
