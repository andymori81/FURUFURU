package net.skysemi.android.app.furufuru;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteUtili {

	public static final String DB_NAME = "furu_db";
	public static final String DB_TABLE = "furu_table";
	public static final int DB_VERSION = 1;
	public static final String _ID = "_id";
	public static final String DATE = "Date";
	public static final String COUNT = "Count";
	public static final String CALORIE = "Calorie";

	/** データベース作成 */
	public SQLiteDatabase createDB(Context context) {
		return new DBHelper(context).getWritableDatabase();
	}

	/** レコード作成 */
	public void writeDB(SQLiteDatabase db, String dat, int cnt, float cal)
			throws Exception {
		ContentValues values = new ContentValues();
		values.put(DATE, dat);
		values.put(COUNT, cnt);
		values.put(CALORIE, cal);
		db.insert(DB_TABLE, "", values);
	}

	/** テーブル読み込み */
	public ArrayList<FuruStatus> readDB(SQLiteDatabase db) throws Exception {
		ArrayList<FuruStatus> list = new ArrayList<FuruStatus>();

		Cursor c = db.query(DB_TABLE,
				new String[] { _ID, DATE, COUNT, CALORIE }, "", null, null,
				null, null);

		if (c.getCount() != 0) {
			c.moveToFirst();
			// リストの作成
			for (int i = 0; i < c.getCount(); i++) {
				list.add(new FuruStatus(c.getInt(0), c.getString(1), c
						.getInt(2), c.getFloat(3)));
				c.moveToNext();
			}
		}
		c.close();
		return list;
	}

	public ArrayList<FuruStatus> readDB(SQLiteDatabase db, String[] selection)
			throws Exception {
		ArrayList<FuruStatus> list = new ArrayList<FuruStatus>();

		String[] param = new String[selection.length];
		for(int i=0; i<selection.length; i++){
			param[i] = "%" + selection[i] + "%";
		}
		String select = DATE + " like ?";
		Cursor c = db.query(DB_TABLE,
				new String[] { _ID, DATE, COUNT, CALORIE }, select,
				param, null, null, null);
				
		if (c.getCount() != 0) {
			c.moveToFirst();
			// リストの作成
			for (int i = 0; i < c.getCount(); i++) {
				list.add(new FuruStatus(c.getInt(0), c.getString(1), c
						.getInt(2), c.getFloat(3)));
				c.moveToNext();
			}
		}
		c.close();
		return list;
	}

	/** レコードの更新 */
	public void upDateRecord(SQLiteDatabase db, int id, String data1,
			int data2, float data3) {
		ContentValues values = new ContentValues();
		values.put(DATE, data1);
		values.put(COUNT, data2);
		values.put(CALORIE, data3);

		db.update(DB_TABLE, values, _ID + "=" + id, null);
	}

	/** レコード削除 */
	public void deleteRecord(SQLiteDatabase db, int id) {
		db.delete(DB_TABLE, _ID + "=" + id, null);
	}

	/** テーブル削除 */
	public void deleteaTable(SQLiteDatabase db) {
		db.execSQL("drop table" + DB_TABLE);
	}

	/** インナークラスでDBHelper定義 */
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table " + DB_TABLE + "(" + _ID
					+ " INTEGER PRIMARY KEY," + DATE + " TEXT," + COUNT
					+ " INTEGER," + CALORIE + " REAL)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO 自動生成されたメソッド・スタブ
		}

	}

}
