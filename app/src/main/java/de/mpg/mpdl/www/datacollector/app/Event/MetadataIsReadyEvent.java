package de.mpg.mpdl.www.datacollector.app.Event;

import java.util.List;

/**
 * Created by allen on 21/04/15.
 */
public class MetadataIsReadyEvent {
    public final String title;
    public final List<String> tags;

    public MetadataIsReadyEvent(String title, List<String> tags) {
        this.title = title;
        this.tags = tags;
    }


}
