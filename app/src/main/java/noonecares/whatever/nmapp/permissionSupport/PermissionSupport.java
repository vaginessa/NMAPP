package noonecares.whatever.nmapp.permissionSupport;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import noonecares.whatever.nmapp.NMAPPconstants;

/**
 * Created by justdial on 4/17/17.
 */

public class PermissionSupport {

    public static int LOCATION_AND_STORAGE_PERMISSION_INITIAL = 1003;
    public static int LOCATION_PERMISSION_GPS_DIALOG = 1004;

    public static boolean checkLocationAndStoragePermission(Activity activity, int requestCode){

        Log.i(NMAPPconstants.TAG, "checking permissions");
        List<String> permissions = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.i(NMAPPconstants.TAG, "Location permissions not granted, so asking");
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Log.i(NMAPPconstants.TAG, "Write ext. storage permissions not granted, so asking");
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(!permissions.isEmpty()){
            ActivityCompat.requestPermissions(activity,permissions.toArray(new String[permissions.size()]),requestCode);
            return false;
        }
        return true;
    }

    public static boolean checkLocationPermission(Activity activity, int requestCode) {
        Log.i(NMAPPconstants.TAG, "checking permissions");
        List<String> permissions = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.i(NMAPPconstants.TAG, "Location permissions not granted, so asking");
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if(!permissions.isEmpty()){
            ActivityCompat.requestPermissions(activity,permissions.toArray(new String[permissions.size()]),requestCode);
            return false;
        }
        return true;
    }
}
