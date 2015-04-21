package de.mpg.mpdl.www.datacollector.app.Event;

import com.squareup.otto.Bus;

/**
 * Created by allen on 21/04/15.
 */
public class OttoSingleton {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }
}
