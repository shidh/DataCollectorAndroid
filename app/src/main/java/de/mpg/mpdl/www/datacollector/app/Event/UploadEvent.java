package de.mpg.mpdl.www.datacollector.app.Event;

/**
 * Created by allen on 28/04/15.
 */
public class UploadEvent {
    public final Integer httpStatus;

    public UploadEvent(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }
}
