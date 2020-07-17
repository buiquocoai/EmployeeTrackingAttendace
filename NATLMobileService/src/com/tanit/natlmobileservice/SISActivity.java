package com.tanit.natlmobileservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class SISActivity extends Activity {
	private static String SOAP_ACTION = "http://sms.natl.com.vn/SIS";
	private static String NAMESPACE = "http://sms.natl.com.vn";
	private static String METHOD_NAME = "SIS";
	private static String URL = "http://sms.natl.com.vn:8082/MobileService.asmx";

	private TextView tv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sis);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		tv = (TextView) findViewById(R.id.lbl_sis_statistic_message);
		this.QuerySIS();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		android.os.Debug.stopMethodTracing();
	}

	private void QuerySIS() {
		new MyTask().execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
				// /error_message = e.toString();
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
					tv.setText(res);
					Toast.makeText(getApplicationContext(), error_message,
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
