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
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;

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
