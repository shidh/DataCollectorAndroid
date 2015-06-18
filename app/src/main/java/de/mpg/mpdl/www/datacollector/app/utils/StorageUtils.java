package de.mpg.mpdl.www.datacollector.app.utils;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by allen on 18/06/15.
 */

public class StorageUtils {
    private static final String AUDIO_FILE_NAME = ".wav";
    private static final String VIDEO_FILE_NAME = ".3gp";

    public static boolean checkExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        else {
            return false;
        }
    }

    public static String getFileName(boolean isAudio) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File storageDir = null;
        if(isAudio) {
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "DataCollector");
        }else{
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "DataCollector");
        }
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            System.out.println(storageDir.getPath());
        }
        //String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        //System.out.println(String.format("%s/%s", storageDir, (isAudio) ? timeStamp + AUDIO_FILE_NAME : timeStamp + VIDEO_FILE_NAME));
        return String.format("%s/%s", storageDir.getPath(), (isAudio) ? timeStamp+AUDIO_FILE_NAME : timeStamp+VIDEO_FILE_NAME);
    }
}