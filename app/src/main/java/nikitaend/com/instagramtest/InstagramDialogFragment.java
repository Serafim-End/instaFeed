package nikitaend.com.instagramtest;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.Serializable;

/**
 * @author Endaltsev Nikita
 *         start at 19.09.15.
 */
public class InstagramDialogFragment extends DialogFragment {

    private String mUrl;
    private OAuthDialogListener mListener;
    private ProgressDialog mSpinner;
    private WebView mWebView;

    public static InstagramDialogFragment newInstance(String url, OAuthDialogListener listener) {
        InstagramDialogFragment instagramDialogFragment = new InstagramDialogFragment();

        Bundle args = new Bundle();
        args.putString("url", url);
        args.putSerializable("oAuth", listener);
        instagramDialogFragment.setArguments(args);
        return instagramDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && !args.equals("null")) {
            mUrl = args.getString("url");
            mListener = (OAuthDialogListener) args.getSerializable("oAuth");
        }

        CookieSyncManager.createInstance(getActivity());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = (View) inflater.inflate(R.layout.view_signin_dialog, container, false);

        mSpinner = new ProgressDialog(getActivity());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");


        mWebView = (WebView) v.findViewById(R.id.webView);
        if (mUrl != null) {
            mWebView.setVerticalScrollBarEnabled(false);
            mWebView.setHorizontalScrollBarEnabled(false);
            mWebView.setWebViewClient(new OAuthWebViewClient());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl(mUrl);
        }
        return v;
    }

    private class OAuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith(Constants.REDIRECT_URI)) {
                String urls[] = url.split("=");
                mListener.onComplete(urls[1]);
                dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {

            super.onReceivedError(view, errorCode, description, failingUrl);
//            mListener.onError(description);
            dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mSpinner.dismiss();
        }

    }

    public interface OAuthDialogListener extends Serializable {
        public abstract void onComplete(String accessToken);
        public abstract void onError(String error);
    }
}
