package nikitaend.com.instagramtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import nikitaend.com.instagramtest.helpers.NetworkHelper;
import nikitaend.com.instagramtest.requests.VolleyInstaFeedRequest;

public class MainActivity extends AppCompatActivity
        implements VolleyInstaFeedRequest.InstaFeedRequestListener {
    private InstagramAuth mApp;
    private InstagramSession.User owner;
    private int numberPhotos = 10;
    private RecyclerView recyclerView;

    public static int display_width;
    public static int display_height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);



        if (!NetworkHelper.isOnline(this)) {
            Toast.makeText(this, "is not online", Toast.LENGTH_SHORT).show();
        }

        mApp = new InstagramAuth(this) {
            @Override
            public void authorize() {
                mApp.mDialogFragment.show(getFragmentManager(), "AuthDialog");
            }
        };

        if (!mApp.hasAccessToken()) {
            mApp.authorize();
        }

        owner = mApp.getInstaOwner();
        if (owner != null) {
            getSupportActionBar().setTitle(owner.username);
        }

        VolleyInstaFeedRequest volleyInstaFeedRequest =
                new VolleyInstaFeedRequest(this, this, mApp.getAccessToken(), numberPhotos);

    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (recyclerView != null) {
            ((FeedAdapter) recyclerView.getAdapter()).onSaveInstanceState(outState);
        }
    }

    public void disconnectDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);
        builder.setMessage("Disconnect from Instagram?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                mApp.resetAccessToken();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_exit) {
            mApp.resetAccessToken();
            mApp.authorize();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {

                recyclerView = (RecyclerView) findViewById(R.id.feed_recycleView);
                recyclerView.setHasFixedSize(false);

                LinearLayoutManager linearLayoutManager =
                        new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(linearLayoutManager);


                FeedAdapter feedAdapter =
                        new FeedAdapter(getApplicationContext(), VolleyInstaFeedRequest.getPhotoUsers(), mApp);
                recyclerView.setAdapter(feedAdapter);
            }
        });
    }

}
