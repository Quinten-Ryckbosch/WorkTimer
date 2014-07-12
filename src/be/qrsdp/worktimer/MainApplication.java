package be.qrsdp.worktimer;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

class WorkDB extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "WorkLog";
    private static final String TABLE_CREATE =
                "CREATE TABLE Log ( StartTime TEXT, StopTime TEXT);";

    WorkDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
