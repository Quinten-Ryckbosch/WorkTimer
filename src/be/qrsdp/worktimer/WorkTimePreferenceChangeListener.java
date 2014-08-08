package be.qrsdp.worktimer;

import be.qrsdp.worktimer.gui.Notification;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

public class WorkTimePreferenceChangeListener implements OnSharedPreferenceChangeListener {
	MainApplication app;
	
	public WorkTimePreferenceChangeListener(MainApplication mainApplication) {
		this.app = mainApplication;
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d("Settings",key + " setting is changed");
		if (key.equals(MainApplication.KEY_PREF_SHOW_NOTIFICATOIN)) {
			app.setAlwaysShowNotification(sharedPreferences.getBoolean(key, true));

			app.setNotification(new Notification(app, app.isAlwaysShowNotification()));
			app.getNotification().updateNotification(app.isAtWork());
		}
		if (key.equals(MainApplication.KEY_PREF_WORK_SSID)) {
			app.setWorkNetworkSSID(sharedPreferences.getString(key, ""));
		}
	}

}
