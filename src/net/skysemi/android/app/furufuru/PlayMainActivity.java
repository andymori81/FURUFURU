package net.skysemi.android.app.furufuru;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class PlayMainActivity extends Activity implements SensorEventListener {

	private SensorManager mSensorManager;
	private TextView mTextView;
	private TextView mTextViewCal;

	private float x = 0;
	private float y = 0;
	private float z = 0;
	private int mCount = 0;
	private boolean mPreFlag = false;
	private boolean mFlag = false;
	private float mMaxValue = 0;
	private float mCal = 0;
	private int mTmpCount = 0;
	private static final int COLLECTION_CONSTANT = 2; // 補正定数
	private String mDate;
	private MissionEvent mMission;

	private ArrayList<FuruStatus> list = new ArrayList<FuruStatus>();

	  /** The log tag. */
	  private static final String LOG_TAG = "InterstitialSample";

	  /** Your ad unit id. Replace with your actual ad unit id. */
	  private static final String AD_UNIT_ID = "ca-app-pub-5520972500232876/7602942341";

	  /** The interstitial ad. */
	  private InterstitialAd interstitialAd;

	  /** The button that show the interstitial. */
	  private Button showButton;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_play_main);

		mTextView = (TextView) findViewById(R.id.activity_play_main_textView_count);
		mTextViewCal = (TextView) findViewById(R.id.activitu_paly_main_textView_cal);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		mMission = new MissionEvent().getMissionEventInstance(this);
		mDate = mMission.getNowDate();
		Log.d("nowDate", mDate);

	    // Create an ad.
	    interstitialAd = new InterstitialAd(this);
	    interstitialAd.setAdUnitId(AD_UNIT_ID);

	    // Set the AdListener.
	    interstitialAd.setAdListener(new AdListener() {
	      @Override
	      public void onAdLoaded() {
	        Log.d(LOG_TAG, "onAdLoaded");
	        Toast.makeText(PlayMainActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();

	        // Change the button text and enable the button.
	        showButton.setText("Show Interstitial");
	        showButton.setEnabled(true);
	      }

	      @Override
	      public void onAdFailedToLoad(int errorCode) {
	        String message = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
	        Log.d(LOG_TAG, message);
	        Toast.makeText(PlayMainActivity.this, message, Toast.LENGTH_SHORT).show();

	        // Change the button text and disable the button.
	        showButton.setText("Ad Failed to Load");
	        showButton.setEnabled(false);
	      }
	    });

	    showButton = (Button) findViewById(R.id.showAd);
	    showButton.setEnabled(false);
	}

	  /** Called when the Load Interstitial button is clicked. */
	  public void loadInterstitial(View unusedView) {
	    // Disable the show button until the new ad is loaded.
	    showButton.setText("Loading Interstitial...");
	    showButton.setEnabled(false);

	    // Check the logcat output for your hashed device ID to get test ads on a physical device.
	    AdRequest adRequest = new AdRequest.Builder()
	        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE")
	        .build();

	    // Load the interstitial ad.
	    interstitialAd.loadAd(adRequest);
	  }

	  /** Called when the Show Interstitial button is clicked. */
	  public void showInterstitial(View unusedView) {
	    // Disable the show button until another interstitial is loaded.
	    showButton.setText("Interstitial Not Ready");
	    showButton.setEnabled(false);

	    if (interstitialAd.isLoaded()) {
	      interstitialAd.show();
	    } else {
	      Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
	    }
	  }

	  /** Gets a string error reason from an error code. */
	  private String getErrorReason(int errorCode) {
	    String errorReason = "";
	    switch(errorCode) {
	      case AdRequest.ERROR_CODE_INTERNAL_ERROR:
	        errorReason = "Internal error";
	        break;
	      case AdRequest.ERROR_CODE_INVALID_REQUEST:
	        errorReason = "Invalid request";
	        break;
	      case AdRequest.ERROR_CODE_NETWORK_ERROR:
	        errorReason = "Network Error";
	        break;
	      case AdRequest.ERROR_CODE_NO_FILL:
	        errorReason = "No fill";
	        break;
	    }
	    return errorReason;
	  }

	@Override
	protected void onResume() {
		super.onResume();
		Sensor sensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		mSensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_GAME);

		SQLiteDatabase db = new SQLiteUtili().createDB(this);
		try {
			list = new SQLiteUtili().readDB(db, new String[] {mDate});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

		if (list.size() != 0) {
			mCount = list.get(0).getCount();
			mCal = list.get(0).getCalorie();
		}
		mTextView.setText(String.valueOf(mCount));
		mTextViewCal.setText(String.valueOf(getKCal(mCal)));

	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);

		SQLiteDatabase db = new SQLiteUtili().createDB(this);

		if (list.size() == 0) {
			try {
				new SQLiteUtili().writeDB(db, mDate, mCount, mCal);
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} finally {
				db.close();
			}

		} else {
			new SQLiteUtili().upDateRecord(db, list.get(0).getId(), mDate,
					mCount, mCal);
			db.close();
		}



	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// 処理なし
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			x = event.values[0];
			y = event.values[1];
			z = event.values[2];
			float mValue = x * x + y * y + z * z; // 加速度ベクトルの絶対値
			if (mMaxValue < mValue) {
				mMaxValue = mValue;
			} // カロリーのために加速度ベクトルの最大値
			if (mValue > 100) {
				mFlag = true;
			} else {
				mFlag = false;
			} // 回数のために閾値以上か以下かを判定

			// フラグが切り替わっていれば回数を数える
			if (mFlag != mPreFlag) {

				// 補正処理
				if (mTmpCount > COLLECTION_CONSTANT) {
					mCount++;
					mTmpCount = 0;
				} else {
					mTmpCount++;
				}

				mCal += mMaxValue * mMaxValue / 100000;
				mTextView.setText(String.valueOf(mCount));
				mTextViewCal.setText(String.valueOf(getKCal(mCal)));
				mMaxValue = 0;

			}
			mPreFlag = mFlag;
			mMission.showEnevt(mCount, (int)getKCal(mCal));
		}

	}

	public float getKCal(float cal) {
		float f = mCal / 1000;
		int tmp = (int) (f * 100);

		return (float) (tmp / 100.00);
	}

}
