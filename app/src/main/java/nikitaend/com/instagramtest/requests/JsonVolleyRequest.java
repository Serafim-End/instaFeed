package nikitaend.com.instagramtest.requests;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * @author Endaltsev Nikita
 *         start at 18.09.15.
 */
public class JsonVolleyRequest extends JsonObjectRequest {
        private Context mContext;

        public JsonVolleyRequest(int method,Context mContext, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
            this.mContext = mContext;
            setShouldCache(false);
            setRetryPolicy(new DefaultRetryPolicy(55000, 1, 1));
        }


}
