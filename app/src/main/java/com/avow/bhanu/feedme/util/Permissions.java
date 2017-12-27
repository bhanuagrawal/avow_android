package com.avow.bhanu.feedme.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by bhanu on 12/8/17.
 */

public class Permissions {



    public static final int REQUEST_PERMISSIONS = 1;
    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS
    };

    public static void requestPermissions(Activity activity) {
        // Check if we have write permission
        int permission_contacts_read = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
        int permission_write_external = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission_contacts_read != PackageManager.PERMISSION_GRANTED || permission_write_external != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_PERMISSIONS
            );
        }
    }

    public static Boolean checkContactReadPermissions(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        else {
            return true;
        }

    }

}
