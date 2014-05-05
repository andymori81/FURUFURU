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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MissionEventDialog extends Activity{
	
	private String mTweetMessage;
	private Twitter mTwitter;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.show_event_dialog);
		
		Bundle extras = getIntent().getExtras();
		// テキストの表示処理
		TextView messageView = (TextView)findViewById(R.id.show_event_dialog_messageTextView);
		mTweetMessage = extras.getString(MissionEvent.DIALOG_MESSAGE);
		messageView.setText(mTweetMessage);
		
		// バッジの表示処理
		switch (extras.getInt(MissionEvent.SHOW_BADGE_COUNT)) {
		case 1:
			findViewById(R.id.show_event_dialog_badge1).setVisibility(View.GONE);
			findViewById(R.id.show_event_dialog_badge2).setVisibility(View.GONE);
			findViewById(R.id.show_event_dialog_badge3).setVisibility(View.GONE);
			findViewById(R.id.show_event_dialog_badge4).setVisibility(View.GONE);
			break;
		case 2:
			findViewById(R.id.show_event_dialog_badge2).setVisibility(View.GONE);
			findViewById(R.id.show_event_dialog_badge3).setVisibility(View.GONE);
			findViewById(R.id.show_event_dialog_badge4).setVisibility(View.GONE);
			break;
		case 3:
			findViewById(R.id.show_event_dialog_badge3).setVisibility(View.GONE);
			findViewById(R.id.show_event_dialog_badge4).setVisibility(View.GONE);
			break;
		case 4:
			findViewById(R.id.show_event_dialog_badge4).setVisibility(View.GONE);
			break;

		default:
			break;
		}
		
		
		Button ok_button = (Button)findViewById(R.id.show_event_dialog_button1);
		ok_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		Button tweet_button = (Button)findViewById(R.id.show_event_dialog_button2);
		tweet_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!TwitterUtils.hasAccessToken(getApplicationContext())) {
		            Intent intent = new Intent(getApplicationContext(), TwitterOAuthActivity.class);
		            startActivity(intent);
		           
		        }else{
		        	
		        	mTwitter = TwitterUtils.getTwitterInstance(getApplicationContext());
		        	tweet();
		        }
			}
			
		});
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		overridePendingTransition(R.animator.screen_keep, R.animator.fade_out);
	}
	
	private void tweet() {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    mTwitter.updateStatus(params[0]);
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    showToast("ツイートが完了しました！");
                    finish();
                } else {
                    showToast("ツイートに失敗しました。。。");
                }
            }
        };
        task.execute(mTweetMessage);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
  

   

}
