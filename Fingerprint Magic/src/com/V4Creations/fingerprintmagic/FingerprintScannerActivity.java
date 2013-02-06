package com.V4Creations.fingerprintmagic;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.V4Creations.fingerprintmagic.programinterface.ProgramControlerGetNameInterface;
import com.V4Creations.fingerprintmagic.programinterface.ProgramControlerInterface;
import com.V4Creations.fingerprintmagic.server.GetName;
import com.V4Creations.fingerprintmagic.server.SendBugReport;
import com.V4Creations.fingerprintmagic.system.Settings;
import com.V4Creations.fingerprintmagic.system.SystemFeatureChecker;

/**
 * @author Vishal Vijay
 */
public class FingerprintScannerActivity extends Activity implements
		ProgramControlerGetNameInterface, ProgramControlerInterface {
	// Variable declaration
	String TAG = "FingerprintScannerActivity";
	String resultName = null;
	private int MODE;

	Button nameChooserButton;
	Button fingerButton;
	final int BROWSER_ACTIVATION_REQUEST = 2; // request code

	Handler mHandler;

	TextView messageTextView;
	boolean isMessageRunning;
	int messageCurrentLength;

	TextView statusTextView;
	boolean isStatusMessageRunning;
	int statusMessageLength;

	TextView internetConnectionTextView;
	boolean isInternetCheckRunning;

	MediaPlayer scanningMediaPlayer;
	MediaPlayer defaultMediaPlayer;
	MediaPlayer errorMediaPlayer;
	Vibrator vibrator;

	String statusMessage[];
	String message[];
	String resultMessage[];

	Dialog dialog;
	Dialog helpDialog;

	// Variable declaration
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.fingerprint_scanner_layout);
		MODE = getIntent().getIntExtra(Settings.MODE, Settings.MULTI_MODE);
		if (MODE == Settings.SINGLE_MODE)
			Toast.makeText(getApplicationContext(),
					R.string.single_mode_enabled, Toast.LENGTH_SHORT).show();
		mHandler = new Handler();
		init();
		if (MODE == Settings.MULTI_MODE)
			updateInternetConnection();
		startMessaging();
	}

	private void startMessaging() {
		isMessageRunning = true;
		messageCurrentLength = 0;
		statusTextView.setText(statusMessage[0]);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (isMessageRunning) {
					try {
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								messageTextView
										.setText(message[messageCurrentLength++]);
							}

						});
						if (messageCurrentLength == message.length)
							messageCurrentLength = 0;
						Thread.sleep(3000);
					} catch (Exception e) {
					}
				}
			}
		}).start();
	}

	private void stopMessaging() {
		isMessageRunning = false;
	}

	private void startStatusMessage() {
		isStatusMessageRunning = true;
		statusMessageLength = 1;

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (isStatusMessageRunning) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							messageTextView.setText(resultMessage[0]);
							statusTextView
									.setText(statusMessage[statusMessageLength++]);
							defaultSoundStart();
							scanningSoundStart();
							vibrationStart(125);
						}

					});
					try {
						Thread.sleep(1500);
					} catch (Exception e) {
					}
				}

				if (isStatusMessageRunning) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							statusTextView
									.setText(statusMessage[statusMessageLength++]);
						}

					});
					try {
						Thread.sleep(4000);
					} catch (Exception e) {
					}
				}

				if (isStatusMessageRunning) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							statusTextView
									.setText(statusMessage[statusMessageLength++]);
							messageTextView.setText(resultMessage[5]);
							scanningSoundStop();
							defaultSoundStart();
							vibrationStart(250);
						}

					});
					try {
						Thread.sleep(4000);
					} catch (Exception e) {
					}
				}

				if (isStatusMessageRunning) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							defaultSoundStart();
							statusTextView
									.setText(statusMessage[statusMessageLength++]);
						}

					});
				}

			}
		}).start();
	}

	private void stopStatusMessage() {
		isStatusMessageRunning = false;
	}

	private void startFingerprintNotDetected() {
		fingerButton.setVisibility(View.INVISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {

				mHandler.post(new Runnable() {

					@Override
					public void run() {
						messageTextView.setTextColor(Color.RED);
						statusTextView.setTextColor(Color.RED);
						messageTextView.setText("");
						statusTextView.setText("");
						scanningSoundStop();
						defaultSoundStop();
						errorSoundStart();
					}
				});
				try {

					Thread.sleep(300);
				} catch (Exception e) {
				}
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						messageTextView.setText(statusMessage[5]);
						statusTextView.setText(statusMessage[5]);
					}

				});
				try {

					Thread.sleep(600);
				} catch (Exception e) {
				}

				mHandler.post(new Runnable() {

					@Override
					public void run() {
						messageTextView.setText("");
						statusTextView.setText("");
						errorSoundStart();
					}

				});
				try {

					Thread.sleep(300);
				} catch (Exception e) {
				}
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						messageTextView.setText(statusMessage[5]);
						statusTextView.setText(statusMessage[5]);
					}

				});
				try {

					Thread.sleep(600);
				} catch (Exception e) {
				}

				mHandler.post(new Runnable() {

					@Override
					public void run() {
						statusTextView.setTextColor(internetConnectionTextView
								.getTextColors());
						messageTextView.setTextColor(internetConnectionTextView
								.getTextColors());
						startMessaging();
						statusTextView.setText(statusMessage[0]);
						fingerButton.setVisibility(View.VISIBLE);
					}

				});
			}
		}).start();
	}

	private void startShowResult() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (resultName == null
						|| resultName
								.equals(Settings.CONSTANT_NAMES_MULTY_MODE[2])) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							messageTextView.setText(resultMessage[1]);
						}

					});
					try {

						Thread.sleep(5000);
					} catch (Exception e) {
					}
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							startMessaging();
							fingerButton.setVisibility(View.VISIBLE);
						}

					});
				} else if (resultName
						.equals(Settings.CONSTANT_NAMES_MULTY_MODE[1])) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							messageTextView.setText(resultMessage[6]);
							getName();
						}

					});
				} else if (resultName
						.equals(Settings.CONSTANT_NAMES_MULTY_MODE[3])) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							messageTextView.setText(resultMessage[7]);
						}

					});
					try {

						Thread.sleep(5000);
					} catch (Exception e) {
					}
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							startMessaging();
							fingerButton.setVisibility(View.VISIBLE);
						}

					});
				} else {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							messageTextView.setText(resultMessage[2]);
						}

					});

					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							messageTextView.setText(resultMessage[3]
									+ resultName);
						}

					});

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
					}

					mHandler.post(new Runnable() {

						@Override
						public void run() {
							startMessaging();
							fingerButton.setVisibility(View.VISIBLE);
						}

					});

				}
			}
		}).start();
	}

	private void init() {
		Resources res = getResources();
		statusMessage = res.getStringArray(R.array.status_message);
		message = res.getStringArray(R.array.message);
		resultMessage = res.getStringArray(R.array.result_message);
		internetConnectionTextView = (TextView) findViewById(R.id.internetStatusTextView);
		messageTextView = (TextView) findViewById(R.id.messageTextView);
		statusTextView = (TextView) findViewById(R.id.statusTextView);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		fingerButtonInit();
		nameChooserButtonInit();
		if (MODE == Settings.MULTI_MODE)
			nameChooserButton.setVisibility(View.GONE);
		else {
			internetConnectionTextView.setCompoundDrawablesWithIntrinsicBounds(
					0, R.drawable.green, 0, 0);
			hideButtonStyle();
		}
		initSounds();
	}

	private void initSounds() {
		scanningMediaPlayer = new MediaPlayer();
		errorMediaPlayer = new MediaPlayer();
		defaultMediaPlayer = new MediaPlayer();
		scanningSoundInit();
		defaultSoundInit();
		errorSoundInit();
	}

	private void scanningSoundInit() {
		scanningMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				scanningMediaPlayer.start();
			}
		});
		scanningMediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				scanningSoundInit();
				return false;
			}
		});
	}

	private void scanningSoundStart() {

		if (Settings.isSoundEnabled(getApplicationContext())) {
			scanningMediaPlayer.reset();
			AssetFileDescriptor afd = getResources().openRawResourceFd(
					R.raw.scanning);
			try {
				scanningMediaPlayer.setDataSource(afd.getFileDescriptor(),
						afd.getStartOffset(), afd.getDeclaredLength());
			} catch (IllegalArgumentException e) {
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			}
			scanningMediaPlayer.prepareAsync();
		}
	}

	private void scanningSoundStop() {
		if (Settings.isSoundEnabled(getApplicationContext()))
			if (scanningMediaPlayer.isPlaying()) {
				scanningMediaPlayer.stop();
			}
	}

	private void defaultSoundInit() {
		defaultMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				defaultMediaPlayer.start();
			}
		});
		defaultMediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				defaultSoundInit();
				return false;
			}
		});
	}

	private void defaultSoundStart() {
		if (Settings.isSoundEnabled(getApplicationContext())) {
			defaultMediaPlayer.reset();
			AssetFileDescriptor afd = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				defaultMediaPlayer.setDataSource(afd.getFileDescriptor(),
						afd.getStartOffset(), afd.getDeclaredLength());
			} catch (IllegalArgumentException e) {
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			}
			defaultMediaPlayer.prepareAsync();
		}
	}

	private void defaultSoundStop() {
		if (Settings.isSoundEnabled(getApplicationContext()))
			if (defaultMediaPlayer.isPlaying()) {
				defaultMediaPlayer.stop();
			}
	}

	private void errorSoundInit() {

		errorMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				errorMediaPlayer.start();
			}
		});
		errorMediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				errorSoundInit();
				return false;
			}
		});
	}

	private void errorSoundStart() {
		if (Settings.isSoundEnabled(getApplicationContext())) {
			errorMediaPlayer.reset();
			AssetFileDescriptor afd = getResources().openRawResourceFd(
					R.raw.beep_error);
			try {
				errorMediaPlayer.setDataSource(afd.getFileDescriptor(),
						afd.getStartOffset(), afd.getDeclaredLength());
			} catch (IllegalArgumentException e) {
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			}
			errorMediaPlayer.prepareAsync();
		}
	}

	private void erroeSoundStop() {
		if (Settings.isSoundEnabled(getApplicationContext()))
			if (errorMediaPlayer.isPlaying()) {
				errorMediaPlayer.stop();
				// errorMediaPlayer.release();
			}
	}

	private void vibrationStart(long length) {
		if (Settings.isVibrationEnabled(getApplicationContext()))
			vibrator.vibrate(length);
	}

	private void fingerButtonInit() {
		fingerButton = (Button) findViewById(R.id.fingerButton);
		fingerButton.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					stopMessaging();
					startStatusMessage();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					stopStatusMessage();
					statusTextView.setText(statusMessage[6]);
					if (statusMessageLength > 3) {
						if (MODE == Settings.MULTI_MODE)
							getName();
						else
							getNameFromResult();

					} else {
						startFingerprintNotDetected();
					}
				}
				return false;
			}
		});
	}

	protected void getNameFromResult() {
		messageTextView.setText(resultMessage[4]);
		statusTextView.setText(statusMessage[6]);
		fingerButton.setVisibility(View.INVISIBLE);
		waitForUserStaisfaction();
	}

	void waitForUserStaisfaction() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						startShowResult();
					}

				});
			}
		}).start();
	}

	@Override
	protected void onPause() {
		scanningSoundStop();
		defaultSoundStop();
		erroeSoundStop();
		super.onPause();
	}

	private void openNameSelectorActivity(int mode) {
		Intent launchNewIntent = new Intent(FingerprintScannerActivity.this,
				NameSelectorActivity.class);
		launchNewIntent.putExtra(Settings.MODE, mode);
		startActivityForResult(launchNewIntent, BROWSER_ACTIVATION_REQUEST);
	}

	private void nameChooserButtonInit() {
		nameChooserButton = (Button) findViewById(R.id.nameChooseButton);
		nameChooserButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openNameSelectorActivity(Settings.SINGLE_MODE);
			}
		});

	}

	private void getName() {
		messageTextView.setText(resultMessage[4]);
		statusTextView.setText(statusMessage[6]);
		if (SystemFeatureChecker.isInternetConnection(getApplicationContext())) {
			fingerButton.setVisibility(View.INVISIBLE);
			new GetName(this).execute();
		} else {
			Toast.makeText(getApplicationContext(),
					R.string.sorry_error_in_internet_connection,
					Toast.LENGTH_SHORT).show();
			startMessaging();
		}
	}

	private void updateInternetConnection() {
		isInternetCheckRunning = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (isInternetCheckRunning) {
					try {
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if (SystemFeatureChecker
										.isInternetConnection(getApplicationContext())) {
									internetConnectionTextView
											.setCompoundDrawablesWithIntrinsicBounds(
													0, R.drawable.green, 0, 0);
								} else {
									internetConnectionTextView
											.setCompoundDrawablesWithIntrinsicBounds(
													0, R.drawable.red, 0, 0);
								}
							}

						});
						Thread.sleep(Settings
								.getInternetCheckingDelay(getApplicationContext()));
					} catch (Exception e) {
					}
				}
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fingerprint_scanner_menu, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.setUserIdMenu:
			showSetUserIdDialog();
			return true;
		case R.id.rateApp:
			rateAppOnPlayStore();
			return true;
		case R.id.preference:
			Intent settingsActivity = new Intent(getBaseContext(),
					PreferencesActivity.class);
			settingsActivity.putExtra(Settings.MODE, Settings.CLIENT_MODE);
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
		if (Settings.isShowHelpOnlyInServerMode(getApplicationContext())) {
			Toast.makeText(
					getApplicationContext(),
					getString(R.string.dont_have_permmision_to_see_help_from_here)
							+ Settings.URL + "help.html", Toast.LENGTH_LONG)
					.show();
			return;
		}
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
		helpDialog.setCancelable(false);
		helpDialog.show();
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

	private void showSetUserIdDialog() {

		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.set_user_id_layout);

		final EditText userIdEditText = (EditText) dialog
				.findViewById(R.id.userIdEditText);
		userIdEditText.setText(Settings.getUserId(getApplicationContext()));
		Button setUserIdButton = (Button) dialog
				.findViewById(R.id.setUserIdButton);
		setUserIdButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String userId = userIdEditText.getText().toString();
				Settings.setUserId(getApplicationContext(), userId);

				Toast.makeText(getApplicationContext(),
						R.string.user_id_sucessfully_selected,
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}

		});
		dialog.setTitle(R.string.set_user_id);
		dialog.show();
	}

	@Override
	public void onBackPressed() { // method for exit confirmation
		AlertDialog.Builder builder = new AlertDialog.Builder(
				FingerprintScannerActivity.this);
		builder.setMessage(R.string.are_you_sure)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.setTitle(R.string.exit);
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BROWSER_ACTIVATION_REQUEST) {
			if (resultCode == RESULT_OK) {
				resultName = data.getExtras().getString("name");
				if (MODE == Settings.SINGLE_MODE)
					hideButtonStyle();
			}
		}
	}

	private void hideButtonStyle() {
		if (Settings.isShowHidenButton(getApplicationContext())) {
			nameChooserButton
					.setBackgroundResource(R.drawable.name_choose_button);
			nameChooserButton.setText("Names");
			nameChooserButton.setTextSize(12);
		} else {
			nameChooserButton.setBackgroundResource(R.drawable.transparent2);
			nameChooserButton.setText("");
		}

	}

	@Override
	protected void onDestroy() {
		isInternetCheckRunning = false;
		isMessageRunning = false;
		isStatusMessageRunning = false;
		scanningMediaPlayer.release();
		defaultMediaPlayer.release();
		errorMediaPlayer.release();
		super.onDestroy();
	}

	@Override
	public void ControlProgram(String name) {
		resultName = name;
		startShowResult();
	}

	@Override
	public void ControlProgram(int errorStatus) {
		if (errorStatus == 0)
			dialog.dismiss();
	}
}
