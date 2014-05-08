package net.skysemi.android.app.furufuru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TopActivity extends Activity implements OnClickListener {

	Button mStartButton;
	Button mRecordButton;
	Button mBadgeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top);

		mStartButton = (Button) findViewById(R.id.activity_top_button_start);
		mStartButton.setOnClickListener(this);
		mRecordButton = (Button) findViewById(R.id.activity_top_button_record);
		mRecordButton.setOnClickListener(this);

	      }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.top, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v == mStartButton)
			startActivity(new Intent(this, PlayMainActivity.class));

		if (v == mRecordButton)
			startActivity(new Intent(this, RecordViewActivity.class));


		// finish();
	}

}
