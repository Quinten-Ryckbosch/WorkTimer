package be.qrsdp.worktimer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import be.qrsdp.utils.Util;


public class WorkWeek extends WorkDay {
	int weeknumber, year;
	Calendar firstDay;
	Map<Integer, WorkDay> thisWeeksLogs;
	int duration;

	public WorkWeek(int weekNumber, int year) {
		this.weeknumber = weekNumber;
		this.year = year;
		duration = 0;
		thisWeeksLogs = new HashMap<Integer, WorkDay>();
		Calendar day = Util.getFirstDayOfWeek(weekNumber, year);
		this.firstDay = (Calendar) day.clone();
		for(int i = 0; i < 7; i++){
			thisWeeksLogs.put(Util.getIndex(day),new WorkDay((Calendar) day.clone()));
			day.add(Calendar.DAY_OF_YEAR, 1);
		}
	}
	
	public boolean addWorkLog(WorkLog log){
		thisWeeksLogs.get(Util.getIndex(log.getStartTime())).addWorkLog(log);
		duration += log.getDuration();
		return true;
	}

	public void endWorkLog(WorkLog log) {
		thisWeeksLogs.get(Util.getIndex(log.getStartTime())).endWorkBlock();
		
		//update Duration
		duration = 0;
		for(WorkDay it: thisWeeksLogs.values()){
			duration += it.getDuration();
		}
		
	}
	
	public int getDuration(){
		return duration;
	}
	
	public boolean isCurrent(){
		for(WorkDay it: thisWeeksLogs.values()){
			if(it.isCurrent()) return true;
		}
		return false;
	}
	
	public String getString(){
		Calendar lastDay = ((Calendar) firstDay.clone());
		lastDay.add(Calendar.DAY_OF_YEAR, 6);
		
		return firstDay.get(Calendar.DAY_OF_MONTH)
				+ " " + Util.MONTH_NAMES_SHORT[firstDay.get(Calendar.MONTH)]
				+ " - " + lastDay.get(Calendar.DAY_OF_MONTH)
				+ " " + Util.MONTH_NAMES_SHORT[lastDay.get(Calendar.MONTH)];
	}
	
	public ArrayList<WorkLog> getLogs(){
		ArrayList<WorkLog> list = new ArrayList<WorkLog>();
		for(WorkDay day: thisWeeksLogs.values()){
			list.addAll(day.getLogs());
		}
		return list;
	}
	
	public WorkDay getDay(Calendar day){
		return thisWeeksLogs.get(Util.getIndex(day));
	}
	
	
	public ArrayList<WorkDay> getDays(){
		ArrayList<WorkDay> list = new ArrayList<WorkDay>(thisWeeksLogs.values());
		Collections.sort(list);
		return list;
	}

}
