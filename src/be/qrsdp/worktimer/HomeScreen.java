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
	      //atWorkBtn.setText(app.isAtWork() ? R.string.button_at_work : R.string.button_not_at_work);
	      //FIXME The color doesn't stick. Probably changed back by normal button behavior.
	      //atWorkBtn.setPressed(app.isAtWork());
	      //[SANDER] Solution maybe is to just change the background. But I can't find the default pressed and not-pressed colors.
	      atWorkBtn.setBackgroundResource(app.isAtWork() ? R.drawable.working : R.drawable.notworking);
	      //atWorkBtn.setBackgroundColor(app.isAtWork() ? Color.BLUE : Color.LTGRAY);
	      
	      //Change the log
	      logsTextView.setText(app.getLastLogs());
	      
	      System.out.println("Atwork = " + app.isAtWork());
	    }
	};

	
	private void getGuiElementsFromLayout() {
		atWorkBtn = (Button) findViewById(R.id.btn_work);
		emailBtn = (Button) findViewById(R.id.btn_email);
		
		logsTextView = (TextView) findViewById(R.id.tv_log);
	}
	
	private class LoadDataBaseTask extends AsyncTask<Void, Void, String> {
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */
	    protected String doInBackground(Void... args) {
	        app.loadWorkLogs();
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
	    
	    
	    //onProgressUpdate
	    //Check "Working" vs "Not Working" state early
	}

}
