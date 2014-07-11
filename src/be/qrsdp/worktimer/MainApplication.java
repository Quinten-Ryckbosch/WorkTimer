package be.qrsdp.worktimer;

import java.util.ArrayList;

import android.app.Application;

public class MainApplication extends Application {

	private ArrayList<WorkLog> workLogs;
	
	@Override
	public void onCreate() {

		workLogs = new ArrayList<WorkLog>();
		
		super.onCreate();
	}
	
	
	
    
}
