package be.qrsdp.worktimer;

import java.util.Calendar;

import android.sax.EndTextElementListener;

public class WorkLog {
	

	// Holds time (and other things?) for 1 work block
	Calendar startTime, stopTime = null;
	
	WorkLog(){
		startWorkBlock();
	};
	
	void startWorkBlock(){
		startTime = Calendar.getInstance();
		System.out.println(toString());
	}
	
	void endWorkBlock(){
		stopTime = Calendar.getInstance();
		System.out.println(toString());
	}
	
	private String getTime(Calendar time){
		String ret = "";
		ret += time.get(Calendar.DAY_OF_MONTH) + "/" + time.get(Calendar.MONTH) + "/" + time.get(Calendar.YEAR);
		ret += " " + time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE);
		return ret;
	}
	
	public String toString(){
		String time = "";
		time += getTime(startTime);
		
		if(stopTime == null){
			time += " - current";
		} else {
			time += " - " + getTime(stopTime);
		}
		
		return time;
	}
	
}
