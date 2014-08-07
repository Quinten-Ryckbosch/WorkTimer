package be.qrsdp.worktimer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class WorkDay extends WorkLog{
	private Calendar day;
	private ArrayList<WorkLog> todaysLogs;
	private int duration;
	
	public WorkDay(){
		todaysLogs = new ArrayList<WorkLog>();
		duration = 0;
	}
	
	public WorkDay(Calendar day) {
		this.day = day;
		todaysLogs = new ArrayList<WorkLog>();
		duration = 0;
	}
	
	public boolean addWorkLog(WorkLog log){
		if(log.getStartTime().get(Calendar.DAY_OF_YEAR) != day.get(Calendar.DAY_OF_YEAR)) return false;
		if(log.getStartTime().get(Calendar.YEAR) != day.get(Calendar.YEAR)) return false;
		todaysLogs.add(log);
		duration += log.getDuration();
		return true;
	}

	public void endWorkLog(WorkLog log) {
		log.endWorkBlock();
		
		WorkLog check = getWorkLog(log);
		if(check == null){
			System.err.println("This is not working");
		} else {
			System.err.println(check.getStopTime().toString());
		}
		
		//update Duration
		duration = 0;
		for(WorkLog it: todaysLogs){
			duration += it.getDuration();
		}
		
	}
	
	public int getDuration(){
		return duration;
	}
	
	public boolean isCurrent(){
		for(WorkLog it: todaysLogs){
			if(it.isCurrent()) return true;
		}
		return false;
	}
	
	public String getString(){
		return day.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
				+ " " + day.get(Calendar.DAY_OF_MONTH)
				+ " " + day.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
	}
	
	public ArrayList<WorkLog> getLogs(){
		Collections.sort(todaysLogs);
		return todaysLogs;
	}
	
	public Calendar getDay(){
		return day;
	}
	
	private WorkLog getWorkLog(WorkLog log){
		for(WorkLog it: todaysLogs){
			if(it.equals(log)){
				return it;
			}
		}
		return null;
	}
	
	public int compareTo(WorkDay arg0) {
		return (arg0.getDay().compareTo(this.getDay()));
	}
}
