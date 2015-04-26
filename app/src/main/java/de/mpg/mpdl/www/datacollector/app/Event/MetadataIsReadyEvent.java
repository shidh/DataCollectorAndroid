package de.mpg.mpdl.www.datacollector.app.Event;

import java.util.List;

/**
 * Created by allen on 21/04/15.
 */
public class MetadataIsReadyEvent {
    public final List<String> tags;

    public MetadataIsReadyEvent(List<String> tags) {
        this.tags = tags;
    }


}
