package be.qrsdp.worktimer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        app = (MainApplication) getApplication();
        
        getGuiElementsFromLayout();
        
        //Button
        //atWorkBtn.setText(app.isAtWork() ? R.string.button_at_work : R.string.button_not_at_work);
        //atWorkBtn.setPressed(app.isAtWork());
        atWorkBtn.setBackgroundResource(app.isAtWork() ? R.drawable.working : R.drawable.notworking);
        atWorkBtn.setOnClickListener(atWorkBtnListener);
        
        //Logs
        logsTextView.setText(app.getLastLogs());
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

}
