package be.qrsdp.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import be.qrsdp.worktimer.MainApplication;
import be.qrsdp.worktimer.data.WorkDay;
import be.qrsdp.worktimer.data.WorkLog;
import be.qrsdp.worktimer.data.WorkWeek;

public class Util {
	
	public static int getIndex(Calendar cal){
		return cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.DAY_OF_YEAR);
	}
	
	public static int getWeekIndex(Calendar cal){
		return getWeekIndex(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.YEAR));
	}
	
	public static int getWeekIndex(int weekNumber, int year){
		return year * 100 + weekNumber;
	}
	
	public static String getTwoDigitNumber(int i){
		if(i < 10) return "0" + i;
		return "" + i;
	}
	
	public static String getDurationString(int duration) {
		String time = "";
		if (duration >= 60) {
			time += (int)Math.floor(duration / 60.0) + "u";
			time += Util.getTwoDigitNumber(duration % 60) + "m";
		} else {
			time += duration % 60 + "m";
		}
		return time;
	}
	
	
	/**
	 * Clean log for this week.
	 * Only clean for one week, otherwise this would take to much time even without changing anything.
	 * 
	 * @param app
	 * @param week
	 * @return
	 */
	public static boolean cleanLogs(MainApplication app, WorkWeek week) {
		boolean changed = false;
		//Split logs that overlap days.
		for(WorkDay day: week.getDays()){
			for(WorkLog log: day.getLogs()){
				if(log.getStopTime() != null){ // Current log is a special case
					if(log.getStartTime().get(Calendar.DAY_OF_YEAR) != log.getStopTime().get(Calendar.DAY_OF_YEAR)){
						Log.d("cleanLogs", "Changing log " + log.getTotalString());

						//split log
						changed = true;

						Calendar newStartTime = (Calendar) log.getStopTime().clone();
						newStartTime.set(Calendar.HOUR_OF_DAY, 0);
						newStartTime.set(Calendar.MINUTE, 0);
						newStartTime.set(Calendar.SECOND, 0);
						newStartTime.set(Calendar.MILLISECOND, 0);
						WorkLog newLog = new WorkLog(newStartTime, log.getStopTime());
						app.addLog(newLog);
						Calendar newStopTime = (Calendar) newStartTime.clone();
						newStopTime.add(Calendar.MILLISECOND, -1);
						log.endWorkBlock(newStopTime);
						app.updateLog(log);

					}
				}
			}
		}
		
		//Before removing small logs, try to merge them to get a bigger log.
		
		if(!changed){
			//merge logs when stop and restart time is less then 10 minutes
			for(WorkDay day: week.getDays()){ // look per day (otherwise previous "daysplit" will be merged)
				ArrayList<WorkLog> logs = day.getLogs();
				Collections.reverse(logs);
				for(int index = 1; index < logs.size(); index ++){
					Calendar lastStopTime = logs.get(index - 1).getStopTime();
					Calendar nextStartTime = logs.get(index).getStartTime();
					if(lastStopTime != null){	//normally only the last log has a stoptime == null, but hey.
						long diff = nextStartTime.getTimeInMillis() - lastStopTime.getTimeInMillis();
						if(diff < 10 * 60 * 1000){
							//Log.d("UTIL", "first = " + logs.get(index-1).getTotalString());
							//Log.d("UTIL", "second= " + logs.get(index).getTotalString());
							changed = true;
							WorkLog mergedLog = new WorkLog(logs.get(index-1).getStartTime(), logs.get(index).getStopTime());
							app.updateLog(mergedLog); //actually new logs.get(index-1) log (same startTime)
							app.deleteLog(logs.get(index));
							break;	//logs list is out dated.
						}
					}
				}
			}
		}
		
		if(!changed){
			//remove logs when duration is less than 10 minutes
			for(WorkDay day: week.getDays()){
				for(WorkLog log: day.getLogs()){
					if(log.getStopTime() != null){
						long diff = log.getStopTime().getTimeInMillis() - log.getStartTime().getTimeInMillis();
						if(diff < 10 * 60 * 1000){
							changed = true;
							app.deleteLog(log);
							break;	//logs list is out dated.
						}
					}
				}
			}
		}
		return changed;
	}
	
	//Doesn't belong here
	public static Calendar getFirstDayOfWeek(int weekNumber, int year){
		//Get the first day of the week
		Calendar cal = Calendar.getInstance();
		cal.set(year, 1, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.WEEK_OF_YEAR, weekNumber);
		
		//make sure you have the first day of the week.
		int days = cal.getFirstDayOfWeek() - cal.get(Calendar.DAY_OF_WEEK);
		cal.add(Calendar.DAY_OF_WEEK, days);
		
		return cal;
	}
	
	//Doesn't belong here
	public static Calendar[] getFirstAndLastDayOfWeek(int weekNumber, int year){
		Calendar[] ret = new Calendar[2];
		ret[0] = getFirstDayOfWeek(weekNumber, year);
		ret[1] = getFirstDayOfWeek(weekNumber + 1, year);
		ret[1].add(Calendar.SECOND, -1);
		return ret;
	}
	
	//Doesn't belong here
	/*public static String getWeekString(Calendar[] days){
		return days[0].get(Calendar.DAY_OF_MONTH)
				+ " " + MONTH_NAMES_SHORT[days[0].get(Calendar.MONTH)]
				+ " - " + days[1].get(Calendar.DAY_OF_MONTH)
				+ " " + MONTH_NAMES_SHORT[days[1].get(Calendar.MONTH)];
	}*/
	
	public static String getSSID(Context context){
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String ssid = wifiInfo.getSSID();
		ssid = ssid.substring(1, ssid.length()-1);
		return ssid;
	}
}
