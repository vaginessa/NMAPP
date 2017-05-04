package noonecares.whatever.nmapp;

import android.Manifest;
import android.app.Activity;
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
import android.view.View;
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

public class MainActivity extends AppCompatActivity implements GetLocation.LocationCallback, LocationListener, NetworkCalls.NetworkCallbackVolley, MapFragment.OnFragmentInteractionListener, NoConnectionFragment.OnFragmentInteractionListener,NMAPPUtil.GPSFinderMethods {

    private FrameLayout mainFrame;
    private MapFragment mapFragment;
    private RelativeLayout splashLay;
    private ProgressBar progressBar;
    private TextView noLocText;
    private NoRouteFragment noRouteFragment;
    private NoConnectionFragment noConnectionFragment;
    private Dialog locProgressDialog;
    Context mcontext;
    public GPSFinder gpsFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mcontext =this;
        gpsFinder = new GPSFinder(this,this,this);
        mainFrame = (FrameLayout) findViewById(R.id.mainFrame);
        splashLay = (RelativeLayout) findViewById(R.id.splashLay);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noLocText = (TextView) findViewById(R.id.no_loc_Text);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        splashLay.setVisibility(View.GONE);
//                    }
//                });
//            }
//        },2000);


        if(ConnectionDetector.getConnectionDetector().isConnectedToInternet(getApplicationContext())){
            if(PermissionSupport.checkLocationAndStoragePermission(this, PermissionSupport.LOCATION_AND_STORAGE_PERMISSION_INITIAL)){

                mapFragment = NMAPPUtil.checkLocationModeAndInflateMap(this,this,splashLay);

            }

        }
        else{
            noConnectionFragment = NoConnectionFragment.newInstance();
            NMAPPUtil.inflateFragment(this,noConnectionFragment);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PermissionSupport.LOCATION_AND_STORAGE_PERMISSION_INITIAL){

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                mapFragment = NMAPPUtil.checkLocationModeAndInflateMap(this,this,splashLay);

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

                locProgressDialog = CustomProgressDialog.ProgressDialog(this, NMAPPconstants.FETCHING_LOCATION);
                if(locProgressDialog != null)
                {
                    locProgressDialog.show();
                }
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(NMAPPUtil.getLocationMode(getApplicationContext()) != Settings.Secure.LOCATION_MODE_OFF){
                            //check for permission and location and inflateMap
                            if (PermissionSupport.checkLocationPermission(MainActivity.this,PermissionSupport.LOCATION_PERMISSION_GPS_DIALOG)) {
                                NMAPPUtil.getLocationAndInflateMap(MainActivity.this,MainActivity.this,splashLay);
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"GPS is still off...", Toast.LENGTH_SHORT);
                        }
                    }
                });

        }
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
        if(locProgressDialog != null)
        {
            locProgressDialog.hide();
        }
        NMAPPUtil.inflateMapFragment(this,location.getLatitude(),location.getLongitude(),splashLay);
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
    public void onMapFragmentInteraction(Uri uri) {
        Log.i(NMAPPconstants.TAG,"Map fragment interaction " + uri.toString() );
    }

    @Override
    public void onConnectionFragmentInteraction(Uri uri) {
        Log.i(NMAPPconstants.TAG,"Noconnection interaction + " + uri.toString() );
    }

    @Override
    public void tryAgainButtonPressed() {
        if(ConnectionDetector.getConnectionDetector().isConnectedToInternet(this)){
            if(PermissionSupport.checkLocationAndStoragePermission(this, PermissionSupport.LOCATION_AND_STORAGE_PERMISSION_INITIAL)){
                NMAPPUtil.checkLocationModeAndInflateMap(this,this,splashLay);
            }
        }else {
            Toast.makeText(this, NMAPPconstants.STILL_NO_INTERNET, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public GPSFinder getGPSFinder() {
        return gpsFinder;
    }
}
