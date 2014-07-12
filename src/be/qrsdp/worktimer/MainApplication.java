package be.qrsdp.worktimer;

import java.util.ArrayList;

import android.app.Application;

public class MainApplication extends Application {

	private ArrayList<WorkLog> workLogs;
	private WorkLog currentLog;
	private boolean atWork;
	
	@Override
	public void onCreate() {

		workLogs = new ArrayList<WorkLog>();
		atWork = false;
		
		super.onCreate();
	}
	
	void toggle(){
		if(!atWork){
			//Start new workBlock
			currentLog = new WorkLog();
			workLogs.add(currentLog);
		} else {
			//End last workBlock
			currentLog.endWorkBlock();
		}
		atWork = !atWork;
	}
	
	boolean isAtWork(){
		return atWork;
	}

	public String getLastLogs() {
		String lastLogs = "";
		for(int i=workLogs.size()-1; i>=Math.max(0,workLogs.size()-5); i--){
			lastLogs += workLogs.get(i) + "\n";
		}
		return lastLogs;
	}
	
    
}


