package noonecares.whatever.nmapp.Location;

import android.app.Activity;

/**
 * Created by justdial on 4/17/17.
 */

public class GetLocation {

    private Activity mActivity;
    private LocationCallback mLocationCallback;

    public GetLocation(Activity mActivity, LocationCallback mLocationCallback) {
        this.mActivity = mActivity;
        this.mLocationCallback = mLocationCallback;
    }

    private void getLocation(){

    }

    public interface LocationCallback{
        void LocationDetected(Double lat, Double lon);
    }
}
