package be.qrsdp.worktimer;

import java.util.Calendar;

import android.util.Log;

public class WorkLog implements Comparable<WorkLog> {
	private final static String WORKLOG_TAG = "WorkLog";

	// Holds time (and other things?) for 1 work block
	private Calendar startTime, stopTime = null;
	private boolean current = false;
	
	WorkLog(){
		startWorkBlock();
	}
	
	WorkLog(String startTime, String stopTime){
		this.startTime = parseWorkLog(startTime);
		this.stopTime = parseWorkLog(stopTime);
		current = (this.stopTime == null);
		Log.d(WORKLOG_TAG, "Log parsed: " + getTimeString());
	}
	
	void startWorkBlock(){
		startTime = Calendar.getInstance();
		current = true;
		Log.d(WORKLOG_TAG, "Started Working: " + getTimeString());
	}
	
	void endWorkBlock(){
		stopTime = Calendar.getInstance();
		current = false;
		Log.d(WORKLOG_TAG, "Stoped Working: " + getTimeString());
	}
	
	public boolean isCurrent(){
		return current;
	}
	
	public Calendar getStartTime(){
		return startTime;
	}
	
	public Calendar getStopTime(){
		return stopTime;
	}
	
	public static String getTwoDigitNumber(int i){
		String s = "";
		if(i < 10){
			s += "0";
		}
		s += "" + i;
		return s;
	}
	
	public static String getTime(Calendar time){
		return getTwoDigitNumber(time.get(Calendar.DAY_OF_MONTH))
			+ "/" + getTwoDigitNumber(time.get(Calendar.MONTH))
			+ "/" + time.get(Calendar.YEAR)
			+ " " + getTwoDigitNumber(time.get(Calendar.HOUR_OF_DAY))
			+ ":" + getTwoDigitNumber(time.get(Calendar.MINUTE));
	}
	
	public String getTimeString(){
		String time = "";
		time += getTime(startTime);
		
		if(stopTime == null){
			time += " - current";
		} else {
			time += " - " + getTime(stopTime);
		}
		
		return time;
	}
	
	public static String parseWorkLog(Calendar time){
		if(time == null)
			return "null";
		String ret = "" + time.get(0);
		for(int i=1; i<Calendar.FIELD_COUNT; i++){
			ret += ";" + time.get(i);
		}
		return ret;
	}
	
	public static Calendar parseWorkLog(String time){
		if(time.equalsIgnoreCase("null"))
			return null;
		Calendar calTime = Calendar.getInstance();
		String[] fieldArray = time.split(";");
		for(int i=0; i<fieldArray.length; i++){
			calTime.set(i, Integer.parseInt(fieldArray[i]));
		}
		return calTime;
	}

	public int compareTo(WorkLog arg0) {
		return (arg0.getStartTime().compareTo(this.getStartTime()));
	}
	
}


