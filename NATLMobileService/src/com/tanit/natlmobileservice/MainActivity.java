package com.tanit.natlmobileservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static String SOAP_ACTION = "http://sms.natl.com.vn/MX";
	private static String NAMESPACE = "http://sms.natl.com.vn";
	private static String METHOD_NAME = "MX";
	private static String URL = "http://sms.natl.com.vn:8082/MobileService.asmx";

	static final String STATE_DEVICE_OWNER = "state.device.owner.value";
	static final String STATE_ACCESS_CODE = "state.access.code.value";
	private String device_owner = "Unknown";
	private String access_code = "0000000000";
	// /000000000000000DeviceId;310260000000000SubscriberId;310260SimOperator;89014103211118510720SimSerialNumber;

	TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.lable_hello_owner_message);
		restoreValue(savedInstanceState);
		if (savedInstanceState == null) {
			this.ValidateDevice();
		} else {
			tv.setText(device_owner.toString());
			this.SetupPermission();
		}
	}

	public void SetupPermission() {
		if (access_code.length() > 0
				&& access_code.substring(0, 1).equalsIgnoreCase("1")) {
			if (access_code.substring(1, 2).equalsIgnoreCase("1")) {
				findViewById(R.id.main_cmd_cru).setEnabled(true);
			}
			if (access_code.substring(2, 3).equalsIgnoreCase("1")) {
				findViewById(R.id.main_cmd_sis).setEnabled(true);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString(STATE_DEVICE_OWNER, device_owner);
		outState.putString(STATE_ACCESS_CODE, access_code);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		restoreValue(savedInstanceState);
	}

	private void restoreValue(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			device_owner = savedInstanceState.getString(STATE_DEVICE_OWNER);
			access_code = savedInstanceState.getString(STATE_ACCESS_CODE);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// Stop method tracing that the activity started during onCreate()
		android.os.Debug.stopMethodTracing();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	public void on_exit_click(View view) {
		super.finish();
	}

	public void on_sis_click(View view) {
		Intent intent = new Intent(this, SISActivity.class);
		startActivity(intent);
	}

	public void on_cru_click(View view) {
		Intent intent = new Intent(this, CrushingActivity.class);
		startActivity(intent);
	}

	private void ValidateDevice() {
		new MyTask().execute();
	}

	public class MyTask extends AsyncTask<Void, Void, String> {

		ProgressDialog progress;
		String response = "";
		String error_message = "";

		public void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// Initialize soap request + add parameters
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			TelephonyManager telephonyManager;
			String sTemp = "";
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			sTemp = telephonyManager.getDeviceId() + "DeviceId;";
			sTemp += telephonyManager.getSubscriberId() + "SubscriberId;";
			sTemp += telephonyManager.getSimOperator() + "SimOperator;";
			sTemp += telephonyManager.getSimSerialNumber() + "SimSerialNumber;";
			// Use this to add parameters
			request.addProperty("sY", sTemp);

			// Declare the version of the SOAP request
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			// AndroidHttpTransport androidHttpTransport = new
			// AndroidHttpTransport(URL);
			try {
				// this is the actual part that will call the webservice
				androidHttpTransport.call(SOAP_ACTION, envelope);
				// Get the SoapResult from the envelope body.
				SoapObject result = (SoapObject) envelope.bodyIn;
				if (result != null) {
					CharSequence text = result.getProperty(0).toString();
					response = text.toString();
					error_message = "Query is successful!";
				} else {
					response = "error_1";
					error_message = "Query is failed - No Response";
				}
			}

			catch (Exception e) {
				response = "error_2";
				error_message = "Could not connect to server";
				///error_message = e.toString();
			}
			return response;
		}

		@Override
		public void onPostExecute(String res) {
			if (!(res.equalsIgnoreCase(""))) {
				if (res.equalsIgnoreCase("error_1")) {
					tv.setText("");
					Toast.makeText(getApplicationContext(), error_message,
							Toast.LENGTH_LONG).show();
				} else if (res.equalsIgnoreCase("error_2")) {
					tv.setText("");
					Toast.makeText(getApplicationContext(), error_message,
							Toast.LENGTH_LONG).show();
				} else {
					String s = res;
					s = s.substring(0, s.indexOf("ACCESS_CODE"));
					device_owner = s;
					access_code = res.substring(res.indexOf("ACCESS_CODE")
							+ "ACCESS_CODE_".length());
					// Toast.makeText(getApplicationContext(),
					// access_code.substring(1, 2), Toast.LENGTH_LONG) .show();

					if (access_code.length() > 0
							&& access_code.substring(0, 1)
									.equalsIgnoreCase("1")) {
						if (access_code.substring(1, 2).equalsIgnoreCase("1")) {
							findViewById(R.id.main_cmd_cru).setEnabled(true);
						}
						if (access_code.substring(2, 3).equalsIgnoreCase("1")) {
							findViewById(R.id.main_cmd_sis).setEnabled(true);
						}
					}
					tv.setText(s);
				}
			}
		}
	}
}
