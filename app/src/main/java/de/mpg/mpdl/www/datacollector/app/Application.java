package de.mpg.mpdl.www.datacollector.app;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by allen on 19/04/15.
 */
class myApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}