package net.skysemi.android.app.furufuru;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class TwitterOAuthActivity extends Activity {

	private static final int OAUTH_ACTION_VIEW = 0;
	private static final String CALLBACK_URL = "furu://twitter";

    private Twitter mTwitter;
    private RequestToken mRequestToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_oauth);

        mTwitter = TwitterUtils.getTwitterInstance(this);
        setResult(Activity.RESULT_CANCELED);

//        Bundle extras = getIntent().getExtras();
//        if(extras != null && extras.getString("from_activity").equals("mission_event_dialog")){
        startAuthorize();

//        }
    }

    @Override
    public void onRestart(){
    	super.onRestart();
    	Log.d("onRestart()", "true");
    }
    @Override
    public void onResume(){
    	super.onResume();
    	Log.d("onResume()", "true");
    }

    /**
     * OAuth認証（厳密には認可）を開始します。
     *
     * @param listener
     */
    private void startAuthorize() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {

                	Log.d("startAuthorize()", "true");

                    mRequestToken = mTwitter.getOAuthRequestToken(CALLBACK_URL);
                    return mRequestToken.getAuthorizationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                    Log.d("startAuthorize()", "false");
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
                if (url != null) {

                	Log.d("onPostExecute()", "true");

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivityForResult(intent, OAUTH_ACTION_VIEW);

                } else {

                    // 失敗。。。
                	Log.d("onPostExecute()", "false");

                }
            }
        };
        task.execute();

    }

    @Override
    public void onNewIntent(Intent intent) {
    	Log.d("onNewIntent()", "true");
        if (intent == null
                || intent.getData() == null
                || !intent.getData().toString().startsWith(CALLBACK_URL)) {
            return;
        }
        String verifier = intent.getData().getQueryParameter("oauth_verifier");

        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    // 認証成功！
                	Log.d("onPostExecute()", "認証成功");
                	TwitterUtils.storeAccessToken(getApplicationContext(), accessToken);
                	setResult(Activity.RESULT_OK);
                }
                finish();
            }
        };
        task.execute(verifier);

    }

//    @Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
//		if(requestCode != Activity.RESULT_OK){
//			finish();
//		}
//    }

}
