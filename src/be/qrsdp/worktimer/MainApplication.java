package be.qrsdp.worktimer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import be.qrsdp.utils.Util;
import be.qrsdp.worktimer.data.DatabaseHelper;
import be.qrsdp.worktimer.data.WorkDay;
import be.qrsdp.worktimer.data.WorkLog;
import be.qrsdp.worktimer.data.WorkWeek;
import be.qrsdp.worktimer.gui.Notification;

public class MainApplication extends Application {
	private final static String LOG_TAG = "MainApplication";

	public static final String KEY_PREF_SHOW_NOTIFICATOIN = "PREF_SHOW_NOTIFICATION";
	public static final String KEY_PREF_WORK_SSID = "PREF_WORK_SSID";

	// private ArrayList<WorkLog> workLogs = null;
	// private Map<Integer, WorkDay> workDays = null;
	private SparseArray<WorkWeek> workWeeks = null;
	private WorkLog currentLog = null;
	private boolean atWork;
	private boolean dataBaseLoaded = false;

	//settings
	private String workNetworkSSID;
	private boolean alwaysShowNotification;

	public int showWeekNumber, showYear;

	//HelperClasses
	private DatabaseHelper dataBaseHelper;
	private Notification notification;

	@Override
	public void onCreate() {

		Log.d(LOG_TAG, "is Created.");

		dataBaseLoaded = false;
		dataBaseHelper = new DatabaseHelper(getApplicationContext());
		// dataBaseHelper.spoofDataBase();

		// Weeknumber
		resetWeekToShow();

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPref.registerOnSharedPreferenceChangeListener(new WorkTimePreferenceChangeListener(this));
		//TODO unregister at appropriate time (whenever that is...)
		alwaysShowNotification = sharedPref.getBoolean(KEY_PREF_SHOW_NOTIFICATOIN, true);
		workNetworkSSID = sharedPref.getString(KEY_PREF_WORK_SSID, "");
		
		// Create the notification
		setNotification(new Notification(this, alwaysShowNotification));
		

		super.onCreate();
	}
	
	//setter and Getters
	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
	
	public boolean isAlwaysShowNotification() {
		return alwaysShowNotification;
	}

	public void setAlwaysShowNotification(boolean alwaysShowNotification) {
		this.alwaysShowNotification = alwaysShowNotification;
	}
	
	public String getWorkNetworkSSID() {
		return workNetworkSSID;
	}

	public void setWorkNetworkSSID(String workNetworkSSID) {
		this.workNetworkSSID = workNetworkSSID;
	}
	
	public boolean isAtWork() {
		return atWork;
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
	
	//application stuff
	
	public void resetWeekToShow() {
		this.showWeekNumber  = getTodaysWeekNumber();
		this.showYear = getTodaysYear();
	}
	
	private int getTodaysWeekNumber() {
		return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
	}

	private int getTodaysYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
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

	
	public WorkWeek getWorkWeek(Calendar cal) {
		return getWorkWeek(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.YEAR));
	}

	public WorkWeek getWorkWeek(int weekNumber, int year) {
		if (workWeeks.get(Util.getWeekIndex(weekNumber, year)) == null) {
			workWeeks.put(Util.getWeekIndex(weekNumber, year), new WorkWeek(weekNumber, year));
		}
		return workWeeks.get(Util.getWeekIndex(weekNumber, year));
	}

	/*
	 * OLDCODE
	 * public WorkDay getWorkDayLog(Calendar cal) {
		return getWorkWeek(cal).getDay(cal);
	}*/
	
	public boolean addNewWorkLog(WorkLog log){
		return getWorkWeek(log.getStartTime()).addWorkLog(log);
	}
	
	public WorkLog startNewWorkLog() {
		currentLog = new WorkLog();
		addNewWorkLog(currentLog);
		
		//OLDCODE
		//WorkDay day = getWorkDayLog(currentLog.getStartTime());
		//day.addWorkLog(currentLog);
		return currentLog;
	}

	public WorkLog endCurrWorkLog() {
		getWorkWeek(currentLog.getStartTime()).endWorkLog(currentLog);
		
		//OLDCODE
		//WorkDay day = getWorkDayLog(currentLog.getStartTime());
		//day.endWorkLog(currentLog);
		return currentLog;
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
		getNotification().updateNotification(isAtWork());
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
		getNotification().updateNotification(isAtWork());
	}

	public HashMap<WorkDay, List<WorkLog>> getLogsOfWeek(int weekNumber, int year) {
		while(Util.cleanLogs(this, getWorkWeek(weekNumber, year))){
			loadAllWorkLogs(true);
		}
		return getWorkWeek(weekNumber, year).getLogs();
	}

	//Database stuff
	
	public void loadCurrentWorkLog() {
		if (!dataBaseLoaded) {
			this.currentLog = dataBaseHelper.getCurrentLog();
			this.atWork = (this.currentLog != null);
			
			getNotification().updateNotification(isAtWork());
		}
	}

	public void loadAllWorkLogs(boolean forse){
		if(forse || !dataBaseLoaded){
			ArrayList<WorkLog> workLogs = dataBaseHelper.getAllRecords();
			Collections.sort(workLogs);

			atWork = isAtWork(workLogs);
			dataBaseLoaded = true;

			workWeeks = new SparseArray<WorkWeek>();
			for (WorkLog log : workLogs) {
				addNewWorkLog(log);
				//OLDCODE getWorkDayLog(log.getStartTime()).addWorkLog(log);
			}
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
