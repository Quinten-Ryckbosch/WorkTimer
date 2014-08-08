package be.qrsdp.worktimer.data;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final static String DBHELPER_TAG = "WorkDBHelper";

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "WorkLog.db";
	private static final String TABLE_CREATE = "CREATE TABLE Log ( StartTime TEXT, StopTime TEXT);";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
		Log.d(DBHELPER_TAG, "DataBase " + DATABASE_NAME + " is created.");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void spoofDataBase() {
		SQLiteDatabase dataBase = this.getWritableDatabase();
		dataBase.delete("log", null, null);
		dataBase.close();

		Calendar now = Calendar.getInstance();
		for (int i = 0; i < 5; i++) {
			Calendar from = (Calendar) now.clone();
			from.add(Calendar.DAY_OF_MONTH, -1 * i);
			Calendar to = (Calendar) from.clone();
			to.add(Calendar.HOUR, 7);
			insertRecord(new WorkLog(from, to));
			from = (Calendar) to.clone();
			from.add(Calendar.MINUTE, 1);
			to = (Calendar) from.clone();
			to.add(Calendar.HOUR, 1);
			insertRecord(new WorkLog(from, to));
			from = (Calendar) to.clone();
			from.add(Calendar.HOUR, 1);
			to = (Calendar) from.clone();
			to.add(Calendar.MINUTE, 1);
			insertRecord(new WorkLog(from, to));
		}
	}

	public long insertRecord(WorkLog log) {
		SQLiteDatabase dataBase = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("StartTime", WorkLog.parseWorkLog(log.getStartTime()));
		values.put("StopTime", WorkLog.parseWorkLog(log.getStopTime()));
		long rowId = dataBase.insert("log", null, values);

		Log.d(DBHELPER_TAG, "Record inserted:");
		Log.d(DBHELPER_TAG,
				"\tStartTime: " + WorkLog.parseWorkLog(log.getStartTime()));
		Log.d(DBHELPER_TAG,
				"\tStopTime:  " + WorkLog.parseWorkLog(log.getStopTime()));

		dataBase.close();
		return rowId;
	}

	public WorkLog getCurrentLog() {
		SQLiteDatabase dataBase = this.getWritableDatabase();
		String[] whereArgs = new String[] { "null" };
		String queryString = "SELECT * FROM log " + "WHERE StopTime = ?";
		Cursor data = dataBase.rawQuery(queryString, whereArgs);
		Log.d(DBHELPER_TAG, "Record updated: " + queryString);

		if (data.moveToFirst()) {
			return new WorkLog(data.getString(0), data.getString(1));
		}
		return null;
	}

	public void updateRecord(WorkLog log) {
		SQLiteDatabase dataBase = this.getWritableDatabase();
		String updateQuery = "UPDATE log SET StopTime='"
				+ WorkLog.parseWorkLog(log.getStopTime())
				+ "' WHERE StartTime='"
				+ WorkLog.parseWorkLog(log.getStartTime()) + "'";
		Log.d(DBHELPER_TAG, "Record updated: " + updateQuery);
		// TODO use dataBase.update instead of raw sql command.
		dataBase.execSQL(updateQuery);
	}
	
	public void deleteRecord(WorkLog log){
		SQLiteDatabase dataBase = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM log WHERE StopTime='" + WorkLog.parseWorkLog(log.getStopTime())
				+ "' AND StartTime='" + WorkLog.parseWorkLog(log.getStartTime()) + "'";
		Log.d(DBHELPER_TAG, "Record deleted: " + deleteQuery);
		dataBase.execSQL(deleteQuery);
	}

	public ArrayList<WorkLog> getAllRecords() {
		ArrayList<WorkLog> workLogs = new ArrayList<WorkLog>();
		String selectQuery = "SELECT  * FROM log";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				WorkLog current = new WorkLog(cursor.getString(0),
						cursor.getString(1));

				Log.d(DBHELPER_TAG, "Record loaded:\t" + current.getString());

				workLogs.add(current);
			} while (cursor.moveToNext());
		}

		Log.d(DBHELPER_TAG, "Number of records returned: " + workLogs.size());

		return workLogs;

	}

}
