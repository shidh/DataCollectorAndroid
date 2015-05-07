package de.mpg.mpdl.www.datacollector.app.Retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by allen on 07/05/15.
 */

//@Parcel
public class RestError {
    @SerializedName("code")
    private Integer code;

    @SerializedName("error_message")
    private String strMessage;

    public RestError(String strMessage)
    {
        this.strMessage = strMessage;
    }

    //Getters and setters

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStrMessage() {
        return strMessage;
    }

    public void setStrMessage(String strMessage) {
        this.strMessage = strMessage;
    }
}