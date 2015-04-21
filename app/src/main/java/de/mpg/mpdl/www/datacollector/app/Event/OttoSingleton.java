package de.mpg.mpdl.www.datacollector.app.Event;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by allen on 21/04/15.
 */
public class OttoSingleton {
    private static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance() {
        return BUS;
    }
}
