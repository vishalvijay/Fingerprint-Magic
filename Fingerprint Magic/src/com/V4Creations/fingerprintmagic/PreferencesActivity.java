package com.V4Creations.fingerprintmagic;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.V4Creations.fingerprintmagic.system.Settings;

/**
 * @author Vishal Vijay
 */

public class PreferencesActivity extends PreferenceActivity {
	private int MODE;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MODE = getIntent().getIntExtra(Settings.MODE, Settings.CLIENT_MODE);
		addPreferencesFromResource(R.xml.preferences_xml);
		if (MODE == Settings.CLIENT_MODE) {
			CheckBoxPreference onExitDeleteBoxPreference = (CheckBoxPreference) findPreference(Settings.PREFS_IS_ON_EXIT_DELETE_CURRENT_NAME);
			PreferenceCategory preformancePreferenceCategory = (PreferenceCategory) findPreference("performance");
			preformancePreferenceCategory
					.removePreference(onExitDeleteBoxPreference);
			preformancePreferenceCategory = (PreferenceCategory) findPreference("appsettings");
			PreferenceScreen preferenceScreen = getPreferenceScreen();
			preferenceScreen.removePreference(preformancePreferenceCategory);
		} else {
			PreferenceCategory preformancePreferenceCategory = (PreferenceCategory) findPreference("important");
			PreferenceScreen preferenceScreen = getPreferenceScreen();
			preferenceScreen.removePreference(preformancePreferenceCategory);
		}
	}
}
