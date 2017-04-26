package noonecares.whatever.nmapp;
/*
 * Internet Connection handler
 * @author Justdial
 * @copyright Justdial Ltd.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

	private static ConnectionDetector mConnectionDetector = null;

	private ConnectionDetector() {

	}

	public static synchronized ConnectionDetector getConnectionDetector() {
		if (mConnectionDetector == null) {
			mConnectionDetector = new ConnectionDetector();
		}
		return mConnectionDetector;
	}

	public boolean isConnectedToInternet(Context context) {
		ConnectivityManager cm =
					(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//			boolean isConnected = activeNetwork != null &&
//					activeNetwork.isConnectedOrConnecting();
		return  activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting();
	}
}
