package nikitaend.com.instagramtest.requests;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * @author Endaltsev Nikita
 *         start at 18.09.15.
 */
public class VolleyQueueHelper {

    private static VolleyQueueHelper volleyQueueHelper;
    private RequestQueue requestQueue;
    public synchronized static VolleyQueueHelper getInstance(Context context) {
        if(volleyQueueHelper == null) {
            volleyQueueHelper = new VolleyQueueHelper(context);
        }
        return volleyQueueHelper;
    }

    private VolleyQueueHelper(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public <T> void addRequest(Request<T> request, Object context) {
        request.setTag(context);
        requestQueue.add(request);
    }
    public <T> void addRequest(Request<T> request) {
        requestQueue.add(request);
    }
    public RequestQueue getQueue() {
        return requestQueue;
    }
}

