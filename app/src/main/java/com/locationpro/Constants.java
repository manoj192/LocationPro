package com.locationpro;

import android.os.Environment;

import java.io.File;

/**
 * Created by ananth on 9/5/2015.
 */
public class Constants {
    public static final String FOLDER_NAME = "ByiByiImage";
    public static final String FOLDER_PATH = Environment
            .getExternalStorageDirectory() + "/" + FOLDER_NAME;
    public static final String IMG_PREFIX = "IMG_";
    public static final String VID_PREFIX = "VID_";

    //locations

    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME ="com.byibyi";

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    static {
        File file = new File(FOLDER_PATH);
        if (!file.exists())
            file.mkdirs();
    }
}
