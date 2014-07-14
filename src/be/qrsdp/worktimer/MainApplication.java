package be.qrsdp.worktimer;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MainApplication extends Application {
	
	private ArrayList<WorkLog> workLogs;
	private WorkLog currentLog;
	private boolean atWork;
	
	private WorkDBHelper dataBaseHelper;
	
	@Override
	public void onCreate() {

		System.out.println("MainApplication is Created.");
		
		dataBaseHelper = new WorkDBHelper(getApplicationContext());
		workLogs = dataBaseHelper.getAllRecords();
		Collections.sort(workLogs);
		
		
		atWork = isAtWork(workLogs);
		
		super.onCreate();
	}
	
	boolean isAtWork(ArrayList<WorkLog> workLogs){
		for(WorkLog log: workLogs){
			if(log.isCurrent()){
				this.currentLog = log;
				return true;
			}
		}
		return false;
	}
	
	void toggle(){
		if(!atWork){
			//Start new workBlock
			currentLog = new WorkLog();
			workLogs.add(currentLog);
			Collections.sort(workLogs);
			dataBaseHelper.insertRecord(currentLog);
		} else {
			//End last workBlock
			currentLog.endWorkBlock();
			dataBaseHelper.updateRecord(currentLog);
			
		}
		atWork = !atWork;
	}
	
	boolean isAtWork(){
		return atWork;
	}

	public String getLastLogs() {
		String lastLogs = "";
		for(int i=0; i < Math.min(5,workLogs.size()); i++){
			lastLogs += workLogs.get(i).getTimeString() + "\n";
		}
		return lastLogs;
	}
	
    
}

class WorkDBHelper extends SQLiteOpenHelper {
	private final static String DBHELPER_TAG = "WorkDBHelper";

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "WorkLog.db";
    private static final String TABLE_CREATE = "CREATE TABLE Log ( StartTime TEXT, StopTime TEXT);";

    WorkDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(TABLE_CREATE);
    	Log.d(DBHELPER_TAG, "DataBase " + DATABASE_NAME + " is created.");
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public long insertRecord(WorkLog log){
		SQLiteDatabase dataBase = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("StartTime", WorkLog.parseWorkLog(log.getStartTime()));
		values.put("StopTime", WorkLog.parseWorkLog(log.getStopTime()));
		long rowId = dataBase.insert("log", null, values);
		
		Log.d(DBHELPER_TAG, "Record inserted:");
		Log.d(DBHELPER_TAG, "\tStartTime: " + WorkLog.parseWorkLog(log.getStartTime()));
		Log.d(DBHELPER_TAG, "\tStopTime:  " + WorkLog.parseWorkLog(log.getStopTime()));
		
		dataBase.close();
		return rowId;
	}
	
	public void updateRecord(WorkLog log){
		SQLiteDatabase dataBase = this.getWritableDatabase();
		String updateQuery = "UPDATE log SET StopTime='" + WorkLog.parseWorkLog(log.getStopTime())
				+ "' WHERE StartTime='" + WorkLog.parseWorkLog(log.getStartTime()) + "'";
		Log.d(DBHELPER_TAG, "Record updated: " + updateQuery);
		//TODO use dataBase.update instead of raw sql command.
		dataBase.execSQL(updateQuery);
	}
	
	public ArrayList<WorkLog> getAllRecords(){
		ArrayList<WorkLog> workLogs = new ArrayList<WorkLog>();
		String selectQuery = "SELECT  * FROM log";
	    SQLiteDatabase database = this.getWritableDatabase();
	    Cursor cursor = database.rawQuery(selectQuery, null);
	    if (cursor.moveToFirst()) {
	        do {
	        	WorkLog current = new WorkLog(cursor.getString(0), cursor.getString(1));
	        	
	        	Log.d(DBHELPER_TAG, "Record loaded:");
	        	Log.d(DBHELPER_TAG, "\t" + current.getTimeString());
                
	        	workLogs.add(current);
	        } while (cursor.moveToNext());
	    }
	    
	    Log.d(DBHELPER_TAG, "Number of records returned: " + workLogs.size());

	    return workLogs;

	}
}
