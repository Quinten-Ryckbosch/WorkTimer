package be.qrsdp.worktimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;
import be.qrsdp.utils.Util;

public class NetworkReceiver extends BroadcastReceiver {
	private boolean connected = false;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("app","Network connectivity change");
		ConnectivityManager conn =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conn.getActiveNetworkInfo();
		
		if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			if(!connected){
				Toast.makeText(context, "Connected to: " + Util.getSSID(context), Toast.LENGTH_SHORT).show();
				connected = true;
			}
	    } else {
	    	if(connected){
	    		Toast.makeText(context, "Not connected to a wifi network.", Toast.LENGTH_SHORT).show();
	    		connected = false;
	    	}
	    }
		
	}

}
