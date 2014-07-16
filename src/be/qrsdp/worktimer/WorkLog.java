package be.qrsdp.worktimer;

import java.util.ArrayList;
import java.util.Calendar;

import android.text.format.Time;
import android.util.Log;

public class WorkLog implements Comparable<WorkLog> {
	private final static String WORKLOG_TAG = "WorkLog";
	private final static String[] MONTH_NAMES_SHORT = {"Jan", "Feb", "Mrt", "Apr", "Mei", "Jun", "Jul", "Aug", "Sept", "Okt", "Nov", "Dec"};

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
	
	public static String getWeekString(Calendar[] days){
		return days[0].get(Calendar.DAY_OF_MONTH)
				+ " " + MONTH_NAMES_SHORT[days[0].get(Calendar.MONTH)]
				+ " - " + days[1].get(Calendar.DAY_OF_MONTH)
				+ " " + MONTH_NAMES_SHORT[days[1].get(Calendar.MONTH)];
	}
	
	public static String getDayString(Calendar day) {
		return day.get(Calendar.DAY_OF_MONTH)
				+ " " + MONTH_NAMES_SHORT[day.get(Calendar.MONTH)];
	}
	
	public String getLogString(){
		String time = "";
		time += getTwoDigitNumber(startTime.get(Calendar.HOUR_OF_DAY))
				+ ":" + getTwoDigitNumber(startTime.get(Calendar.MINUTE));
		
		if(stopTime == null){
			time += " - current";
		} else {
			time += " - " + getTwoDigitNumber(stopTime.get(Calendar.HOUR_OF_DAY))
					+ ":" + getTwoDigitNumber(stopTime.get(Calendar.MINUTE));
		}
		
		return time += " \t " + this.getDurationInMin() + " min.";
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
			duration += log.getDurationInMin();
		}
		
		return (int)Math.round(duration / 60.0);
	}
	
	private static Calendar getFirstDayOfWeek(int weekNumber){
		int year = 2014;
		Calendar cal = Calendar.getInstance();
		cal.set(year, 1, 1, 0, 0, 0);
		cal.set(Calendar.WEEK_OF_YEAR, weekNumber);
		int days = cal.getFirstDayOfWeek() - cal.get(Calendar.DAY_OF_WEEK);
		//cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.add(Calendar.DAY_OF_WEEK, days);
		return cal;
	}
	
	public static Calendar[] getFirstAndLastDayOfWeek(int weekNumber, ArrayList<WorkLog> list){
		Calendar[] ret = new Calendar[2];
		ret[0] = getFirstDayOfWeek(weekNumber);
		ret[1] = getFirstDayOfWeek(weekNumber + 1);
		ret[1].add(Calendar.SECOND, -1);
		return ret;
	}
	
	public static ArrayList<WorkLog> getLogsOfWeek(int weekNumber, ArrayList<WorkLog> list){
		Calendar[] days = getFirstAndLastDayOfWeek(weekNumber, list);
		return WorkLog.getLogsBetween(days[0], days[1], list);
	}
	
	public static int getDurationOfWeek(int weekNumber, ArrayList<WorkLog> list){
		int duration = 0;
		ArrayList<WorkLog> parialList = WorkLog.getLogsOfWeek(weekNumber, list);
		for(WorkLog log: parialList){
			duration += log.getDurationInMin();
		}
		return (int)Math.round(duration / 60.0);
	}

	public static ArrayList<WorkLog> getLogsOfDay(Calendar day, ArrayList<WorkLog> list) {
		Calendar from = (Calendar) day.clone();
		from.set(Calendar.HOUR_OF_DAY, 0);
		from.set(Calendar.MINUTE, 0);
		from.set(Calendar.SECOND, 0);
		Calendar to = (Calendar) day.clone();
		to.set(Calendar.HOUR_OF_DAY, 23);
		to.set(Calendar.MINUTE, 59);
		to.set(Calendar.SECOND, 59);
		return getLogsBetween(from, to, list);
	}
	
	public static int getDurationOfDay(Calendar day, ArrayList<WorkLog> list) {
		int duration = 0;
		ArrayList<WorkLog> parialList = WorkLog.getLogsOfDay(day, list);
		for(WorkLog log: parialList){
			duration += log.getDurationInMin();
		}
		return (int)Math.round(duration / 60.0);
	}
	
	public int compareTo(WorkLog arg0) {
		return (arg0.getStartTime().compareTo(this.getStartTime()));
	}
	
}


