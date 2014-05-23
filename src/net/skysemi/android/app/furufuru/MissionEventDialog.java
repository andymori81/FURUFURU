package net.skysemi.android.app.furufuru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MissionEventDialog extends Activity{

	private static final int FOR_OAUTH = 0;
	private static final int FOR_TWEET = 1;
	private String mTweetMessage;

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
		            intent.putExtra("from_activity", "mission_event_dialog");
		            startActivity(intent);

		        }else{

		        	Intent intent = new Intent(getApplicationContext(), Tweet.class);
		        	intent.putExtra("tweet_message", mTweetMessage);
		        	startActivityForResult(intent, FOR_TWEET);
		        }
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		if(requestCode == FOR_OAUTH){
			if(requestCode == Activity.RESULT_OK){
				showToast("認証成功！");
			}else{
				showToast("認証失敗。。。");
			}
		}
		if(requestCode == FOR_TWEET){
			if(resultCode == Activity.RESULT_OK){
				showToast("ツイートが完了しました！");
				finish();
			}else{
				showToast("ツイートに失敗しました。。。");
			}
		}
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		overridePendingTransition(R.animator.screen_keep, R.animator.fade_out);
	}



    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }




}
