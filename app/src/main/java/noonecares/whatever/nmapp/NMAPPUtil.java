package noonecares.whatever.nmapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

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
}
