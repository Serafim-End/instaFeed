package nikitaend.com.instagramtest.requests;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import nikitaend.com.instagramtest.BuildConfig;
import nikitaend.com.instagramtest.Constants;
import nikitaend.com.instagramtest.PhotoUser;

/**
 * @author Endaltsev Nikita
 *         start at 25.09.15.
 */
public class VolleyInstaFeedRequest {
    private Context mContext;
    private String accessToken;
    private int numberPhotos;
    private static InstaFeedRequestListener instaFeedRequestListener;

    private static ArrayList<PhotoUser> photoUsers = new ArrayList<>();
    public VolleyInstaFeedRequest(Context context, InstaFeedRequestListener instaFeedRequestListener,
                                  String accessToken, int numberPhotos) {
        this.mContext = context;
        this.accessToken = accessToken;
        this.numberPhotos = numberPhotos;
        this.instaFeedRequestListener = instaFeedRequestListener;

        configureFeed();
    }

    private void configureFeed() {
        StringBuilder userUrl = new StringBuilder();
        userUrl.append(Constants.USER_FEED_ENDPOINT)
                .append("/?access_token=" + accessToken)
                .append("&count=" + numberPhotos);

        JsonVolleyRequest jsonVolleyRequest = new JsonVolleyRequest(Request.Method.GET, mContext,
                userUrl.toString(), (JSONObject) null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject meta = response.getJSONObject("meta");
                    if (meta.getInt("code") != 200) {
                        if (BuildConfig.DEBUG)
                            Log.d("test", "wrong meta response code" + meta.getInt("code"));
                        // some problem in request
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.d("test", "correct meta code (200)");

                        JSONArray photosInfo = response.getJSONArray("data");
                        for (int i = 0; i < photosInfo.length(); i++) {
                            JSONObject photoInfo = (JSONObject) photosInfo.get(i);
                            parsePhoto(photoInfo);
                        }

                        instaFeedRequestListener.onSuccess();
                    }

                } catch (JSONException e) {
                    if (BuildConfig.DEBUG)
                        Log.d("test", e.getLocalizedMessage());
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (BuildConfig.DEBUG)
                    Log.d("test", "some problem in volley request: " + error.toString());
            }
        });

        VolleyQueueHelper.getInstance(mContext).addRequest(jsonVolleyRequest, mContext);

    }

    public static void configureLikesId(Context mContext, final InstaFeedLikeRequestListener instaFeedLikeRequestListener,
                                        final PhotoUser photoUser, String id) {
        StringBuilder userUrl = new StringBuilder();
        userUrl.append(Constants.USER_FEED_ENDPOINT)
                .append(id)
                .append(Constants.LIKE_MEDIA_ENDPOINT);

        JsonVolleyRequest jsonVolleyRequest = new JsonVolleyRequest(Request.Method.GET, mContext,
                userUrl.toString(), (JSONObject) null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject meta = response.getJSONObject("meta");
                    if (meta.getInt("code") != 200) {
                        if (BuildConfig.DEBUG)
                            Log.d("test", "wrong meta response code" + meta.getInt("code"));
                        // some problem in request
                    } else {
//                        photoUser.likes = parseLikes(photoUser, response);
                        instaFeedLikeRequestListener.onSuccess(parseLikes(photoUser, response));
                    }
                } catch (JSONException e) {
                    if (BuildConfig.DEBUG)
                        Log.d("test", e.getLocalizedMessage());
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (BuildConfig.DEBUG)
                    Log.d("test", "some problem in volley request: " + error.toString());
            }
        });

        VolleyQueueHelper.getInstance(mContext).addRequest(jsonVolleyRequest, mContext);
    }

    private void parsePhoto(JSONObject photoInfo) {
        try {
            String type = photoInfo.getString("type");
            if (type.equals("image")) {
                PhotoUser photoUser = new PhotoUser();
                photoUser.likes = parseLikes(photoUser, photoInfo.getJSONObject("likes"));
                photoUser.image = parseImage(photoUser, photoInfo.getJSONObject("images"));
                photoUser.comments = parseComments(photoUser, photoInfo.getJSONObject("comments"));
                photoUser.user = parseUser(photoUser, photoInfo);
                photoUser.isLiked = photoInfo.getBoolean("user_has_liked");

                photoUsers.add(photoUser);

            }
        } catch (JSONException e) {
            if (BuildConfig.DEBUG)
                Log.d("test", "some problem parsing photo: " + e.toString());
        }
    }

    private static PhotoUser.Likes parseLikes(PhotoUser photoUser, JSONObject likes) {
        try {
            JSONArray likesJsonArray = likes.getJSONArray("data");
            ArrayList<PhotoUser.Like> likesArrayList = new ArrayList<>();
            for (int i = 0; i < likesJsonArray.length(); i++) {
                JSONObject likeJson = (JSONObject) likesJsonArray.get(i);
                likesArrayList.add(
                        photoUser.new Like(likeJson.getString("username"),
                                likeJson.getString("profile_picture"),
                                likeJson.getString("id")));
            }
            return photoUser.new Likes(likes.getInt("count"), likesArrayList);

        } catch (JSONException e) {
            if (BuildConfig.DEBUG)
                Log.d("test", "some problem parsing likes: " + e.toString());
            return null;
        }
    }

    private PhotoUser.Comments parseComments(PhotoUser photoUser, JSONObject commentsJson) {
        try {
            JSONArray commentsJsonArray = commentsJson.getJSONArray("data");
            ArrayList<PhotoUser.Comment> commentsArray = new ArrayList<>();
            for (int i = 0 ; i < commentsJsonArray.length(); i++) {
                JSONObject commentJson = commentsJsonArray.getJSONObject(i);
                JSONObject fromJson = commentJson.getJSONObject("from");
                commentsArray.add(
                        photoUser.new Comment(fromJson.getString("username"),
                                fromJson.getString("profile_picture"),
                                commentJson.getString("created_time"),
                                commentJson.getString("text")));
            }
            return photoUser.new Comments(commentsJson.getInt("count"), commentsArray);
        } catch (JSONException e) {
            if (BuildConfig.DEBUG)
                Log.d("test", e.toString());
            return null;
        }
    }

    private PhotoUser.Image parseImage(PhotoUser photoUser, JSONObject imagesJson) {
        try {
            JSONObject lowResolutionObject = imagesJson.getJSONObject("low_resolution");
            JSONObject thumbnail = imagesJson.getJSONObject("thumbnail");
            JSONObject standartResolution = imagesJson.getJSONObject("standard_resolution");
            return photoUser.new Image(lowResolutionObject.getString("url"),
                    thumbnail.getString("url"), standartResolution.getString("url"));
        } catch (JSONException e) {
            if (BuildConfig.DEBUG)
                Log.d("test", "some problem parsing comments: " + e.toString());
            return null;
        }
    }

    private PhotoUser.User parseUser(PhotoUser photoUser, JSONObject photoInfo) {
        try {
            JSONObject userJson = photoInfo.getJSONObject("user");

//            if (photoInfo.get("caption") == null) {
                return photoUser.new User(userJson.getString("username"),
                        userJson.getString("profile_picture"),
                        photoInfo.getString("id"), "", "");
//            } else {
//                JSONObject captionJson = photoInfo.getJSONObject("caption");
//                return photoUser.new User(userJson.getString("username"),
//                        userJson.getString("profile_picture"),
//                        photoInfo.getString("id"), captionJson.getString("created_time"), "");
//            }
        } catch (JSONException e) {
            if (BuildConfig.DEBUG)
                Log.d("test", "some problem parsing user: " + e.toString());
            return null;
        }
    }

    public static ArrayList<PhotoUser> getPhotoUsers() {
        return photoUsers;
    }

    public interface InstaFeedRequestListener {
        public abstract void onSuccess();
    }

    public interface InstaFeedLikeRequestListener {
        public abstract void onSuccess(PhotoUser.Likes likes);
    }



}
