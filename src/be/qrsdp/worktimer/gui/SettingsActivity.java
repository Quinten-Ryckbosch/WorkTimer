package be.qrsdp.worktimer.gui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import be.qrsdp.worktimer.R;

public class SettingsActivity extends PreferenceActivity {
    
	@SuppressWarnings("deprecation") // Needed for android version < 11
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
	
}