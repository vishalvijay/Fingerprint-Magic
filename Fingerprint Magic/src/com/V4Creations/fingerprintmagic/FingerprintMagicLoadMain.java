package com.V4Creations.fingerprintmagic;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.V4Creations.fingerprintmagic.programinterface.ProgramControlerInterface;
import com.V4Creations.fingerprintmagic.server.RegisterLicence;
import com.V4Creations.fingerprintmagic.system.Settings;
import com.V4Creations.fingerprintmagic.system.StringOperator;
import com.V4Creations.fingerprintmagic.system.SystemFeatureChecker;

/**
 * @author Vishal Vijay
 */
public class FingerprintMagicLoadMain extends Activity implements
		ProgramControlerInterface {

	// Variable declaration

	String TAG = "FingerprintMagicLoadMain";
	Handler loadRateBarHandler;
	boolean isNeedToOpenInClientMode;
	ProgressBar loadProgressBar;
	Button singleModeButton;
	Button beAServerButton;
	Dialog dialog;
	Dialog firstTimeDialog;

	// Variable declaration

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_fingerprint_magic_load_main);
		init();

		if (isVersionCodeChaged()) {
			showRegisterDialog();
		} else {
			doStartUp();
		}
	}

	private boolean isVersionCodeChaged() {
		if (Settings.getBuildNumber(getApplicationContext()) < SystemFeatureChecker
				.getAppVersionCode(getApplicationContext()))
			return true;
		return false;
	}

	private void doStartUp() {
		if (Settings
				.isFingerprintScannigScreenFirstTime(getApplicationContext()))
			showFirstTimeDialog();
		else
			startLoadingPage();
	}

	private void showFirstTimeDialog() {
		firstTimeDialog = new Dialog(this);
		firstTimeDialog.setContentView(R.layout.readme);
		Button okButton = (Button) firstTimeDialog.findViewById(R.id.okButton);

		WebView firstShowWebView = (WebView) firstTimeDialog
				.findViewById(R.id.firstShowWebView);
		String htmlString = Settings.getHtmlFromAsset(getApplicationContext(),
				"readme.html");
		if (htmlString != null)
			firstShowWebView.loadDataWithBaseURL("file:///android_asset/",
					htmlString, "text/html", "UTF-8", null);
		else
			Toast.makeText(this, "Not available", Toast.LENGTH_SHORT).show();

		CheckBox showHidenButtonCheckBox = (CheckBox) firstTimeDialog
				.findViewById(R.id.showHidenButtonCheckBox);
		final CheckBox dontShowCheckBox = (CheckBox) firstTimeDialog
				.findViewById(R.id.dontShowCheckBox);

		showHidenButtonCheckBox.setChecked(Settings
				.isShowHidenButton(getApplicationContext()));
		showHidenButtonCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						showButton(isChecked);
					}
				});
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dontShowCheckBox.isChecked())
					Settings.setFingerprintScannerScreenFirstTime(
							getApplicationContext(), false);
				startLoadingPage();
				firstTimeDialog.dismiss();
			}
		});
		firstTimeDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				});
		firstTimeDialog.setTitle("Readme");
		firstTimeDialog.show();
	}

	private void showButton(boolean isChecked) {
		if (isChecked) {
			singleModeButton.setBackgroundResource(R.drawable.left_button);
			singleModeButton.setText("Single");
			singleModeButton.setTextSize(12);
			beAServerButton.setBackgroundResource(R.drawable.rigth_button);
			beAServerButton.setText("Server");
			beAServerButton.setTextSize(12);
		} else {
			singleModeButton.setBackgroundResource(R.drawable.left_right);
			singleModeButton.setText("");
			beAServerButton.setBackgroundResource(R.drawable.left_right);
			beAServerButton.setText("");
		}
		Settings.setShowHidenButton(getApplicationContext(), isChecked);
	}

	private void registerLicense(String username, String phonenumber) {
		new RegisterLicence(this, username, phonenumber).execute();
	}

	private void showRegisterDialog() {
		if (Settings.getUserName(getApplicationContext()) != null
				&& Settings.getPhoneNumber(getApplicationContext()) != null) {
			registerLicense(Settings.getUserName(getApplicationContext()),
					Settings.getPhoneNumber(getApplicationContext()));
			return;
		}

		dialog = new Dialog(this);
		dialog.setContentView(R.layout.register_layout);

		final EditText usernameEditText = (EditText) dialog
				.findViewById(R.id.usernameEditText);
		Button registerButton = (Button) dialog
				.findViewById(R.id.registerButton);
		final EditText phoneNumberEditText = (EditText) dialog
				.findViewById(R.id.phoneNumberEditText);

		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = usernameEditText.getText().toString();
				String phoneNumber = phoneNumberEditText.getText().toString();

				if (StringOperator.isValidName(username)) {
					if (StringOperator.isPhoneNumber(phoneNumber)) {
						if (!SystemFeatureChecker
								.isInternetConnection(getApplicationContext())) {
							Toast.makeText(getApplicationContext(),
									R.string.registration_internet_error,
									Toast.LENGTH_SHORT).show();
							return;
						}
						registerLicense(
								StringOperator.toFullNameFormate(username),
								phoneNumber);
						dialog.dismiss();
					} else {
						Toast.makeText(getApplicationContext(),
								R.string.invalid_phone_number_error,
								Toast.LENGTH_SHORT).show();
						phoneNumberEditText.requestFocus();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.invalid_name_error, Toast.LENGTH_SHORT)
							.show();
					usernameEditText.requestFocus();
				}
			}
		});
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		dialog.setTitle("Register");
		dialog.show();
	}

	private void init() {
		loadRateBarHandler = new Handler();
		isNeedToOpenInClientMode = true;
		initLoadProgressBar();
		initSingleModeButton();
		initBeAServerButton();
		if (Settings.isShowHidenButton(getApplicationContext())) {
			singleModeButton.setBackgroundResource(R.drawable.left_button);
			singleModeButton.setText("Single");
			singleModeButton.setTextSize(12);
			beAServerButton.setBackgroundResource(R.drawable.rigth_button);
			beAServerButton.setText("Server");
			beAServerButton.setTextSize(12);
		}
	}

	private void initBeAServerButton() {
		beAServerButton = (Button) findViewById(R.id.beAServerButton);
		beAServerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isNeedToOpenInClientMode = false;
				openNameSelectorActivity(Settings.MULTI_MODE);
			}
		});
	}

	private void initSingleModeButton() {
		singleModeButton = (Button) findViewById(R.id.singleModeButton);
		singleModeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isNeedToOpenInClientMode = false;
				openFingerPrintScannerActivity(Settings.SINGLE_MODE);
			}
		});
	}

	private void initLoadProgressBar() {
		loadProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) loadProgressBar
				.getLayoutParams();
		params.setMargins(0, 0, 0,
				(SystemFeatureChecker.getDisplayHeight(this) / 4) - 96);
		loadProgressBar.setLayoutParams(params);
		loadProgressBar.setProgress(0);
	}

	@Override
	public void onBackPressed() { // method for exit confirmation
		finish();
	}

	private void startLoadingPage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (isNeedToOpenInClientMode) {
					if (loadProgressBar.getProgress() < 100)
						loadRateBarHandler.post(new Runnable() {
							@Override
							public void run() {
								loadProgressBar.setProgress(loadProgressBar
										.getProgress() + 1);
							}
						});
					else
						loadRateBarHandler.post(new Runnable() {
							@Override
							public void run() {
								isNeedToOpenInClientMode = false;
								openFingerPrintScannerActivity(Settings.MULTI_MODE);
							}
						});
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();
	}

	private void openFingerPrintScannerActivity(int mode) {
		Intent launchNewIntent = new Intent(FingerprintMagicLoadMain.this,
				FingerprintScannerActivity.class);
		launchNewIntent.putExtra(Settings.MODE, mode);
		startActivity(launchNewIntent);
		finish();
	}

	private void openNameSelectorActivity(int mode) {
		Intent launchNewIntent = new Intent(FingerprintMagicLoadMain.this,
				NameSelectorActivity.class);
		launchNewIntent.putExtra(Settings.MODE, mode);
		startActivity(launchNewIntent);
		finish();
	}

	@Override
	public void ControlProgram(int i) {
		if (i == 1)
			finish();
		else {
			doStartUp();
		}
	}

	@Override
	protected void onDestroy() {
		if (isNeedToOpenInClientMode == true)
			isNeedToOpenInClientMode = false;
		super.onDestroy();
	}
}
