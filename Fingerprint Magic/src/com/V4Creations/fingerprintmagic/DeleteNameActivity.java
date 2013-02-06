package com.V4Creations.fingerprintmagic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.V4Creations.fingerprintmagic.db.FingerprintDB;
import com.V4Creations.fingerprintmagic.programinterface.ProgramControlerInterface;
import com.V4Creations.fingerprintmagic.server.SendBugReport;
import com.V4Creations.fingerprintmagic.system.Settings;
import com.V4Creations.fingerprintmagic.system.SystemFeatureChecker;

/**
 * @author Vishal Vijay
 */
public class DeleteNameActivity extends Activity implements
		ProgramControlerInterface {

	// Variable declaration
	String TAG = "DeleteNameActivity";
	private LinearLayout nameList;
	private FingerprintDB fingerprintDB;
	private int displayWidth;
	private Dialog dialog;
	private Dialog helpDialog;

	// Variable declaration
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_name_layout);
		init();
		fillNameList();
	}

	private void init() {
		fingerprintDB = new FingerprintDB(getApplicationContext());
		displayWidth = SystemFeatureChecker.getDisplayWidth(this);
		nameList = (LinearLayout) findViewById(R.id.nameList);
		activityResultInit();
	}
	private void activityResultInit() {
		Intent resultData = new Intent();
		setResult(Activity.RESULT_OK, resultData);
	}

	private void fillNameList() {
		Cursor cursor = fingerprintDB.getAllNames();
		while (cursor.moveToNext()) {
			addButtonToNameList(cursor.getString(cursor.getColumnIndex("name"))
					.toString());
		}
		cursor.close();
	}

	private void addButtonToNameList(String name) {
		Button nameListButton = new Button(this);
		nameListButton.setLayoutParams(new LinearLayout.LayoutParams(
				displayWidth, LinearLayout.LayoutParams.MATCH_PARENT));
		nameListButton.setBackgroundResource(R.drawable.black_button);
		nameListButton.setSoundEffectsEnabled(false);
		nameListButton.setText(name);
		nameListButton.setTextSize(16);
		nameListButton.setOnClickListener(nameListButtonClick);
		nameList.addView(nameListButton);
		nameListButton.setTag(nameListButton.getText().toString());
	}

	private OnClickListener nameListButtonClick = new OnClickListener() {
		public void onClick(View v) {
			String name = (String) v.getTag();
			if (fingerprintDB.deleteName(name) != 0) {
				Toast.makeText(getApplicationContext(),
						name + getString(R.string.deleted), Toast.LENGTH_SHORT)
						.show();
				refill();
			} else
				Toast.makeText(
						getApplicationContext(),
						getString(R.string.error_occured_while_deleting) + name
								+ getString(R.string.full_stop),
						Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.delete_name_menu, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.deleteAll:
			onDeleteAll();
			return true;
		case R.id.rateApp:
			rateAppOnPlayStore();
			return true;
		case R.id.preference:
			Intent settingsActivity = new Intent(getBaseContext(),
					PreferencesActivity.class);
			settingsActivity.putExtra(Settings.MODE, Settings.SERVER_MODE);
			startActivity(settingsActivity);
			return true;
		case R.id.reportBug:
			showReportBugDialog();
			return true;
		case R.id.help:
			showHelpDialog();
			return true;
		case R.id.about:
			showDialog(Settings.DIALOG_ABOUT);
			return true;
		}
		return false;
	}

	private void showHelpDialog() {
		helpDialog = new Dialog(this);
		helpDialog.setContentView(R.layout.help);
		Button okButton = (Button) helpDialog.findViewById(R.id.okButton);

		WebView firstShowWebView = (WebView) helpDialog
				.findViewById(R.id.firstShowWebView);
		String htmlString = Settings.getHtmlFromAsset(getApplicationContext(),
				"help.html");
		CheckBox showHelpOnlyInServerModeCheckBox = (CheckBox) helpDialog
				.findViewById(R.id.showHelpOnlyInServerModeCheckBox);
		showHelpOnlyInServerModeCheckBox.setChecked(Settings
				.isShowHelpOnlyInServerMode(getApplicationContext()));
		showHelpOnlyInServerModeCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							Settings.setShowHelpOnlyInServerMode(
									getApplicationContext(), true);
							Toast.makeText(
									getApplicationContext(),
									R.string.now_you_can_see_the_help_only_in_server_mode,
									Toast.LENGTH_LONG).show();
						} else
							Settings.setShowHelpOnlyInServerMode(
									getApplicationContext(), false);
					}
				});

		if (htmlString != null)
			firstShowWebView.loadDataWithBaseURL("file:///android_asset/",
					htmlString, "text/html", "UTF-8", null);
		else
			Toast.makeText(this, "Not available", Toast.LENGTH_SHORT).show();

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				helpDialog.dismiss();
			}
		});
		helpDialog.setTitle("Help");
		helpDialog.show();
	}

	private void rateAppOnPlayStore() {
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(myAppLinkToMarket);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this,
					getString(R.string.unable_to_find_google_play_app),
					Toast.LENGTH_LONG).show();
		}
	}

	private void showReportBugDialog() {

		dialog = new Dialog(this);
		dialog.setContentView(R.layout.report_bug_layout);

		TextView phoneModelTextView = (TextView) dialog
				.findViewById(R.id.phoneModelTextView);
		phoneModelTextView.setText(SystemFeatureChecker.getDeviceName());
		Button reportButton = (Button) dialog.findViewById(R.id.reportButton);
		final EditText bugReportEditText = (EditText) dialog
				.findViewById(R.id.bugReportEditText);

		reportButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String bug = bugReportEditText.getText().toString();

				if (bug.length() > 10) {
					if (!SystemFeatureChecker
							.isInternetConnection(getApplicationContext())) {
						Toast.makeText(getApplicationContext(),
								R.string.internet_connection_no_available,
								Toast.LENGTH_SHORT).show();
						return;
					}
					sendBugReport(bug);
				} else {
					Toast.makeText(
							getApplicationContext(),
							R.string.bug_report_should_be_at_least_10_character,
							Toast.LENGTH_SHORT).show();
					bugReportEditText.requestFocus();
				}
			}
		});
		dialog.setTitle(R.string.report_bug);
		dialog.show();
	}

	protected void sendBugReport(String bug) {
		new SendBugReport(this, bug).execute();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		LayoutInflater factory = LayoutInflater.from(this);

		switch (id) {
		case Settings.DIALOG_ABOUT:
			final View aboutView = factory.inflate(R.layout.about, null);
			TextView versionLabel = (TextView) aboutView
					.findViewById(R.id.version_label);
			String versionName = SystemFeatureChecker
					.getAppVersionName(getApplicationContext());
			versionLabel.setText(getString(R.string.version, versionName));
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle(R.string.app_name).setView(aboutView)
					.setPositiveButton("OK", null).create();
		}

		return null;
	}

	private void refill() {
		if (((LinearLayout) nameList).getChildCount() > 0)
			((LinearLayout) nameList).removeAllViews();
		fillNameList();
	}

	public void onDeleteAll() { // method for exit confirmation
		AlertDialog.Builder builder = new AlertDialog.Builder(
				DeleteNameActivity.this);
		builder.setMessage(R.string.are_you_sure)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (fingerprintDB.deleteAllNames() != 0) {
									Toast.makeText(getApplicationContext(),
											R.string.all_names_deleted,
											Toast.LENGTH_SHORT).show();
									refill();
								} else
									Toast.makeText(
											getApplicationContext(),
											R.string.error_in_deleting_all_name,
											Toast.LENGTH_SHORT).show();
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.setTitle(R.string.delete_all);
		alert.setIcon(android.R.drawable.ic_menu_delete);
		alert.show();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		fingerprintDB.close();
	}

	@Override
	public void ControlProgram(int errorStatus) {
		if (errorStatus == 0)
			dialog.dismiss();
	}
}
