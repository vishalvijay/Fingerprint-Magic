package com.V4Creations.fingerprintmagic.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.V4Creations.fingerprintmagic.R;
import com.V4Creations.fingerprintmagic.programinterface.ProgramControlerInterface;
import com.V4Creations.fingerprintmagic.system.JSONParser;
import com.V4Creations.fingerprintmagic.system.Settings;
import com.V4Creations.fingerprintmagic.system.SystemFeatureChecker;

/**
 * @author Vishal Vijay
 */

public class RegisterLicence extends AsyncTask<String, String, String> {
	private Context mcontext;
	String imei;
	String username;
	String phonenumber;
	ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	String TAG = "RegisterImei";
	ProgramControlerInterface programControlerInterface;
	int errorStatus;

	public RegisterLicence(Context context, String username, String phonenumber) {
		mcontext = context;
		imei = SystemFeatureChecker.getImei(mcontext);

		if (imei == null)
			imei = Settings.getImei(mcontext);

		programControlerInterface = (ProgramControlerInterface) context;
		errorStatus = 1;
		this.username = username;
		this.phonenumber = phonenumber;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(mcontext);
		pDialog.setMessage(mcontext.getString(R.string.please_wait_registering));
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	protected String doInBackground(String... args) {
		String infoStatus;
		String URL = Settings.URL + "make_registration.php";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("imei", imei));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("phonenumber", phonenumber));
		params.add(new BasicNameValuePair("phonemodel", SystemFeatureChecker
				.getDeviceName()));
		params.add(new BasicNameValuePair("appversion", SystemFeatureChecker
				.getAppVersionName(mcontext)));
		params.add(new BasicNameValuePair("androidversion",
				SystemFeatureChecker.getAndroidVersion()));
		JSONObject json;
		try {
			json = jsonParser.makeHttpRequest(URL, "POST", params);
			try {
				int success = json.getInt(Settings.SUCCESS);
				if (success == 1) {
					Settings.setImei(mcontext, imei);
					Settings.setUserName(mcontext, username);
					Settings.setPhoneNumber(mcontext, phonenumber);
					Settings.setBuildNumber(mcontext,
							SystemFeatureChecker.getAppVersionCode(mcontext));
					infoStatus = mcontext
							.getString(R.string.user_successfully_registered);
					errorStatus = 0;
				} else {
					infoStatus = mcontext
							.getString(R.string.error_in_registration);
				}
			} catch (JSONException e) {
				infoStatus = mcontext.getString(R.string.error_in_registration);
			}
		} catch (Exception e1) {
			infoStatus = e1.getMessage();
		}
		return infoStatus;
	}

	protected void onPostExecute(String string) {
		Toast.makeText(mcontext, string, Toast.LENGTH_SHORT).show();
		programControlerInterface.ControlProgram(errorStatus);
		pDialog.dismiss();
	}
}