package noonecares.whatever.nmapp;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.tileprovider.modules.IFilesystemCache;
import org.osmdroid.tileprovider.modules.SqlTileWriter;

import noonecares.whatever.nmapp.ApiCalls.NetworkCalls;
import noonecares.whatever.nmapp.Dialog.CustomProgressDialog;
import noonecares.whatever.nmapp.Fragments.MapFragment;
import noonecares.whatever.nmapp.Fragments.NoConnectionFragment;
import noonecares.whatever.nmapp.Fragments.NoRouteFragment;
import noonecares.whatever.nmapp.Location.GPSFinder;
import noonecares.whatever.nmapp.Location.GetLocation;
import noonecares.whatever.nmapp.permissionSupport.PermissionSupport;

public class MainActivity extends AppCompatActivity implements GetLocation.LocationCallback, LocationListener, NetworkCalls.NetworkCallbackVolley, MapFragment.OnFragmentInteractionListener {

    private FrameLayout mainFrame;
    private MapFragment mapFragment;
    private RelativeLayout splashLay;
    private ProgressBar progressBar;
    private TextView noLocText;
    private NoRouteFragment noRouteFragment;
    private NoConnectionFragment noConnectionFragment;
    GPSFinder gpsFinder;
    private Dialog locProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mainFrame = (FrameLayout) findViewById(R.id.mainFrame);
        splashLay = (RelativeLayout) findViewById(R.id.splashLay);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noLocText = (TextView) findViewById(R.id.no_loc_Text);

        if(ConnectionDetector.getConnectionDetector().isConnectedToInternet(getApplicationContext())){
            if(PermissionSupport.checkLocationAndStoragePermission(this, PermissionSupport.LOCATION_AND_STORAGE_PERMISSION_INITIAL)){
                if (getLocationMode(MainActivity.this) != Settings.Secure.LOCATION_MODE_OFF){
                    getLocationAndInflateMap();
                }else{
                    NMAPPUtil.showSettingsAlertForLocation(this);
                }
            }
        } else{

            inflateFragment(noConnectionFragment);
        }

//        mapFragment = MapFragment.newInstance();
//        noRouteFragment = NoRouteFragment.newInstance();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PermissionSupport.LOCATION_AND_STORAGE_PERMISSION_INITIAL){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                getLocationAndInflateMap();
            }

        }
        else if  (requestCode == PermissionSupport.LOCATION_PERMISSION_GPS_DIALOG){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NMAPPconstants.GPS_ACTIVITY){

            Log.i(NMAPPconstants.TAG, NMAPPconstants.GPS_ACTIVITY + "");

            if (PermissionSupport.checkLocationPermission(this,PermissionSupport.LOCATION_PERMISSION_GPS_DIALOG)) {
                locProgressDialog = CustomProgressDialog.showProgressDialog(this, NMAPPconstants.FETCHING_LOCATION);
                if(locProgressDialog != null)
                {
                    locProgressDialog.show();
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(getLocationMode(getApplicationContext()) != Settings.Secure.LOCATION_MODE_OFF){

                        }
                        else{
                            Toast.makeText(getApplicationContext(),"GPS is still off...", Toast.LENGTH_SHORT);
                        }
                    }
                }, 3000);
            }
        }
    }

    private void onLocationDetection() {

    }

    private void inflateMapFragment(Double lat,Double lon) {
        mapFragment = MapFragment.newInstance(lat,lon);
        inflateFragment(mapFragment);
    }

    private void inflateFragment(Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(R.id.mainFrame,fragment,fragment.getClass().getName());
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    private void getLocationAndInflateMap() {
        // getLocation and inflate Fragment and also do tile caching
        gpsFinder = new GPSFinder(this,this,this);
        if(gpsFinder.getLastknownLocation() != null){
            inflateMapFragment(gpsFinder.getLatitude(),gpsFinder.getLongitude());
        }else{
            Log.i(NMAPPconstants.TAG, " no last known location");
            gpsFinder.requestLocationUpdates(0,0);
        }
    }

    private int getLocationMode(Context context) {
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

    //Location Listener callbacks
    @Override
    public void LocationDetected(Double lat, Double lon) {
        Log.i(NMAPPconstants.TAG,lat + " , " + lon);
//        inflateMapFragment(lat,lon);
//        gpsFinder.removeUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(NMAPPconstants.TAG,"Location changed to " + location.toString() );
        Log.i(NMAPPconstants.TAG,location.getLatitude() + " , " + location.getLongitude());

        inflateMapFragment(location.getLatitude(),location.getLongitude());
        gpsFinder.removeUpdates();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError() {

    }

    //Map Fragment method
    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i(NMAPPconstants.TAG,uri.toString() );
    }
}
