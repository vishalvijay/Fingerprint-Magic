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
import com.V4Creations.fingerprintmagic.programinterface.ProgramControlerCurrentNameInterface;
import com.V4Creations.fingerprintmagic.system.JSONParser;
import com.V4Creations.fingerprintmagic.system.Settings;
import com.V4Creations.fingerprintmagic.system.SystemFeatureChecker;

/**
 * @author Vishal Vijay
 */

public class InsertName extends AsyncTask<String, String, String> {
	private Context mcontext;
	String imei;
	String name;
	String currentName = "";
	ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	String TAG = "InsertName";
	ProgramControlerCurrentNameInterface programControlerCurrentNameInterface;

	public InsertName(Context context, String name) {
		mcontext = context;
		imei = Settings.getImei(mcontext);
		this.name = name;
		programControlerCurrentNameInterface = (ProgramControlerCurrentNameInterface) context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(mcontext);
		pDialog.setMessage(mcontext.getString(R.string.please_wait_sending)
				+ name + mcontext.getString(R.string.to_server));
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	protected String doInBackground(String... args) {
		String infoStatus;
		if (!SystemFeatureChecker.isInternetConnection(mcontext)) {
			infoStatus = mcontext
					.getString(R.string.sending_failed_internet_not_available);
			return infoStatus;
		}
		String URL = Settings.URL + "insert_name.php";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("imei", imei));
		params.add(new BasicNameValuePair("name", name));
		JSONObject json;
		try {
			json = jsonParser.makeHttpRequest(URL, "POST", params);
			try {
				int success = json.getInt(Settings.SUCCESS);
				if (success == 1) {
					currentName = name;
					infoStatus = name
							+ mcontext.getString(R.string.successfully_send);
				} else {
					infoStatus = mcontext
							.getString(R.string.sorry_sending_failed);
				}
			} catch (JSONException e) {
				infoStatus = mcontext.getString(R.string.sorry_sending_failed);
			}
		} catch (Exception e1) {
			infoStatus = e1.getMessage();
		}

		return infoStatus;
	}

	protected void onPostExecute(String string) {
		Toast.makeText(mcontext, string, Toast.LENGTH_SHORT).show();
		if (!currentName.equals(""))
			Settings.setCurrentName(mcontext, currentName);
		programControlerCurrentNameInterface.ControlProgram();
		pDialog.dismiss();
	}
}
