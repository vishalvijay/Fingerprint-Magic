package com.V4Creations.fingerprintmagic;

import java.util.Locale;

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
import com.V4Creations.fingerprintmagic.programinterface.ProgramControlerCurrentNameInterface;
import com.V4Creations.fingerprintmagic.programinterface.ProgramControlerInterface;
import com.V4Creations.fingerprintmagic.server.DeleteCurrentName;
import com.V4Creations.fingerprintmagic.server.InsertName;
import com.V4Creations.fingerprintmagic.server.SendBugReport;
import com.V4Creations.fingerprintmagic.system.Settings;
import com.V4Creations.fingerprintmagic.system.StringOperator;
import com.V4Creations.fingerprintmagic.system.SystemFeatureChecker;

/**
 * @author Vishal Vijay
 */

public class NameSelectorActivity extends Activity implements
		ProgramControlerCurrentNameInterface, ProgramControlerInterface {

	// Variable declaration
	String TAG = "NameSelectorActivity";
	private int MODE;
	private LinearLayout nameList;
	private LinearLayout systemList;
	private FingerprintDB fingerprintDB;
	private int displayWidth;
	final int BROWSER_ACTIVATION_REQUEST = 2; // request code
	private TextView userIdTextView;
	private TextView currentNameTextView;
	private boolean isExit = false;
	Dialog dialog;
	Dialog helpDialog;

	// Variable declaration
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.name_selector_layout);
		init();
		fillNameList();
	}

	private void init() {
		fingerprintDB = new FingerprintDB(getApplicationContext());
		displayWidth = SystemFeatureChecker.getDisplayWidth(this);
		MODE = getIntent().getIntExtra("mode", Settings.SINGLE_MODE);

		nameList = (LinearLayout) findViewById(R.id.nameList);
		systemList = (LinearLayout) findViewById(R.id.systemList);
		fillSystemList();
		if (MODE == Settings.SINGLE_MODE) {
			tempActivityResult();
			hideInfoBar();
		} else {
			userIdInit();
			currentNameInit();
		}
	}
	private void fillSystemList() {
		if (MODE == Settings.MULTI_MODE)
			for (int i = 0; i < Settings.CONSTANT_NAMES_MULTY_MODE.length; i++)
				addButtonToSystemList(Settings.CONSTANT_NAMES_MULTY_MODE[i]);
		else
			for (int i = 0; i < Settings.CONSTANT_NAMES_SINGL_MODE.length; i++)
				addButtonToSystemList(Settings.CONSTANT_NAMES_SINGL_MODE[i]);
	}

	private void addButtonToSystemList(String name) {
		Button systemListButton = new Button(this);
		systemListButton.setLayoutParams(new LinearLayout.LayoutParams(
				displayWidth, LinearLayout.LayoutParams.MATCH_PARENT));
		if (name.equals(Settings.CONSTANT_NAMES_MULTY_MODE[0])) {
			systemListButton.setBackgroundResource(R.drawable.red_button);
			systemListButton.setOnClickListener(deleteListButtonClick);
		} else {
			systemListButton.setBackgroundResource(R.drawable.blue_button);
			systemListButton.setOnClickListener(nameListButtonClick);
		}
		systemListButton.setSoundEffectsEnabled(false);
		systemListButton.setText(name);
		systemListButton.setTextSize(13);

		systemList.addView(systemListButton);
		systemListButton.setTag(systemListButton.getText().toString());

	}

	private void userIdInit() {
		userIdTextView = (TextView) findViewById(R.id.userIdTextView);
		setUserId();
	}

	private void currentNameInit() {
		currentNameTextView = (TextView) findViewById(R.id.currentNameTextView);
		setCurrentName();
	}

	private void hideInfoBar() {
		LinearLayout indfoBarLinearLayout = (LinearLayout) findViewById(R.id.infoBarLinearLayout);
		indfoBarLinearLayout.setVisibility(View.GONE);
	}

	private void setCurrentName() {
		String name = Settings.getCurrentName(getApplicationContext());
		if (name == null)
			currentNameTextView
					.setText(getString(R.string.selected_name_not_yet));
		else
			currentNameTextView.setText(getString(R.string.selected_name)
					+ name);
	}

	private void setUserId() {
		userIdTextView.setText(getString(R.string.user_id)
				+ Settings.getImei(getApplicationContext()));
	}

	private void fillNameList() {
		Cursor cursor = fingerprintDB.getAllNames();
		while (cursor.moveToNext()) {
			addButtonToNameList(cursor.getString(cursor.getColumnIndex("name"))
					.toString());
		}
		cursor.close();
	}

	private void tempActivityResult() {
		Intent resultData = new Intent();
		resultData.putExtra("name", (String) null);
		setResult(Activity.RESULT_OK, resultData);
	}

	private void addButtonToNameList(String name) {
		Button nameListButton = new Button(this);
		nameListButton.setLayoutParams(new LinearLayout.LayoutParams(
				displayWidth, LinearLayout.LayoutParams.MATCH_PARENT));
		nameListButton.setBackgroundResource(R.drawable.black_button);
		nameListButton.setOnClickListener(nameListButtonClick);
		nameListButton.setSoundEffectsEnabled(false);
		nameListButton.setText(name);
		nameListButton.setTextSize(16);

		nameList.addView(nameListButton);
		nameListButton.setTag(nameListButton.getText().toString());
	}

	private OnClickListener nameListButtonClick = new OnClickListener() {
		public void onClick(View v) {
			String name = (String) v.getTag();
			if (MODE == Settings.SINGLE_MODE)
				prepareResult(name);
			else
				sendNameToServer(name);
		}
	};
	private OnClickListener deleteListButtonClick = new OnClickListener() {
		public void onClick(View v) {
			if (MODE == Settings.MULTI_MODE)
				deleteCurrentName();
			else
				prepareResult(null);
		}
	};

	private void sendNameToServer(String name) {
		new InsertName(this, name).execute();
	}

	private void prepareResult(String name) {
		Intent resultData = new Intent();
		resultData.putExtra("name", name);
		setResult(Activity.RESULT_OK, resultData);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server_mode_menu, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.addMenu:
			showAddNameDialog();
			return true;
		case R.id.deleteMenu:
			Intent deleteIntent = new Intent(NameSelectorActivity.this,
					DeleteNameActivity.class);
			startActivityForResult(deleteIntent, BROWSER_ACTIVATION_REQUEST);
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
		case R.id.about:
			showDialog(Settings.DIALOG_ABOUT);
			return true;
		case R.id.help:
			showHelpDialog();
			return true;
		}
		return false;
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BROWSER_ACTIVATION_REQUEST) {
			if (resultCode == RESULT_OK) {
				refill();
			}
		}
	}

	private void refill() {
		if (((LinearLayout) nameList).getChildCount() > 0)
			((LinearLayout) nameList).removeAllViews();
		fillNameList();
	}

	private void showAddNameDialog() {

		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.add_name_layout);

		final EditText nameEditText = (EditText) dialog
				.findViewById(R.id.nameEditText);
		Button addNameButton = (Button) dialog.findViewById(R.id.addNameButton);
		addNameButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = nameEditText.getText().toString();
				if (name.equals("")) {
					Toast.makeText(getApplicationContext(),
							R.string.please_enter_a_name, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (isValidName(name))
					addName(StringOperator.toFullNameFormate(name));
				else
					Toast.makeText(getApplicationContext(),
							R.string.invalid_name_error, Toast.LENGTH_SHORT)
							.show();
			}

			private void addName(String name) {
				if (fingerprintDB.insertName(name) == -1)
					Toast.makeText(getApplicationContext(),
							R.string.duplicate_entry, Toast.LENGTH_SHORT)
							.show();
				else {
					nameEditText.setText("");
					nameEditText.requestFocus();
					Toast.makeText(getApplicationContext(),
							name + getString(R.string.successfully_added),
							Toast.LENGTH_SHORT).show();
					refill();
				}
			}
		});
		dialog.setTitle("Add Name");
		dialog.show();
	}

	private boolean isValidName(String name) {
		name = name.toLowerCase(Locale.ENGLISH);
		for (int i = 0; i < Settings.CONSTANT_NAMES_MULTY_MODE.length; i++)
			if (name.equals(Settings.CONSTANT_NAMES_MULTY_MODE[i]
					.toLowerCase(Locale.ENGLISH)))
				return false;
		return true;
	}

	@Override
	public void onBackPressed() { // method for exit confirmation
		if (MODE == Settings.MULTI_MODE) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					NameSelectorActivity.this);
			builder.setMessage(R.string.are_you_sure)
					.setCancelable(false)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									isExit = true;
									if (Settings
											.isOnExitDeleteCurrentName(getApplicationContext()))
										deleteCurrentName();
									else
										ControlProgram();
								}
							})
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.setTitle(R.string.exit);
			alert.setIcon(android.R.drawable.ic_dialog_alert);
			alert.show();
		} else
			super.onBackPressed();
	}

	private void deleteCurrentName() {
		new DeleteCurrentName(this).execute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		fingerprintDB.close();
	}

	@Override
	public void ControlProgram() {
		if (isExit)
			NameSelectorActivity.this.finish();
		else
			setCurrentName();
	}

	@Override
	public void ControlProgram(int errorStatus) {
		if (errorStatus == 0)
			dialog.dismiss();
	}
}
