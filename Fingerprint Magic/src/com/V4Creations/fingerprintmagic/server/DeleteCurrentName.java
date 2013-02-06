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

public class DeleteCurrentName extends AsyncTask<String, String, String> {
	private Context mcontext;
	String imei;
	ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	String TAG = "DeleteName";
	ProgramControlerCurrentNameInterface programControlerCurrentNameInterface;

	public DeleteCurrentName(Context context) {
		mcontext = context;
		imei = Settings.getImei(mcontext);
		programControlerCurrentNameInterface = (ProgramControlerCurrentNameInterface) context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(mcontext);
		pDialog.setMessage(mcontext
				.getString(R.string.deleting_selected_name_from_server));
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	protected String doInBackground(String... args) {
		String infoStatus;
		if (!SystemFeatureChecker.isInternetConnection(mcontext)) {
			infoStatus = mcontext
					.getString(R.string.deletion_failed_internet_not_available);
			return infoStatus;
		}
		String URL = Settings.URL + "delete_userid.php";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("imei", imei));
		JSONObject json;
		try {
			json = jsonParser.makeHttpRequest(URL, "POST", params);
			try {
				int success = json.getInt(Settings.SUCCESS);
				if (success == 1) {
					infoStatus = mcontext
							.getString(R.string.name_deleted_from_server);
					Settings.setCurrentName(mcontext, null);
				} else {
					infoStatus = mcontext.getString(R.string.no_name_in_server);
				}
			} catch (JSONException e) {
				infoStatus = mcontext.getString(R.string.sorry_deletion_failed);
			}
		} catch (Exception e1) {
			infoStatus=e1.getMessage();
		}

		return infoStatus;
	}

	protected void onPostExecute(String string) {
		Toast.makeText(mcontext, string, Toast.LENGTH_SHORT).show();
		programControlerCurrentNameInterface.ControlProgram();
		pDialog.dismiss();
	}
}
