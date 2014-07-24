package be.qrsdp.worktimer;

import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;
import be.qrsdp.utils.Util;

public class WorkLog implements Comparable<WorkLog> {
	private final static String WORKLOG_TAG = "WorkLog";
	
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
		//
		Log.d(WORKLOG_TAG, "Log parsed: " + getTotalString());
	}
	
	void startWorkBlock(){
		startTime = Calendar.getInstance();
		current = true;
		Log.d(WORKLOG_TAG, "Started Working: " + getTotalString());
	}
	
	void endWorkBlock(){
		stopTime = Calendar.getInstance();
		current = false;
		duration = (int)(this.stopTime.getTimeInMillis() - this.startTime.getTimeInMillis())/1000;
		Log.d(WORKLOG_TAG, "Stoped Working: " + getTotalString());
	}
	
	public int getDuration(){
		if(this.duration < 0){
			if(this.stopTime == null){
				return  (int)(Math.round(Calendar.getInstance().getTimeInMillis() - this.startTime.getTimeInMillis())/1000.0/60.0);
			} else {
				this.duration = (int)(Math.round(this.stopTime.getTimeInMillis() - this.startTime.getTimeInMillis())/1000.0/60.0);
			}
		}
		return this.duration;
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
	
	public String getString(){
		String time = "";
		time += Util.getTwoDigitNumber(startTime.get(Calendar.HOUR_OF_DAY))
				+ ":" + Util.getTwoDigitNumber(startTime.get(Calendar.MINUTE));
		
		if(stopTime == null){
			time += " - current";
		} else {
			time += " - " + Util.getTwoDigitNumber(stopTime.get(Calendar.HOUR_OF_DAY))
					+ ":" + Util.getTwoDigitNumber(stopTime.get(Calendar.MINUTE));
		}
		
		return time;
	}
	
	public String getTotalString(){
		String time = "";
		time += Util.getTwoDigitNumber(startTime.get(Calendar.DAY_OF_MONTH))
				+ "/" + Util.getTwoDigitNumber(startTime.get(Calendar.MONTH))
				+ "/" + startTime.get(Calendar.YEAR)
				+ " " + Util.getTwoDigitNumber(startTime.get(Calendar.HOUR_OF_DAY))
				+ ":" + Util.getTwoDigitNumber(startTime.get(Calendar.MINUTE));
		
		if(stopTime == null){
			time += " - current";
		} else {
			time += " - " + Util.getTwoDigitNumber(stopTime.get(Calendar.DAY_OF_MONTH))
					+ "/" + Util.getTwoDigitNumber(stopTime.get(Calendar.MONTH))
					+ "/" + stopTime.get(Calendar.YEAR)
					+ " " + Util.getTwoDigitNumber(stopTime.get(Calendar.HOUR_OF_DAY))
					+ ":" + Util.getTwoDigitNumber(stopTime.get(Calendar.MINUTE));;
		}
		
		return time;
	}
	
	/*
	
	*/
	
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
	
	/*public static int getDurationBetween(Calendar from, Calendar to, ArrayList<WorkLog> list){
		int duration = 0;
		ArrayList<WorkLog> parialList = WorkLog.getLogsBetween(from, to, list);
		System.err.println("parialList from " + getTime(from) + " to " + getTime(to) + " : " + parialList.size());
		for(WorkLog log: parialList){
			duration += log.getDurationInMin();
		}
		
		return duration;
	}*/
	
	/*public static ArrayList<WorkLog> getLogsOfWeek(int weekNumber, ArrayList<WorkLog> list){
		Calendar[] days = getFirstAndLastDayOfWeek(weekNumber);
		return WorkLog.getLogsBetween(days[0], days[1], list);
	}*/
	
	/*public static int getDurationOfWeek(int weekNumber, ArrayList<WorkLog> list){
		int duration = 0;
		ArrayList<WorkLog> parialList = WorkLog.getLogsOfWeek(weekNumber, list);
		for(WorkLog log: parialList){
			duration += log.getDurationInMin();
		}
		return duration;
	}*/

	/*public static ArrayList<WorkLog> getLogsOfDay(Calendar day, ArrayList<WorkLog> list) {
		Calendar from = (Calendar) day.clone();
		from.set(Calendar.HOUR_OF_DAY, 0);
		from.set(Calendar.MINUTE, 0);
		from.set(Calendar.SECOND, 0);
		Calendar to = (Calendar) day.clone();
		to.set(Calendar.HOUR_OF_DAY, 23);
		to.set(Calendar.MINUTE, 59);
		to.set(Calendar.SECOND, 59);
		return getLogsBetween(from, to, list);
	}*/
	
	/*public static int getDurationOfDay(Calendar day, ArrayList<WorkLog> list) {
		int duration = 0;
		ArrayList<WorkLog> parialList = WorkLog.getLogsOfDay(day, list);
		for(WorkLog log: parialList){
			duration += log.getDurationInMin();
		}
		return duration;
	}*/
	
	public int compareTo(WorkLog arg0) {
		return (arg0.getStartTime().compareTo(this.getStartTime()));
	}
	
}


