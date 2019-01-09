package michaelbrabec.bakalab.Utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class NetworkRequests {

    private static NetworkRequests mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private NetworkRequests(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized NetworkRequests getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkRequests(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public void addToRequestQueue(StringRequest req) {
        getRequestQueue().add(req);
    }

}