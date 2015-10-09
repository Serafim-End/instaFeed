package nikitaend.com.instagramtest.requests;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nikitaend.com.instagramtest.BuildConfig;
import nikitaend.com.instagramtest.Constants;

/**
 * @author Endaltsev Nikita
 *         start at 27.09.15.
 */
public class VolleyInstaPostRequests {

    private Context mContext;
    private static InstaLikePost mInstaLikePost;

    public VolleyInstaPostRequests(Context context) {
        this.mContext = context;
    }


    public static void configureLikePostRequest(Context context, final String id, final String accessToken) {
        StringBuilder userUrl = new StringBuilder();
        userUrl.append(Constants.MEDIA_ENDPOINT)
                .append(id)
                .append(Constants.LIKE_MEDIA_ENDPOINT);


        StringRequest jsonVolleyRequest = new StringRequest(Request.Method.POST,
                userUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject meta = jsonObject.getJSONObject("meta");
                    if (meta.getInt("code") != 200) {
                        if (BuildConfig.DEBUG)
                            Log.d("test", "code is not correct in post like request");
                    }

                } catch (JSONException e) {
                    if (BuildConfig.DEBUG)
                        Log.d("test", e.getLocalizedMessage());
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (BuildConfig.DEBUG)
                    Log.d("test", "onError: " + error.getMessage()
                            + " localized: " + error.getLocalizedMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("access_token", accessToken);
                return params;
            }
        };


        VolleyQueueHelper.getInstance(context).addRequest(jsonVolleyRequest, context);

    }

    public interface InstaLikePost {
        public abstract void onError();
    }
}
