package be.qrsdp.worktimer;

import java.util.ArrayList;

import android.app.Application;

public class MainApplication extends Application {

	private ArrayList<WorkLog> workLogs;
	private boolean atWork;
	
	@Override
	public void onCreate() {

		workLogs = new ArrayList<WorkLog>();
		atWork = false;
		
		super.onCreate();
	}
	
	void toggle(){
		atWork = !atWork;
	}
	
	boolean isAtWork(){
		return atWork;
	}
	
    
}
