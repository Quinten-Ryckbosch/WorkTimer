package be.qrsdp.worktimer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class HomeScreen extends Activity {
    
	private MainApplication app;

	Button atWorkBtn;
	Button emailBtn;
	
	TextView logsTextView;
	
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
        
    	//Load database in extra thread.
        new LoadDataBaseTask().execute();
        
        /*app.loadWorkLogs();
        
        //Button
        //atWorkBtn.setText(app.isAtWork() ? R.string.button_at_work : R.string.button_not_at_work);
        //atWorkBtn.setPressed(app.isAtWork());
        atWorkBtn.setBackgroundResource(app.isAtWork() ? R.drawable.working : R.drawable.notworking);
        atWorkBtn.setOnClickListener(atWorkBtnListener);
        
        //Logs
        logsTextView.setText(app.getLastLogs());*/
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	private OnClickListener atWorkBtnListener = new OnClickListener() {
	    public void onClick(View v) {
	      app.toggle();
	      //Change look of the button
	      atWorkBtn.setBackgroundResource(app.isAtWork() ? R.drawable.working : R.drawable.notworking);
	      
	      //Change the log
	      logsTextView.setText(app.getLastLogs());
	      
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
