package de.mpg.mpdl.www.datacollector.app.Model.ImejiModel;

import com.google.gson.annotations.Expose;

import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.User;

/**
 * Created by allen on 01/04/15.
 * The Transform Object for Collection
 */
public class ImejiCollection{
    @Expose
    private String id;

    @Expose
    private String title;

    @Expose
    private String description;

    @Expose
    private List<User> contributors;

    @Expose
    private ImejiProfile profile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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


    /*
    {
        "id": "SDu8KG_ZonFW62k",
        "createdBy": {
                "fullname": "B",
                "userId": "ru1IbM3wElWTHCil"
        },
        "modifiedBy": {
                "fullname": "B",
                "userId": "ru1IbM3wElWTHCil"
        },
        "createdDate": "2015-06-05T09:23:29 +0200",
        "modifiedDate": "2015-06-05T09:23:50 +0200",
        "versionDate": "",
        "status": "PENDING",
        "version": 0,
        "discardComment": "",
        "title": "Test Facet Bug",
        "description": "",
        "contributors": [
        {
            "id": "j9FL1URiQhfwjqyN",
            "familyName": "B",
            "givenName": "",
            "completeName": "B",
            "alternativeName": "",
            "role": "",
            "identifiers": [
                {
                "type": "imeji",
                "value": "aTLJze8KqXaEICHT"
                }
            ],
            "organizations": [
                {
                "id": "V0vpQ2D4UwOPSD5C",
                "name": "N",
                "description": "",
                "identifiers": [
                {
                    "type": "imeji",
                    "value": "9aOybZ9YGbEHOMVk"
                }
                ],
                "city": "",
                "country": ""
            }
            ]
        }
        ],
        "profile": {
            "id": "haSZ3eVbpAqDaXA",
            "method": ""
        }
    }*/
}
