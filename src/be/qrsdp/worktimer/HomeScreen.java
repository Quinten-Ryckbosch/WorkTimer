package be.qrsdp.worktimer;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class HomeScreen extends Activity {
    
	private MainApplication app;

	Button atWorkBtn;
	Button emailBtn;
	
	TextView logsTextView;
	
	NotificationCompat.Builder mBuilder;
	int notifyID = 1;
	
	
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
        
    	//Create the notification
    	loadNotification();
        
        //Load database in extra thread.
        new LoadDataBaseTask().execute();
        
        
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
		i.putExtra(Intent.EXTRA_TEXT   , app.getLastLogs());
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

	private OnClickListener atWorkBtnListener = new OnClickListener() {
	    public void onClick(View v) {
	      app.toggle();
	      //Change look of the button
	      atWorkBtn.setBackgroundResource(app.isAtWork() ? R.drawable.working : R.drawable.notworking);
	      
	      //Change the log
	      logsTextView.setText(app.getLastLogs());
	      
	      //Change notification
	      updateNotification();
	      
	      System.out.println("Atwork = " + app.isAtWork());
	    }
	};

	
	private void getGuiElementsFromLayout() {
		atWorkBtn = (Button) findViewById(R.id.btn_work);
		logsTextView = (TextView) findViewById(R.id.tv_log);
	}
	
	
	private class LoadDataBaseTask extends AsyncTask<Void, Void, String> {
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */
	    protected String doInBackground(Void... args) {
	    	app.loadAllWorkLogs();
	    	return app.getLastLogs();
	    }
	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(String result) {
	    	atWorkBtn.setBackgroundResource(app.isAtWork() ? R.drawable.working : R.drawable.notworking);
	        atWorkBtn.setOnClickListener(atWorkBtnListener);
	        
	        //Logs
	        logsTextView.setText(result);
	    }
	    
	    protected void onPreExecute(){
	    	logsTextView.setText("Loading logs..");
	    }
	}

}
