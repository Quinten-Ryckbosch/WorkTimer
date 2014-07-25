package be.qrsdp.utils;

import java.util.Calendar;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Util {
	public final static String[] MONTH_NAMES_SHORT = {"Jan", "Feb", "Mrt", "Apr", "Mei", "Jun", "Jul", "Aug", "Sept", "Okt", "Nov", "Dec"};

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
	public static String getWeekString(Calendar[] days){
		return days[0].get(Calendar.DAY_OF_MONTH)
				+ " " + MONTH_NAMES_SHORT[days[0].get(Calendar.MONTH)]
				+ " - " + days[1].get(Calendar.DAY_OF_MONTH)
				+ " " + MONTH_NAMES_SHORT[days[1].get(Calendar.MONTH)];
	}
	
	public static String getSSID(Context context){
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		Log.d("SSID",wifiInfo.getSSID());
		return wifiInfo.getSSID();
	}
}
