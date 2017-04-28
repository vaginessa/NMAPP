package noonecares.whatever.nmapp;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.osmdroid.tileprovider.modules.IFilesystemCache;
import org.osmdroid.tileprovider.modules.SqlTileWriter;

import noonecares.whatever.nmapp.ApiCalls.NetworkCalls;
import noonecares.whatever.nmapp.Fragments.MapFragment;
import noonecares.whatever.nmapp.Fragments.NoConnectionFragment;
import noonecares.whatever.nmapp.Fragments.NoRouteFragment;
import noonecares.whatever.nmapp.Location.GPSFinder;
import noonecares.whatever.nmapp.Location.GetLocation;
import noonecares.whatever.nmapp.permissionSupport.PermissionSupport;

public class MainActivity extends AppCompatActivity implements GetLocation.LocationCallback, LocationListener, NetworkCalls.NetworkCallbackVolley {

    private FrameLayout mainFrame;
    private MapFragment mapFragment;
    private NoRouteFragment noRouteFragment;
    private NoConnectionFragment noConnectionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mainFrame = (FrameLayout) findViewById(R.id.mainFrame);
        if(ConnectionDetector.getConnectionDetector().isConnectedToInternet(getApplicationContext())){
            if(PermissionSupport.checkLocationAndStoragePermission(this, PermissionSupport.LOCATION_AND_STORAGE_PERMISSION_INITIAL)){
                GPSFinder gpsFinder = new GPSFinder(this,this,this);
                if(gpsFinder.getLocation() != null){
                    inflateMapFragment(gpsFinder.getLatitude(),gpsFinder.getLongitude());
                }else{

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

                // getLocation and inflate Fragment and also do tile caching
                GPSFinder gpsFinder = new GPSFinder(this,this,this);
                if(gpsFinder.getLocation() != null){
                    inflateMapFragment(gpsFinder.getLatitude(),gpsFinder.getLongitude());
                }else{

                }
            }
            else if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                // getLocation and inflate Fragment
                GPSFinder gpsFinder = new GPSFinder(this,this,this);
                if(gpsFinder.getLocation() != null){
                    inflateMapFragment(gpsFinder.getLatitude(),gpsFinder.getLongitude());
                }else{

                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    //Location Listener callbacks
    @Override
    public void LocationDetected(Double lat, Double lon) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(NMAPPconstants.TAG,"Location changed to " + location.toString() );
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
}
