package be.qrsdp.worktimer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.util.SparseArray;
import be.qrsdp.utils.Util;
import be.qrsdp.worktimer.gui.HomeScreen;

public class MainApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private final static String LOG_TAG = "MainApplication";

	public static final String KEY_PREF_SHOW_NOTIFICATOIN = "PREF_SHOW_NOTIFICATION";
	public static final String KEY_PREF_WORK_SSID = "PREF_WORK_SSID";

	// private ArrayList<WorkLog> workLogs = null;
	// private Map<Integer, WorkDay> workDays = null;
	private SparseArray<WorkWeek> workWeeks = null;
	private WorkLog currentLog = null;
	private boolean atWork;
	private boolean dataBaseLoaded = false;

	private String workNetworkSSID;

	private boolean showNotification;

	public int showWeekNumber, showYear;

	private WorkDBHelper dataBaseHelper;

	NotificationCompat.Builder mBuilder;
	int notifyID = 1;

	@Override
	public void onCreate() {

		Log.d(LOG_TAG, "is Created.");

		dataBaseLoaded = false;
		dataBaseHelper = new WorkDBHelper(getApplicationContext());
		// dataBaseHelper.spoofDataBase();

		// Weeknumber
		showWeekNumber = getTodaysWeekNumber();
		showYear = getTodaysYear();

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPref.registerOnSharedPreferenceChangeListener(this);
		//TODO unregister at appropriate time (whenever that is...)
		
		showNotification = sharedPref.getBoolean(KEY_PREF_SHOW_NOTIFICATOIN, true);
		// Create the notification
		loadNotification();
		

		super.onCreate();
	}
	
	public void resetWeekToShow() {
		Calendar now = Calendar.getInstance();
		this.showWeekNumber  = now.get(Calendar.WEEK_OF_YEAR);
		this.showYear = now.get(Calendar.YEAR);
		
	}

	public void loadCurrentWorkLog() {
		if (!dataBaseLoaded) {
			this.currentLog = dataBaseHelper.getCurrentLog();
			this.atWork = (this.currentLog != null);
		}
	}

	public WorkWeek getWorkWeek(Calendar cal) {
		return getWorkWeek(cal.get(Calendar.WEEK_OF_YEAR),
				cal.get(Calendar.YEAR));
	}

	public WorkWeek getWorkWeek(int weekNumber, int year) {
		if (workWeeks.get(Util.getWeekIndex(weekNumber, year)) == null) {
			workWeeks.put(Util.getWeekIndex(weekNumber, year), new WorkWeek(
					weekNumber, year));
		}
		return workWeeks.get(Util.getWeekIndex(weekNumber, year));
	}

	public WorkDay getWorkDayLog(Calendar cal) {
		return getWorkWeek(cal).getDay(cal);
	}

	public void loadAllWorkLogs(boolean forse){
		if(forse || !dataBaseLoaded){
			ArrayList<WorkLog> workLogs = dataBaseHelper.getAllRecords();
			Collections.sort(workLogs);

			atWork = isAtWork(workLogs);
			dataBaseLoaded = true;

			workWeeks = new SparseArray<WorkWeek>();
			for (WorkLog log : workLogs) {
				if (getWorkDayLog(log.getStartTime()) == null) {
					System.err.println("Heel raar");
				}
				getWorkDayLog(log.getStartTime()).addWorkLog(log);
			}
		}
	}

	public WorkLog startNewWorkLog() {
		currentLog = new WorkLog();
		WorkDay day = getWorkDayLog(currentLog.getStartTime());
		day.addWorkLog(currentLog);
		return currentLog;
	}

	public WorkLog endCurrWorkLog() {
		WorkDay day = getWorkDayLog(currentLog.getStartTime());
		day.endWorkLog(currentLog);
		return currentLog;
	}

	private boolean isAtWork(ArrayList<WorkLog> workLogs) {
		for (WorkLog log : workLogs) {
			if (log.isCurrent()) {
				this.currentLog = log;
				return true;
			}
		}
		return false;
	}

	public boolean isAtWork() {
		return atWork;
	}

	public void toggle() {
		if (!atWork) {
			// Start new workBlock and insert in dataBase
			dataBaseHelper.insertRecord(startNewWorkLog());
		} else {
			// End last workBlock and update log in dataBase
			dataBaseHelper.updateRecord(endCurrWorkLog());
		}
		atWork = !atWork;

		// Change notification
		updateNotification();
	}

	public void toggleViaNetwork(boolean newState) {
		WorkLog currentLog = dataBaseHelper.getCurrentLog();
		this.atWork = (currentLog != null);
		
		if (this.atWork != newState) {
			// Prev state was: "Not at work", so no current log
			if (newState) { // only make a new log when the new state is indeed
						   // "At work"
				currentLog = new WorkLog();
				dataBaseHelper.insertRecord(currentLog);
			} else { // only end the current log when the new state is
					// indeed "Not at Work"
				currentLog.endWorkBlock();
				dataBaseHelper.updateRecord(currentLog);
			}
			this.atWork = newState;
		}
		// Change notification
		updateNotification();
	}

	public String getLogsList() {
		return "TODO";
	}

	public int getTodaysWeekNumber() {
		return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
	}

	public int getTodaysYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	public String getWeek(int weekNumber, int year) {
		return workWeeks.get(Util.getWeekIndex(weekNumber, year)).getString();
	}

	public HashMap<String, List<String>> getLogsOfWeek(int weekNumber, int year) {
		HashMap<String, List<String>> logsOfWeek = new HashMap<String, List<String>>();
		System.err.println("Workweek map size: " + workWeeks.size());
		WorkWeek week = getWorkWeek(weekNumber, year);
		while(Util.cleanLogs(this, week)){
			loadAllWorkLogs(true);
			week = getWorkWeek(weekNumber, year);
		}
		System.err.println("index: " + Util.getWeekIndex(weekNumber, year));
		// Per Day:
		for (WorkDay workDay : week.getDays()) {
			if (workDay.getLogs().size() > 0) {
				List<String> dayList = new ArrayList<String>();
				for (WorkLog log : workDay.getLogs()) {
					dayList.add(log.getString() + " \t" + getDurationString(log.getDuration()));
				}
				logsOfWeek.put(workDay.getString() + "  \t" + getDurationString(workDay.getDuration()), dayList);

			}
		}
		return logsOfWeek;
	}

	private String getDurationString(int duration) {
		String time = "";
		if (duration >= 60) {
			time += (int)Math.floor(duration / 60.0) + "u";
		}
		time += duration % 60 + "m";
		return time;
	}

	public void changeWeek(int i) {
		showWeekNumber += i;
		if (showWeekNumber > 52) {
			showWeekNumber -= 52;
			showYear++;
		}
		if (showWeekNumber < 1) {
			showWeekNumber += 52;
			showYear--;
		}
	}

	public String getWorkNetworkSSID() {
		return workNetworkSSID;
	}

	private void loadNotification() {
		// Creating a notification
		mBuilder = new NotificationCompat.Builder(this);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, HomeScreen.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(HomeScreen.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		updateNotification();
	}

	private void updateNotification() {
		mBuilder.setSmallIcon(isAtWork() ? R.drawable.working : R.drawable.notworking);
		mBuilder.setContentTitle("WorkLogger");
		mBuilder.setContentText(isAtWork() ? "Working" : "Not working");
		mBuilder.setOngoing(true);
		
		Notification notification = mBuilder.build();
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		if (showNotification) {
			mNotificationManager.notify(notifyID, notification);
		} else {
			mNotificationManager.cancel(notifyID);
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (key.equals(KEY_PREF_SHOW_NOTIFICATOIN)) {
			showNotification = sharedPreferences.getBoolean(key, true);

			if (showNotification) {
				loadNotification();
			} else {
				updateNotification();
			}
		}
		if (key.equals(KEY_PREF_WORK_SSID)) {
			workNetworkSSID = sharedPreferences.getString(key, "");
		}
	}
	
	public void deleteLog(WorkLog log){
		dataBaseHelper.deleteRecord(log);
	}
	
	/**
	 * Update the stopTime for this log based on the startTime
	 * @param log
	 */
	public void updateLog(WorkLog log){
		dataBaseHelper.updateRecord(log);
	}
	
	public void addLog(WorkLog log){
		dataBaseHelper.insertRecord(log);
	}
	
	//DEBUG
	public void spoofDataBase(){
		dataBaseHelper.spoofDataBase();
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
