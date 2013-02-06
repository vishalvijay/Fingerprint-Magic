package com.V4Creations.fingerprintmagic.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.V4Creations.fingerprintmagic.R;
import com.V4Creations.fingerprintmagic.programinterface.ProgramControlerGetNameInterface;
import com.V4Creations.fingerprintmagic.system.JSONParser;
import com.V4Creations.fingerprintmagic.system.Settings;

/**
 * @author Vishal Vijay
 */

public class GetName extends AsyncTask<String, String, String> {
	private Context mcontext;

	String imei;
	JSONParser jsonParser = new JSONParser();
	String TAG = "GetName";
	String infoStatus = null;
	ProgramControlerGetNameInterface programControlerGetNameInterface;

	public GetName(Context context) {
		mcontext = context;
		imei = Settings.getUserId(mcontext);
		programControlerGetNameInterface = (ProgramControlerGetNameInterface) context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	protected String doInBackground(String... args) {
		String name = null;
		String URL = Settings.URL + "get_name.php";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("imei", imei));
		JSONObject json;
		try {
			json = jsonParser.makeHttpRequest(URL, "POST", params);
			try {
				int success = json.getInt(Settings.SUCCESS);
				if (success == 1) {
					name = json.getString("name");
				} else {
					name = null;
				}
			} catch (JSONException e) {
				name = null;
			}
		} catch (Exception e1) {
			infoStatus = e1.getMessage();
		}

		return name;
	}

	protected void onPostExecute(String name) {
		if (infoStatus != null)
			Toast.makeText(mcontext, infoStatus, Toast.LENGTH_SHORT).show();
		else if (name == null)
			Toast.makeText(mcontext,
					mcontext.getString(R.string.cant_give_name),
					Toast.LENGTH_SHORT).show();
		programControlerGetNameInterface.ControlProgram(name);
	}
}
