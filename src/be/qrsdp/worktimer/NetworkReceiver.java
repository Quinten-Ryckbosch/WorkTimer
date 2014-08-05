package be.qrsdp.worktimer;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import be.qrsdp.utils.Util;

public class NetworkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("app","Network connectivity change");
		
		MainApplication app = (MainApplication)context.getApplicationContext();
		NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		if(networkInfo != null)Log.d("app","New network state " + networkInfo.getType());
		else Log.d("app","networkInfo is null ");
		
		if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			if(Util.getSSID(context).equalsIgnoreCase(app.getWorkNetworkSSID())){
				Log.d("SSID","Connected to: " + Util.getSSID(context));
				//Toast.makeText(context, "Connected to: " + Util.getSSID(context), Toast.LENGTH_SHORT).show();
				app.toggleViaNetwork(true);
			}
		} else {
			Log.d("SSID","Not connected to any network.");
			//Toast.makeText(context, "Not connected to a wifi network.", Toast.LENGTH_SHORT).show();
			
			//Check if network is avaible?
			WifiReceiver receiverWifi = new WifiReceiver();
			context.getApplicationContext().registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		    wifi.startScan();
		}
		
		
	}
	
	
	class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
        	WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        	List<ScanResult> results = wifi.getScanResults();
        	boolean networkPresent = false;
    		for (ScanResult result : results) {
    			Log.d("Avaible SSID",result.SSID + " " + result.level);
    			if(result.SSID.equalsIgnoreCase(context.getResources().getString(R.string.networkName))){
    				networkPresent = true;
    			}
                //Toast.makeText(context, result.SSID + " " + result.level, Toast.LENGTH_SHORT).show();
    		}
    		if(!networkPresent){ //only change the working state when the network is not present.
    			((MainApplication)context.getApplicationContext()).toggleViaNetwork(false);
    		}
    		context.getApplicationContext().unregisterReceiver(WifiReceiver.this);
		    
        }
    }
}
