package net.skysemi.android.app.furufuru;

public class FuruStatus {

	private int mId;
	private String mDate;
	private int mCount;
	private float mCalorie;

	public FuruStatus() {
	}

	public FuruStatus(int id, String dat, int cnt, float cal) {
		mId = id;
		mDate = dat;
		mCount = cnt;
		mCalorie = cal;
	}

	public void setId(int id) {
		mId = id;
	}

	public void setDate(String date) {
		mDate = date;
	}

	public void setCount(int count) {
		mCount = count;
	}

	public void setCalorie(float calorie) {
		mCalorie = calorie;
	}

	public int getId() {
		return mId;
	}

	public String getDate() {
		return mDate;
	}

	public int getCount() {
		return mCount;
	}

	public float getCalorie() {
		return mCalorie;
	}

}
