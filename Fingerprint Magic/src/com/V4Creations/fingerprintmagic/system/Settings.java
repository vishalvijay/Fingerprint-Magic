package com.V4Creations.fingerprintmagic.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Vishal Vijay
 */

public class Settings {
	private static final String PREFS_IMEI = "imei";
	private static final String PREFS_USERNAME = "username";
	private static final String PREFS_PHONE_NUMBER = "phonenumber";
	private static final String PREFS_APP_BUILD_NUMBER = "appbuildnumber";
	private static final String PREFS_USER_ID = "userid";
	private static final String PREFS_CURRENT_NAME = "currentname";
	private static final String PREFS_INTERNETCHECKING_DELAY = "internetCheckingDelay";
	private static final String PREFS_IS_SOUND_ENABLED = "isSoundEnabled";
	private static final String PREFS_IS_VIBRATION_ENABLED = "isVibrationEnabled";
	public static final String PREFS_IS_ON_EXIT_DELETE_CURRENT_NAME = "isOnExitDeleteCurrentName";
	private static final String PREFS_IS_FINGERPRINT_LOAD_SCREEN_FIRST_TIME = "isFingerprintLoadScreenFirstTime";
	private static final String PREFS_IS_FINGERPRINT_SCANNER_SCREEN_FIRST_TIME = "isFingerprintScannerScreenFirstTime";
	private static final String PREFS_IS_NAME_SELECTOR_FIRST_TIME = "isNameSelectorFirstTime";
	private static final String PREFS_IS_SHOW_HIDEN_BUTTON = "isShowHidenButton";
	private static final String PREFS_SHOW_HELP_ONLY_IN_SERVER_MODE = "showHelpOnlyInServerMode";

	public static final int DIALOG_ABOUT = 0;
	public static final String MODE = "mode";
	public static final String SUCCESS = "success";
	public static String URL = "http://v4.vtulife.com/";
	public static final int SINGLE_MODE = 1;
	public static final int MULTI_MODE = 2;
	public static final int SERVER_MODE = 3;
	public static final int CLIENT_MODE = 4;
	public static String[] CONSTANT_NAMES_MULTY_MODE = {
			"Delete selected name", "Wait", "Server down", "Name not found" };
	public static String[] CONSTANT_NAMES_SINGL_MODE = {
			"Delete selected name", "Server down", "Name not found" };

	public static void setImei(Context context, String imei) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(Settings.PREFS_IMEI, imei);
		edit.commit();
	}

	public static String getImei(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Random random = new Random();
		long randNum = random.nextLong();
		while (randNum < 100000)
			randNum = random.nextLong();

		randNum %= Math.pow(10, 15);

		return prefs.getString(PREFS_IMEI, Long.toString(randNum));
	}

	public static void setUserId(Context context, String userId) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(Settings.PREFS_USER_ID, userId);
		edit.commit();
	}

	public static String getUserId(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getString(PREFS_USER_ID, null);
	}

	public static void setCurrentName(Context context, String currentName) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(Settings.PREFS_CURRENT_NAME, currentName);
		edit.commit();
	}

	public static String getCurrentName(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getString(PREFS_CURRENT_NAME, null);
	}

	public static void setFingerprintLoadScreenFirstTime(Context context,
			boolean status) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(Settings.PREFS_IS_FINGERPRINT_LOAD_SCREEN_FIRST_TIME,
				status);
		edit.commit();
	}

	public static boolean isFingerprintLoadScreenFirstTime(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_FINGERPRINT_LOAD_SCREEN_FIRST_TIME,
				true);
	}

	public static void setFingerprintScannerScreenFirstTime(Context context,
			boolean status) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(
				Settings.PREFS_IS_FINGERPRINT_SCANNER_SCREEN_FIRST_TIME, status);
		edit.commit();
	}

	public static boolean isFingerprintScannigScreenFirstTime(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_FINGERPRINT_SCANNER_SCREEN_FIRST_TIME,
				true);
	}

	public static void setNameSelectorFirstTime(Context context, boolean status) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(Settings.PREFS_IS_NAME_SELECTOR_FIRST_TIME, status);
		edit.commit();
	}

	public static boolean isNameSelectorFirstTime(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_NAME_SELECTOR_FIRST_TIME, true);
	}

	public static long getInternetCheckingDelay(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return Long.parseLong(prefs.getString(PREFS_INTERNETCHECKING_DELAY,
				"10000"));
	}

	public static boolean isSoundEnabled(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_SOUND_ENABLED, true);
	}

	public static boolean isVibrationEnabled(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_VIBRATION_ENABLED, true);
	}

	public static boolean isOnExitDeleteCurrentName(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_ON_EXIT_DELETE_CURRENT_NAME, true);
	}

	public static boolean isShowHidenButton(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_SHOW_HIDEN_BUTTON, false);
	}

	public static void setShowHidenButton(Context context, boolean status) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(PREFS_IS_SHOW_HIDEN_BUTTON, status);
		edit.commit();
	}

	public static String getHtmlFromAsset(Context context, String url) {
		InputStream is;
		StringBuilder builder = new StringBuilder();
		String htmlString = null;
		try {
			is = context.getAssets().open(url);
			if (is != null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}

				htmlString = builder.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return htmlString;
	}

	public static void setShowHelpOnlyInServerMode(Context context,
			boolean status) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(PREFS_SHOW_HELP_ONLY_IN_SERVER_MODE, status);
		edit.commit();
	}

	public static boolean isShowHelpOnlyInServerMode(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_SHOW_HELP_ONLY_IN_SERVER_MODE, false);
	}

	public static void setUserName(Context context, String username) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(Settings.PREFS_USERNAME, username);
		edit.commit();
	}

	public static void setPhoneNumber(Context context, String phoneNumber) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(Settings.PREFS_PHONE_NUMBER, phoneNumber);
		edit.commit();
	}

	public static String getUserName(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getString(PREFS_USERNAME, null);
	}

	public static String getPhoneNumber(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getString(PREFS_PHONE_NUMBER, null);
	}

	public static void setBuildNumber(Context context, int buildNumber) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt(Settings.PREFS_APP_BUILD_NUMBER, buildNumber);
		edit.commit();
	}

	public static int getBuildNumber(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getInt(PREFS_APP_BUILD_NUMBER, -1);
	}
}
