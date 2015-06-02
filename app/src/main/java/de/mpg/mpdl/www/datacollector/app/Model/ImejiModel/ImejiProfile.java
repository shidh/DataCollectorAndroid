package de.mpg.mpdl.www.datacollector.app.Model.ImejiModel;

import com.google.gson.annotations.Expose;

/**
 * Created by allen on 02/06/15.
 */
public class ImejiProfile {
    @Expose
    private String Profileid;

    @Expose
    private String Method;

    public String getProfileid() {
        return Profileid;
    }

    public void setProfileid(String profileid) {
        Profileid = profileid;
    }

    public String getMethod() {
        return Method;
    }

    public void setMethod(String method) {
        Method = method;
    }

    //    "Profile": {
//        "Profileid": "WYc_odsl7OQrzzJ"
//        "Method": "reference"
//    }
}
