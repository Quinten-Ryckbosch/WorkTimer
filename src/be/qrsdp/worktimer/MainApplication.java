package be.qrsdp.worktimer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MainApplication extends Application {

	private ArrayList<WorkLog> workLogs = null;
	private WorkLog currentLog = null;
	private boolean atWork;
	private boolean dataBaseLoaded = false;

	private WorkDBHelper dataBaseHelper;

	@Override
	public void onCreate() {

		System.out.println("MainApplication is Created.");
		
		dataBaseLoaded = false;
		dataBaseHelper = new WorkDBHelper(getApplicationContext());
		//dataBaseHelper.spoofDataBase();

		super.onCreate();
	}

	public void loadCurrentWorkLog(){
		if(!dataBaseLoaded){
			this.currentLog = dataBaseHelper.getCurrentLog();
			this.atWork = (this.currentLog != null);
		}
	}

	public void loadAllWorkLogs(){
		if(!dataBaseLoaded){
			workLogs = dataBaseHelper.getAllRecords();
			Collections.sort(workLogs);
	
			atWork = isAtWork(workLogs);
			dataBaseLoaded = true;
		}
	}

	private boolean isAtWork(ArrayList<WorkLog> workLogs){
		for(WorkLog log: workLogs){
			if(log.isCurrent()){
				this.currentLog = log;
				return true;
			}
		}
		return false;
	}

	public boolean isAtWork(){
		return atWork;
	}

	void toggle(){
		if(!atWork){
			//Start new workBlock
			currentLog = new WorkLog();
			workLogs.add(currentLog);
			Collections.sort(workLogs);
			dataBaseHelper.insertRecord(currentLog);
		} else {
			//End last workBlock
			currentLog.endWorkBlock();
			dataBaseHelper.updateRecord(currentLog);

		}
		atWork = !atWork;
	}
	

	public String getLastLogs() {
		//Per Week:
		int weekNumber = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
		//ArrayList<WorkLog> weekList = WorkLog.getLogsOfWeek(weekNumber, workLogs);
		String lastLogs = getLogsOfWeek(weekNumber);
		lastLogs += getLogsOfWeek(weekNumber-1);
		return lastLogs;
	}
	
	private String getLogsOfWeek(int weekNumber){
		Calendar[] days = WorkLog.getFirstAndLastDayOfWeek(weekNumber, workLogs);
		String logsOfWeek = WorkLog.getWeekString(days) + " \t "
				+ WorkLog.getDurationOfWeek(weekNumber, workLogs) + " hours \n";
		//Per Day:
		Calendar day = days[1];
		for(int i=0; i<7; i++){
			ArrayList<WorkLog> dayList = WorkLog.getLogsOfDay(day, workLogs);
			if(dayList.size() > 0){
				logsOfWeek += "\t" + WorkLog.getDayString(day) + "  \t" + WorkLog.getDurationOfDay(day, workLogs) + " hours \n";
				for(WorkLog log: dayList){
					logsOfWeek += "\t\t" + log.getLogString() + "\n";
				}
			}
			day.add(Calendar.DAY_OF_WEEK, -1);
		}
		return logsOfWeek;
	}


}

class WorkDBHelper extends SQLiteOpenHelper {
	private final static String DBHELPER_TAG = "WorkDBHelper";

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "WorkLog.db";
	private static final String TABLE_CREATE = "CREATE TABLE Log ( StartTime TEXT, StopTime TEXT);";

	WorkDBHelper(Context context) {
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
	
	public void spoofDataBase(){
		SQLiteDatabase dataBase = this.getWritableDatabase();
		dataBase.delete("log", null, null);
		dataBase.close();
		
		Calendar now = Calendar.getInstance();
		for(int i=0; i<5; i++){
			Calendar from = (Calendar) now.clone();
			from.add(Calendar.DAY_OF_MONTH, -1 * i);
			Calendar to = (Calendar) from.clone();
			to.add(Calendar.HOUR, -7);
			insertRecord(new WorkLog(WorkLog.parseWorkLog(to), WorkLog.parseWorkLog(from)));
		}
	}

	public long insertRecord(WorkLog log){
		SQLiteDatabase dataBase = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("StartTime", WorkLog.parseWorkLog(log.getStartTime()));
		values.put("StopTime", WorkLog.parseWorkLog(log.getStopTime()));
		long rowId = dataBase.insert("log", null, values);

		Log.d(DBHELPER_TAG, "Record inserted:");
		Log.d(DBHELPER_TAG, "\tStartTime: " + WorkLog.parseWorkLog(log.getStartTime()));
		Log.d(DBHELPER_TAG, "\tStopTime:  " + WorkLog.parseWorkLog(log.getStopTime()));

		dataBase.close();
		return rowId;
	}

	public WorkLog getCurrentLog(){
		SQLiteDatabase dataBase = this.getWritableDatabase();
		String[] whereArgs = new String[] {
				"null"
		};
		String queryString =
				"SELECT * FROM log " +
						"WHERE StopTime = ?";
		Cursor data = dataBase.rawQuery(queryString, whereArgs);
		Log.d(DBHELPER_TAG, "Record updated: " + queryString);

		if(data.moveToFirst()){
			return new WorkLog(data.getString(0), data.getString(1));
		}
		return null;
	}

	public void updateRecord(WorkLog log){
		SQLiteDatabase dataBase = this.getWritableDatabase();
		String updateQuery = "UPDATE log SET StopTime='" + WorkLog.parseWorkLog(log.getStopTime())
				+ "' WHERE StartTime='" + WorkLog.parseWorkLog(log.getStartTime()) + "'";
		Log.d(DBHELPER_TAG, "Record updated: " + updateQuery);
		//TODO use dataBase.update instead of raw sql command.
		dataBase.execSQL(updateQuery);
	}

	public ArrayList<WorkLog> getAllRecords(){
		ArrayList<WorkLog> workLogs = new ArrayList<WorkLog>();
		String selectQuery = "SELECT  * FROM log";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				WorkLog current = new WorkLog(cursor.getString(0), cursor.getString(1));

				Log.d(DBHELPER_TAG, "Record loaded:");
				Log.d(DBHELPER_TAG, "\t" + current.getTimeString());

				workLogs.add(current);
			} while (cursor.moveToNext());
		}

		Log.d(DBHELPER_TAG, "Number of records returned: " + workLogs.size());

		return workLogs;

	}
}

