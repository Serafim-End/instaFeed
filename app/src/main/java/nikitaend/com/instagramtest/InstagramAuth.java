package nikitaend.com.instagramtest;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nikitaend.com.instagramtest.requests.JsonVolleyRequest;
import nikitaend.com.instagramtest.requests.VolleyQueueHelper;


public abstract class InstagramAuth {
	
	private InstagramSession mSession;
	public InstagramDialogFragment mDialogFragment;

//	private OAuthAuthenticationListener mListener;
	private ProgressDialog mProgress;

	private String mAccessToken;
	private Context mContext;

	private static final String TAG = "InstagramAPI";

	private String getAuthorizationUrl() {
		StringBuilder authorizationUrl = new StringBuilder();
		authorizationUrl.append(Constants.AUTHORIZATION_URL)
				.append("?client_id=" + Credentials.CLIENT_ID)
				.append("&redirect_uri="+ Constants.REDIRECT_URI)
				.append("&response_type=code")
				.append("&display=touch")
				.append("&scope=likes+comments+relationships");

		return authorizationUrl.toString();
	}

	private String getTokenUrl() {
		StringBuilder tokenUrl = new StringBuilder();
		tokenUrl.append(Constants.ACCESS_TOKEN_ENDPOINT)
				.append("?client_id=" + Credentials.CLIENT_ID)
				.append("&client_secret=" + Credentials.CLIENT_SECRET)
				.append("&redirect_uri=" + Constants.REDIRECT_URI)
				.append("&grant_type=authorization_code");
		return tokenUrl.toString();
	}

	public InstagramAuth(Context context) {
		mContext = context;
		mSession = new InstagramSession(context);
		mAccessToken = mSession.getAccessToken();

		String mTokenUrl = getTokenUrl();
		String mAuthUrl = getAuthorizationUrl();


		InstagramDialogFragment.OAuthDialogListener listener =
				new InstagramDialogFragment.OAuthDialogListener() {
			@Override
			public void onComplete(String code) {
				getAccessToken(code);
			}

			@Override
			public void onError(String error) {
//				mListener.onFail("Authorization failed");
			}
		};

		mDialogFragment = InstagramDialogFragment.newInstance(mAuthUrl, listener);


		mProgress = new ProgressDialog(context);
		mProgress.setCancelable(false);
	}

	private void getAccessToken(final String code) {
		mProgress.setMessage("Getting access token ...");
		mProgress.show();

		JSONObject body = new JSONObject();
		try {
			body.put("client_id", Credentials.CLIENT_ID)
					.put("client_secret", Credentials.CLIENT_SECRET)
					.put("grant_type", "authorization_code")
					.put("redirect_uri", Constants.REDIRECT_URI)
					.put("code", code);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		StringRequest jsonVolleyRequest = new StringRequest(Request.Method.POST,
				Constants.ACCESS_TOKEN_ENDPOINT, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					mAccessToken = jsonObject.getString("access_token");

					JSONObject userObject = jsonObject.getJSONObject("user");
					String id = userObject.getString("id");
					String user = userObject.getString("username");
					String name = userObject.getString("full_name");

					mSession.storeAccessToken(mAccessToken, id, user, name);
					fetchUserName();

					mProgress.dismiss();
				} catch (JSONException e) {
//					mHandler.sendMessage(mHandler.obtainMessage(1, 1, 0));
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
//				mHandler.sendMessage(mHandler.obtainMessage(1, 1, 0));
			}
		}) {
			@Override
			protected Map<String,String> getParams(){
				Map<String,String> params = new HashMap<String, String>();
				params.put("client_id", Credentials.CLIENT_ID);
				params.put("client_secret", Credentials.CLIENT_SECRET);
				params.put("grant_type", "authorization_code");
				params.put("redirect_uri", Constants.REDIRECT_URI);
				params.put("code", code);
				return params;
			}
		};

		VolleyQueueHelper.getInstance(mContext).addRequest(jsonVolleyRequest, mContext);
	}
	
	private void fetchUserName() {
		mProgress.setMessage("Finalizing ...");

		StringBuilder userUrl = new StringBuilder();
		userUrl.append(Constants.API_URL)
				.append("/users/" + mSession.getId())
				.append("/?access_token=" + mAccessToken);

		JsonVolleyRequest jsonVolleyRequest = new JsonVolleyRequest(Request.Method.GET, mContext,
				userUrl.toString(), (JSONObject) null, new com.android.volley.Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonObject = (JSONObject) response.getJSONObject("data");
					String username = jsonObject.getString("username");
					String profile_picture = jsonObject.getString("profile_picture");
					String id = jsonObject.getString("id");
					JSONObject counts = jsonObject.getJSONObject("counts");
					int media = counts.getInt("media");
					int followed_by = counts.getInt("followed_by");
					int follows = counts.getInt("follows");

					mSession.makeUser(username, profile_picture, media, followed_by, follows, id);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, new com.android.volley.Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});

		VolleyQueueHelper.getInstance(mContext).addRequest(jsonVolleyRequest, mContext);

	}

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

//	public void setListener(OAuthAuthenticationListener listener) {
//		mListener = listener;
//	}

	public String getUserName() {
		return mSession.getUsername();
	}

	public String getId() {
		return mSession.getId();
	}

	public InstagramSession getSession() {
		return mSession;
	}
	
	public String getName() {
		return mSession.getName();
	}

	public String getAccessToken() {
		return mAccessToken;
	}
	
	public abstract void authorize();


	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();
			mAccessToken = null;
		}
	}

	public InstagramSession.User getInstaOwner() {
		return mSession.getUser();
	}

	public interface OAuthAuthenticationListener {
		public abstract void onSuccess();
		public abstract void onFail(String error);
	}
}