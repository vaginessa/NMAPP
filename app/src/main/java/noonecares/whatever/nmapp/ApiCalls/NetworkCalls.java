package noonecares.whatever.nmapp.ApiCalls;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import noonecares.whatever.nmapp.ConnectionDetector;

import static noonecares.whatever.nmapp.NMAPPconstants.TAG;

/**
 * Created by justdial on 4/26/17.
 */

public class NetworkCalls {

    private Context context;
    private Activity activity;
    private NetworkCallbackVolley callbackVolley;

    public static NetworkCalls  getNetworkCallsInstance(Context context, Activity activity){

        NetworkCalls networkCalls = new NetworkCalls();
        networkCalls.context = context;
        networkCalls.activity = activity;
        return  networkCalls;
    }

    private void VolleyGETRequest(String url, final NetworkCallbackVolley callbackVolley) {

        if(ConnectionDetector.getConnectionDetector().isConnectedToInternet(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG + " VOLLEY", response.toString());
                    callbackVolley.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG + " VOLLEY", error.toString());
                    callbackVolley.onError();
                }
            });
        }
    }

    private void VolleyPOSTRequest(String url){

    }

    public interface NetworkCallbackVolley{
        void onSuccess();
        void onError();
    }
}
