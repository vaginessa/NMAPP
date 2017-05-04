package noonecares.whatever.nmapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import noonecares.whatever.nmapp.Fragments.MapFragment;
import noonecares.whatever.nmapp.Location.GPSFinder;

/**
 * Created by justdial on 5/3/17.
 */

public class NMAPPUtil {

    public static void showSettingsAlertForLocation(final Activity activity) {
        if(activity == null)return;
        android.app.AlertDialog.Builder builder= new android.app.AlertDialog.Builder (activity);
        builder.setTitle(NMAPPconstants.COMPANY_NAME);
        builder.setMessage(NMAPPconstants.GPS_DISABLED_MSG);
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Intent onGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                onGps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivityForResult(onGps, NMAPPconstants.GPS_ACTIVITY);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static MapFragment checkLocationModeAndInflateMap(GPSFinderMethods gpsFinderMethods,Activity activity,View view ) {

        if (getLocationMode(activity) != Settings.Secure.LOCATION_MODE_OFF){
           return getLocationAndInflateMap(gpsFinderMethods,activity,view);

        } else{
            NMAPPUtil.showSettingsAlertForLocation(activity);
            return null;
        }
    }

    public static int getLocationMode(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (TextUtils.isEmpty(locationProviders)) {
                locationMode = Settings.Secure.LOCATION_MODE_OFF;
            } else if (locationProviders.contains(LocationManager.GPS_PROVIDER) && locationProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                locationMode = Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
            } else if (locationProviders.contains(LocationManager.GPS_PROVIDER)) {
                locationMode = Settings.Secure.LOCATION_MODE_SENSORS_ONLY;
            } else if (locationProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                locationMode = Settings.Secure.LOCATION_MODE_BATTERY_SAVING;
            }

        }

        return locationMode;
    }

    public static void inflateFragment(Activity activity,Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = activity.getFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(R.id.mainFrame,fragment,fragment.getClass().getName());
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public static MapFragment inflateMapFragment(Activity activity,Double lat, Double lon,View view) {
        MapFragment mapFragment = MapFragment.newInstance(lat,lon);
        NMAPPUtil.inflateFragment(activity, mapFragment);
        if(view != null) {
            toggleVisibility(activity, view);
        }
        return mapFragment;
    }


    public static MapFragment getLocationAndInflateMap(GPSFinderMethods gpsFinderMethods, Activity activity,View view) {

        GPSFinder gpsFinder = gpsFinderMethods.getGPSFinder();
        if(gpsFinder.getLastknownLocation() != null){

            return inflateMapFragment(activity,gpsFinder.getLatitude(),gpsFinder.getLongitude(),view);

        }else{
            Log.i(NMAPPconstants.TAG, " no last known location");
            gpsFinder.requestLocationUpdates(0,0);
            return null;
        }
    }

    public static void toggleVisibility(Activity activity,final View view) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(view.getVisibility() == View.VISIBLE){
                    view.setVisibility(View.GONE);
                }else{
                    view.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    interface GPSFinderMethods {
        GPSFinder getGPSFinder();
    }


}
