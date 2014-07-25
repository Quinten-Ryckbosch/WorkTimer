package be.qrsdp.worktimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;


public class HomeScreen extends Activity {
    
	private MainApplication app;

	Button atWorkBtn;
	Button leftBtn, rightBtn;
	
	//TextView logsTextView;
	TextView textWeek;
	
	NotificationCompat.Builder mBuilder;
	int notifyID = 1;
	
	private int showWeekNumber, showYear;
	
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
 
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		System.out.println("HomeScreen Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        app = (MainApplication) getApplication();
        
        getGuiElementsFromLayout();
        
        //Get "atwork" state correct as soon as possible
        app.loadCurrentWorkLog();
    	atWorkBtn.setBackgroundResource(app.isAtWork() ? R.drawable.working : R.drawable.notworking);
    	
    	//Weeknumber
    	showWeekNumber = app.getTodaysWeekNumber();
    	showYear = app.getTodaysYear();
        
    	//Create the notification
    	loadNotification();
        
        //Load database in extra thread.
        new LoadDataBaseTask().execute();
        
        // get the listview
        
 
        // preparing list data
        prepareListData();
        
    }
	
	
	private void getGuiElementsFromLayout() {
		atWorkBtn = (Button) findViewById(R.id.btn_work);
		leftBtn = (Button) findViewById(R.id.buttonLeft);
		rightBtn = (Button) findViewById(R.id.buttonRight);
		//logsTextView = (TextView) findViewById(R.id.tv_log);
		textWeek = (TextView) findViewById(R.id.textWeek);
		expListView = (ExpandableListView) findViewById(R.id.lvExp);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.action_email:
	        sendEmail();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void sendEmail() {
		System.out.println("Send the email.");
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
		i.putExtra(Intent.EXTRA_SUBJECT, "Work log");
		i.putExtra(Intent.EXTRA_TEXT   , app.getLogsList());
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(HomeScreen.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void loadNotification(){
		//Creating a notification
        mBuilder = new NotificationCompat.Builder(this);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, HomeScreen.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeScreen.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        
        updateNotification();
	}
	
	private void updateNotification(){
		mBuilder.setSmallIcon(app.isAtWork() ? R.drawable.working : R.drawable.notworking);
        mBuilder.setContentTitle("WorkLogger");
        mBuilder.setContentText(app.isAtWork() ? "Working" : "Not working");
        mBuilder.setOngoing(true);
        
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	    mNotificationManager.notify(notifyID, mBuilder.build());
	}

	private void refreshData(){
		Log.d("HOMESCREEN", "show data for weeknumber: " + showWeekNumber);
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
		textWeek.setText(app.getWeek(showWeekNumber, showYear));
	}
	
	private OnClickListener atWorkBtnListener = new OnClickListener() {
	    public void onClick(View v) {
	      app.toggle();
	      //Change look of the button
	      atWorkBtn.setBackgroundResource(app.isAtWork() ? R.drawable.working : R.drawable.notworking);
	      
	      //Change the log
	      refreshData();
	      
	      //Change notification
	      updateNotification();
	      
	      System.out.println("Atwork = " + app.isAtWork());
	    }
	};
	
	private OnClickListener leftBtnListener = new OnClickListener() {
		
		public void onClick(View arg0) {
			showWeekNumber --;
			
			//Change the log
		   refreshData();   
		}
	};
	
	private OnClickListener rightBtnListener = new OnClickListener() {
		
		public void onClick(View arg0) {
			showWeekNumber++;
			
			//Change the log
		    refreshData();
		}
	};
	
	private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
 
        // Adding child data
        listDataHeader.add("Top 250");
        listDataHeader.add("Now Showing");
        listDataHeader.add("Coming Soon..");
 
        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");
 
        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");
 
        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");
 
        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }
	
	private class LoadDataBaseTask extends AsyncTask<Void, Void, String> {
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */
	    protected String doInBackground(Void... args) {
	    	app.loadAllWorkLogs();
	    	return app.getLogsOfWeek(showWeekNumber, showYear);
	    }
	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(String result) {
	    	atWorkBtn.setBackgroundResource(app.isAtWork() ? R.drawable.working : R.drawable.notworking);
	        atWorkBtn.setOnClickListener(atWorkBtnListener);
	        leftBtn.setOnClickListener(leftBtnListener);
	    	rightBtn.setOnClickListener(rightBtnListener);
	        
	        //Logs
	        refreshData();
	    }
	    
	    protected void onPreExecute(){
	    	textWeek.setText("Loading logs..");
	    }
	}

}
