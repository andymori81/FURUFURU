package net.skysemi.android.app.furufuru;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;
import android.util.Log;



public class MissionEvent {
	public static final String DIALOG_MESSAGE = "message";
	public static final String SHOW_BADGE_COUNT = "show_badge_count";
	private Context mContext;
	private int mShowCheckCount = -1;
	private int mShowCheckCalorie = -1;
	private boolean mMissionOf3dayFlag, mMissionOf7dayFlag, mMissionOf14dayFlag, mMissionOf30dayFlag;
	private SharedPreferences mPreference;
	public static final String MISSION_PREFERENCE = "mission_preference";
	public static final String BADGE_TOTAL = "badge_total";
	private String mNowDateString;
	private static final String LAST_DAY_MISSION_3DAYS = "mission_3days";
	private static final String LAST_DAY_MISSION_7DAYS = "mission_7days";
	private static final String LAST_DAY_MISSION_14DAYS = "mission_14days";
	private static final String LAST_DAY_MISSION_30DAYS = "mission_30days";


	/** 誤実装を防ぐためにコンストラクタ以外でインスタンス生成 */
	public MissionEvent getMissionEventInstance(Context context){
		mContext = context;

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		mNowDateString = df.format(cal.getTime());

		String[] date = this.createBackNumberArray(cal, df);
		for(int i=0; i<date.length; i++){
			Log.d("date"+ String.valueOf(i), date[i]);
		}
		mPreference = mContext.getSharedPreferences(MISSION_PREFERENCE, Context.MODE_PRIVATE);

		SQLiteDatabase db = new SQLiteUtili().createDB(mContext);

		try {
			mMissionOf3dayFlag = this.getMissionFlag(db, date,
					mPreference.getString(LAST_DAY_MISSION_3DAYS, null), 3, 120);

			mMissionOf7dayFlag = this.getMissionFlag(db, date,
					mPreference.getString(LAST_DAY_MISSION_7DAYS, null), 7, 130);

			mMissionOf14dayFlag = this.getMissionFlag(db, date,
					mPreference.getString(LAST_DAY_MISSION_14DAYS, null), 14, 140);

			mMissionOf30dayFlag = this.getMissionFlag(db, date,
					mPreference.getString(LAST_DAY_MISSION_30DAYS, null), 30, 150);

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			db.close();
		}

		return this;
	}


	/** イベント処理 */
	public void showEnevt(int count, int calorie){

		// カロリーミッション処理
		if(calorie == 0){
			if(calorie != mShowCheckCalorie){
				mShowCheckCalorie = calorie;
				SharedPreferences.Editor editor = mPreference.edit();
				editor.putInt(BADGE_TOTAL, mPreference.getInt(BADGE_TOTAL, 0) + 1);
				editor.commit();
				this.showDialog(mContext.getString(R.string.missionEvent_showDialogMessage_A, 100), 1);
			}
		}
		else if(calorie == 120 && mMissionOf3dayFlag){
			if(calorie != mShowCheckCalorie){
				mShowCheckCalorie = calorie;
				SharedPreferences.Editor editor = mPreference.edit();
				editor.putString(LAST_DAY_MISSION_3DAYS, mNowDateString);
				editor.putInt(BADGE_TOTAL, mPreference.getInt(BADGE_TOTAL, 0) + 2);
				editor.commit();
				this.showDialog(mContext.getString(R.string.missionEvent_showDialogMessage_B, 3, 120), 2);
			}
		}
		else if(calorie == 130 && mMissionOf7dayFlag){
			if(calorie != mShowCheckCalorie){
				mShowCheckCalorie = calorie;
				SharedPreferences.Editor editor = mPreference.edit();
				editor.putString(LAST_DAY_MISSION_7DAYS, mNowDateString);
				editor.putInt(BADGE_TOTAL, mPreference.getInt(BADGE_TOTAL, 0) + 3);
				editor.commit();
				this.showDialog(mContext.getString(R.string.missionEvent_showDialogMessage_B, 7, 130), 3);
			}
		}
		else if(calorie == 140 && mMissionOf14dayFlag){
			if(calorie != mShowCheckCalorie){
				mShowCheckCalorie = calorie;
				SharedPreferences.Editor editor = mPreference.edit();
				editor.putString(LAST_DAY_MISSION_14DAYS, mNowDateString);
				editor.putInt(BADGE_TOTAL, mPreference.getInt(BADGE_TOTAL, 0) + 4);
				editor.commit();
				this.showDialog(mContext.getString(R.string.missionEvent_showDialogMessage_B, 14, 140), 4);
			}
		}
		else if(calorie == 150 && mMissionOf30dayFlag){
			if(calorie != mShowCheckCalorie){
				mShowCheckCalorie = calorie;
				SharedPreferences.Editor editor = mPreference.edit();
				editor.putString(LAST_DAY_MISSION_30DAYS, mNowDateString);
				editor.putInt(BADGE_TOTAL, mPreference.getInt(BADGE_TOTAL, 0) + 5);
				editor.commit();
				this.showDialog(mContext.getString(R.string.missionEvent_showDialogMessage_B, 30, 150), 5);
			}
		}

		// キリ番処理
		else if(count == 100 || count == 500 || (count % 1000 == 0 && count != 0)){
			if(count != mShowCheckCount){
				mShowCheckCount = count;
				Vibrator vib = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
				vib.vibrate(200);

			}
		}
	}

	public String getNowDate(){
		return mNowDateString;
	}

	/** 自作ダイアログ表示 */
	private void showDialog(String message, int badge){
		Intent intent = new Intent(mContext, MissionEventDialog.class);
		intent.putExtra(DIALOG_MESSAGE, message);
		intent.putExtra(SHOW_BADGE_COUNT, badge);
		mContext.startActivity(intent);
		((Activity)mContext).overridePendingTransition(R.animator.fade_in, R.animator.screen_keep);
	}

	/** 過去30日分の日付をデータベースで格納しているフォーマットで取得 */
	private String[] createBackNumberArray(Calendar cal, SimpleDateFormat df){
		String[] date = new String[29];

		for(int i=0; i<date.length; i++){
			cal.add(Calendar.DAY_OF_MONTH, -1);
			date[i] = df.format(cal.getTime());
		}

		return date;
	}

	/** ミッションの発生フラグの生成 */
	private boolean getMissionFlag(SQLiteDatabase db,
			String[] dateArray, String finalDate, int checkDays, int checkCal) throws Exception {

		String[] var = new String[checkDays -1];
		// 計測期間内にミッション達成経歴が無いか判定
		for(int i=0; i<var.length; i++){
			if(dateArray[i].equals(finalDate)){
				return false;
			}else{
				var[i] = dateArray[i];
			}
		}
		// ミッションに対応した期間のデータを取得
		ArrayList<FuruStatus> list = new ArrayList<FuruStatus>(
				new SQLiteUtili().readDB(db, var));

		// 期間内毎日稼動しているか判定
		if(list.size() != checkDays - 1){
			return false;
		}else{
			// 規定のカロリーをクリアしているか判定
			for(int i=0; i<list.size(); i++){
				if((int)list.get(i).getCalorie() < checkCal){
					return false;
				}
			}
		}
		// 全ての条件をクリアしていればTUREを返す
		return true;
	}


}
