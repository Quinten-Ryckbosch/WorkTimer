package be.qrsdp.worktimer.gui;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import be.qrsdp.worktimer.ExpandableListAdapter;
import be.qrsdp.worktimer.MainApplication;
import be.qrsdp.worktimer.R;


public class HomeScreen extends Activity {
	private final static String LOG_TAG = "HomeScreen";

	private MainApplication app;

	Button atWorkBtn;
	Button leftBtn, rightBtn;

	//TextView logsTextView;
	TextView textWeek;

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "Is Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		app = (MainApplication) getApplication();
		//app.spoofDataBase();

		getGuiElementsFromLayout();

		//Get "atwork" state correct as soon as possible
		app.loadCurrentWorkLog();
		atWorkBtn.setBackgroundResource(app.isAtWork() ? R.drawable.working : R.drawable.notworking);

		//Load database in extra thread.
		new LoadDataBaseTask().execute();

	}


	private void getGuiElementsFromLayout() {
		atWorkBtn = (Button) findViewById(R.id.btn_work);
		leftBtn = (Button) findViewById(R.id.buttonLeft);
		rightBtn = (Button) findViewById(R.id.buttonRight);
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
			
			case R.id.action_exit:
				quitApp();
				return true;
				
			case R.id.action_settings:
				loadSettingsScreen();
				return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void loadSettingsScreen() {
	    Intent intent = new Intent(this, SettingsActivity.class);
	    startActivity(intent);
	}


	private void quitApp() {
		// TODO remove notification if not at work? (keep it if it is activated?)
		finish();
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

	private void refreshData(HashMap<String, List<String>> result){
		Log.d("HOMESCREEN", "show data for weeknumber: " + app.showWeekNumber);
		if(result == null){
			result = app.getLogsOfWeek(app.showWeekNumber, app.showYear);
		}
		if(result.size() == 0){
			Toast.makeText(this, "No logs to show for this week", Toast.LENGTH_LONG).show();
		}// else {
		listAdapter = new ExpandableListAdapter(this, result);
		//}
		expListView.setAdapter(listAdapter);
		textWeek.setText(app.getWeek(app.showWeekNumber, app.showYear));
	}

	public void toggle(){
		app.toggle();
		//Change look of the button
		atWorkBtn.setBackgroundResource(app.isAtWork() ? R.drawable.working : R.drawable.notworking);

		//Change the log
		refreshData(null);

		System.out.println("Atwork = " + app.isAtWork());
	}

	private OnClickListener atWorkBtnListener = new OnClickListener() {
		public void onClick(View v) {
			toggle();
		}
	};

	private OnClickListener leftBtnListener = new OnClickListener() {

		public void onClick(View arg0) {
			app.changeWeek(-1);
			//app.showWeekNumber --;

			//Change the log
			refreshData(null);   
		}
	};

	private OnClickListener rightBtnListener = new OnClickListener() {

		public void onClick(View arg0) {
			app.changeWeek(1);
			//app.showWeekNumber++;

			//Change the log
			refreshData(null);
		}
	};

	private class LoadDataBaseTask extends AsyncTask<Void, Void, HashMap<String, List<String>>> {
		/** The system calls this to perform work in a worker thread and
		 * delivers it the parameters given to AsyncTask.execute() */
		protected HashMap<String, List<String>> doInBackground(Void... args) {
			app.loadAllWorkLogs(false);
			return app.getLogsOfWeek(app.showWeekNumber, app.showYear);
		}

		/** The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground() */
		protected void onPostExecute(HashMap<String, List<String>> result) {
			atWorkBtn.setBackgroundResource(app.isAtWork() ? R.drawable.working : R.drawable.notworking);
			atWorkBtn.setOnClickListener(atWorkBtnListener);
			leftBtn.setOnClickListener(leftBtnListener);
			rightBtn.setOnClickListener(rightBtnListener);

			//Logs
			refreshData(result);
		}

		protected void onPreExecute(){
			textWeek.setText("Loading logs..");
		}
	}

}
