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

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("app","Network connectivity change");
		
		MainApplication app = (MainApplication)context.getApplicationContext();
		NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

		if(networkInfo != null)Log.d("app","New network state " + networkInfo.getType());
		else Log.d("app","networkInfo is null ");
		if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			if(Util.getSSID(context).equalsIgnoreCase(context.getResources().getString(R.string.networkName))){
				Log.d("SSID","Connected to: " + Util.getSSID(context));
				//Toast.makeText(context, "Connected to: " + Util.getSSID(context), Toast.LENGTH_SHORT).show();
				app.toggleViaNetwork(true);
			}
		} else {
			Log.d("SSID","Not connected to any network.");
			//Toast.makeText(context, "Not connected to a wifi network.", Toast.LENGTH_SHORT).show();
			app.toggleViaNetwork(false);
		}

	}

}
