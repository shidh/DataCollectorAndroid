package de.mpg.mpdl.www.datacollector.app.Event;

import android.location.Location;

/**
 * Created by allen on 23/04/15.
 */
public class LocationChangedEvent {
    public final Location location;

    public LocationChangedEvent(Location location) {
        this.location = location;
    }
}
