package be.qrsdp.worktimer;

import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;

public class WorkLog implements Comparable<WorkLog> {
	private final static String WORKLOG_TAG = "WorkLog";

	// Holds time (and other things?) for 1 work block
	private Calendar startTime, stopTime = null;
	private boolean current = false;
	//Duration of this log in seconds;
	private int duration = -1;
	
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
		duration = (int)(this.stopTime.getTimeInMillis() - this.startTime.getTimeInMillis())/1000;
		Log.d(WORKLOG_TAG, "Stoped Working: " + getTimeString());
	}
	
	public int getDurationInSec(){
		if(duration < 0){
			if(this.stopTime == null){
				return (int)(Math.round(Calendar.getInstance().getTimeInMillis() - this.startTime.getTimeInMillis())/1000.0);
			} else {
				duration = (int)(Math.round(this.stopTime.getTimeInMillis() - this.startTime.getTimeInMillis())/1000.0);
			}
		}
		return duration;
	}
	
	public int getDurationInMin(){
		return (int)Math.round(getDurationInSec()/60.0);
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
		
		return time += " - " + this.getDurationInMin();
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
	
	public static ArrayList<WorkLog> getLogsBetween(Calendar from, Calendar to, ArrayList<WorkLog> list){
		ArrayList<WorkLog> partialList = new ArrayList<WorkLog>();
		
		for(WorkLog log: list){
			if(log.getStartTime().after(from) && log.getStartTime().before(to)){
				partialList.add(log);
			}
		}
		
		return partialList;
	}
	
	public static int getDurationBetweenInHours(Calendar from, Calendar to, ArrayList<WorkLog> list){
		int duration = 0;
		ArrayList<WorkLog> parialList = WorkLog.getLogsBetween(from, to, list);
		System.err.println("parialList from " + getTime(from) + " to " + getTime(to) + " : " + parialList.size());
		for(WorkLog log: parialList){
			duration = log.getDurationInMin();
		}
		
		return (int)Math.round(duration / 60.0);
	}
	
	public static int getDurationOfWeek(int weekNumber, ArrayList<WorkLog> list){
		Calendar from = Calendar.getInstance();
		from.set(2014, 0, 0, 0, 0, 0);
		from.set(Calendar.WEEK_OF_YEAR, weekNumber);
		Calendar to   = Calendar.getInstance();
		to.set(2014, 0, 0, 0, 0, 0);
		to.set(Calendar.WEEK_OF_YEAR, weekNumber + 1);
		return getDurationBetweenInHours(from, to, list);
	}

	public int compareTo(WorkLog arg0) {
		return (arg0.getStartTime().compareTo(this.getStartTime()));
	}
	
}


