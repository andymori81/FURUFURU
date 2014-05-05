package net.skysemi.android.app.furufuru;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.plot.PlotOrientation;
import org.afree.data.xy.XYSeriesCollection;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RecordViewActivity extends Activity implements OnClickListener {

	private ArrayList<FuruStatus> mList;
	private Button mCountTab;
	private Button mCalorieTab;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_record_view);

		mCountTab = (Button) findViewById(R.id.activity_record_view_countTab);
		mCountTab.setOnClickListener(this);
		mCalorieTab = (Button) findViewById(R.id.activity_record_view_calorieTab);
		mCalorieTab.setOnClickListener(this);
		
		// トータルバッジ数表示
		TextView totalBadge = (TextView)findViewById(R.id.activity_record_view_totalBadge);
		SharedPreferences pref = getSharedPreferences(MissionEvent.MISSION_PREFERENCE, Context.MODE_PRIVATE);
		totalBadge.setText(String.valueOf(pref.getInt(MissionEvent.BADGE_TOTAL, 0)));

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-");
		String yearAndMonth = df.format(cal.getTime());
		Log.d("yearAndMonth", yearAndMonth);
		SQLiteDatabase db = new SQLiteUtili().createDB(this);
		
		try {
			mList = new SQLiteUtili().readDB(db, new String[] {yearAndMonth});
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			db.close();
		}

		float[] calorieArray = this.getCalorieArray(this
				.createRecordList(mList));
		ChartView chartView = (ChartView) findViewById(R.id.chartView);
		XYSeriesCollection dataset = chartView.getDataSet(calorieArray);
		AFreeChart chart = ChartFactory.createXYLineChart("タイトル", "X軸ラベル",
				"ｙ軸ラベル", dataset, PlotOrientation.VERTICAL, false, true, false);
		chartView.setCalorieChart(chart);
	}

	@Override
	public void onClick(View v) {
		if (v == mCalorieTab) {
			float[] calorieArray = this.getCalorieArray(this
					.createRecordList(mList));
			ChartView chartView = (ChartView) findViewById(R.id.chartView);
			XYSeriesCollection dataset = chartView.getDataSet(calorieArray);
			AFreeChart chart = ChartFactory.createXYLineChart("タイトル", "X軸ラベル",
					"ｙ軸ラベル", dataset, PlotOrientation.VERTICAL, false, true,
					false);
			chartView.setCalorieChart(chart);
		}

		if (v == mCountTab) {
			int[] countArray = this.getCountArray(this.createRecordList(mList));
			ChartView chartView = (ChartView) findViewById(R.id.chartView);
			XYSeriesCollection dataset = chartView.getDataSet(countArray);
			AFreeChart chart = ChartFactory.createXYLineChart("タイトル", "X軸ラベル",
					"ｙ軸ラベル", dataset, PlotOrientation.VERTICAL, false, true,
					false);
			chartView.setCountChart(chart);
		}

	}

	public int[] getCountArray(ArrayList<RecordStatus> list) {
		int[] countArray = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			countArray[i] = list.get(i).getCount();
		}
		return countArray;
	}

	public float[] getCalorieArray(ArrayList<RecordStatus> list) {
		float[] calorieArray = new float[list.size()];
		for (int i = 0; i < list.size(); i++) {
			calorieArray[i] = list.get(i).getCalorie();
		}
		return calorieArray;
	}

	public ArrayList<RecordStatus> createRecordList(ArrayList<FuruStatus> list) {
		ArrayList<RecordStatus> recodeList = new ArrayList<RecordStatus>();

		Time time = new Time("Asia/Tokyo");
		time.setToNow();
		int nowDate = time.monthDay;

		for (int i = 0; i < nowDate; i++) {
			for (int j = 0; j < list.size(); j++) {
				
				if (Integer.parseInt(list.get(j).getDate().substring(8))
						- (i + 1) == 0) {
					recodeList.add(i, new RecordStatus(list.get(j).getCount(),
							list.get(j).getCalorie()));
					break;
				} else {
					if (j == (list.size() - 1)) {
						recodeList.add(i, new RecordStatus(0, 0));
					}
				}
			}
		}

		return recodeList;
	}

	class RecordStatus {

		private int mCount;
		private float mCalorie;

		public RecordStatus(int count, float calorie) {
			mCount = count;
			mCalorie = calorie;
		}

		public int getCount() {
			return mCount;
		}

		public float getCalorie() {
			return mCalorie;
		}
	}

}
