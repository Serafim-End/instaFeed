package nikitaend.com.instagramtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class InstagramSession {

	private SharedPreferences sharedPref;
	private Editor editor;

	private static final String SHARED = "Instagram_Preferences";
	private static final String API_USERNAME = "username";
	private static final String API_ID = "id";
	private static final String API_NAME = "name";
	private static final String API_ACCESS_TOKEN = "access_token";
	private User owner;

	public InstagramSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}

	public void storeAccessToken(String accessToken, String id, String username, String name) {
		editor.putString(API_ID, id);
		editor.putString(API_NAME, name);
		editor.putString(API_ACCESS_TOKEN, accessToken);
		editor.putString(API_USERNAME, username);
		editor.commit();
	}

	public void storeAccessToken(String accessToken) {
		editor.putString(API_ACCESS_TOKEN, accessToken);
		editor.commit();
	}

	public void resetAccessToken() {
		editor.putString(API_ID, null);
		editor.putString(API_NAME, null);
		editor.putString(API_ACCESS_TOKEN, null);
		editor.putString(API_USERNAME, null);
		editor.commit();
	}

	public String getUsername() {
		return sharedPref.getString(API_USERNAME, null);
	}

	public String getId() {
		return sharedPref.getString(API_ID, null);
	}

	public String getName() {
		return sharedPref.getString(API_NAME, null);
	}

	public String getAccessToken() {
		return sharedPref.getString(API_ACCESS_TOKEN, null);
	}

	public void makeUser(String username, String profile_picture,
					int media_count, int followed_by_count, int follows_count, String id) {
		owner = new User(username, profile_picture, media_count, followed_by_count, follows_count, id);
	}

	public User getUser() {
		if (owner != null) {
			return owner;
		}

		return null;
	}

	public class User {
		public String username;
		public String profile_picture;
		public int media_count;
		public int followed_by_count;
		public int follows_count;
		public String id;

		public User(String username, String profile_picture,
					int media_count, int followed_by_count, int follows_count, String id) {
			this.username = username;
			this.profile_picture = profile_picture;
			this.media_count = media_count;
			this.followed_by_count = followed_by_count;
			this.follows_count = follows_count;
			this.id = id;
		}

	}

}