package net.skysemi.android.app.furufuru;

import twitter4j.TwitterException;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

public class Tweet extends Activity {

	private String mTweetMess;

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.activity_twitter_oauth);

	     setResult(Activity.RESULT_CANCELED);
	     Bundle extras = getIntent().getExtras();
	     if(extras == null){
	    	 finish();
	     }else{
	    	mTweetMess = extras.getString("tweet_message");
	     }
	     tweet();

	 }

	 private void tweet() {
	     AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
	         @Override
	         protected Boolean doInBackground(String... params) {
	             try {
	            	 TwitterUtils.getTwitterInstance(getApplicationContext()).updateStatus(params[0]);
	                 return true;
	             } catch (TwitterException e) {
	                 e.printStackTrace();
	                 return false;
	             }
	         }

	         @Override
	         protected void onPostExecute(Boolean result) {
	             if (result) {
	                 setResult(Activity.RESULT_OK);
	             }
	             finish();
	         }
	     };
	     task.execute(mTweetMess);
	 }

}
