package be.qrsdp.worktimer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class HomeScreen extends Activity {
    
	private MainApplication app;

	Button atWorkBtn;
	Button emailBtn;
	
	TextView logsTextView;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        app = (MainApplication) getApplication();
        
        getGuiElementsFromLayout();
        
        atWorkBtn.setText(app.isAtWork() ? R.string.button_at_work : R.string.button_not_at_work);
        atWorkBtn.setPressed(app.isAtWork());
        atWorkBtn.setOnClickListener(atWorkBtnListener);
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
	      //Change look of the botton
	      atWorkBtn.setText(app.isAtWork() ? R.string.button_at_work : R.string.button_not_at_work);
	      atWorkBtn.setPressed(app.isAtWork()); //FIXME The color doesn't stick. It only changes when you rotate the phone.
	      
	      System.err.println("Atwork = " + app.isAtWork());
	    }
	};

	
	private void getGuiElementsFromLayout() {
		atWorkBtn = (Button) findViewById(R.id.btn_work);
		emailBtn = (Button) findViewById(R.id.btn_email);
		
		logsTextView = (TextView) findViewById(R.id.tv_log);
	}

}
