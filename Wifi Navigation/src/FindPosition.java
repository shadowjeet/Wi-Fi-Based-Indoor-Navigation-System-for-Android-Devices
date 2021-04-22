package com.example.wifinavigation_find;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
public class FindPosition extends Activity {
	Button findOnMap, getAxis;
	RadioGroup APS;
	RadioButton AP1, AP2, AP3;
	WifiManager wifiManager;
	List<ScanResult> wifiScanList;
	String BSSID, X_AXIS, Y_AXIS;
	Timer scanTimer;
	public Handler scanHandler;
	TextView scanCounter, ap1Signal, ap2Signal, ap3Signal, x_axis, y_axis;
	EditText AverageSignals, AP1Signal, AP2Signal, AP3Signal;
	boolean run = true;
	int count = 0;
	int RSS = 0;
	int AvgRSS = 0;
	File folder, AP1File, AP2File, AP3File;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_position);
		initialize();
		// methods for all button clicks
		clickListener();
	}
	public void initialize() {
		// This button starts MapActivity.java
		findOnMap = (Button) findViewById(R.id.find_On_Map);
		getAxis = (Button) findViewById(R.id.getAxis);
		APS = (RadioGroup) findViewById(R.id.radioGroup);
		// calling wifi scanning service
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		// setting times for scanning
		scanTimer = new Timer();
		scanHandler = new Handler();
		// all text views
		scanCounter = (TextView) findViewById(R.id.scanCounter);		
		x_axis = (TextView) findViewById(R.id.x_axis);
		y_axis = (TextView) findViewById(R.id.y_axis);	
		// file handling
		folder = getExternalFilesDir("FingerPrints");
		AP1File = new File(folder, "AP1.txt");
		AP2File = new File(folder, "AP2.txt");
		AP3File = new File(folder, "AP3.txt");
	}
	// this is a method for all the buttons clicked
	public void clickListener() {
		findOnMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent mapActivity = new Intent(						"com.example.wifinavigation_find.MAPACTIVITY");
				startActivity(mapActivity);
			}
		});
		getAxis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				findStrongSignal();
			}
		});

		}
	public void chooseAPs(View v) {
		boolean checked = ((RadioButton) v).isChecked();
		switch (v.getId()) {
		case R.id.AP1:
			if (!checked) {
				Toast.makeText(getApplicationContext(),
						"Please choose Access Point.", Toast.LENGTH_SHORT)
						.show();
			} else {
				BSSID = "90:f6:52:98:c4:a2";
				Toast.makeText(getApplicationContext(), "AP1 checked",
						Toast.LENGTH_SHORT).show();
				run = true;
				scanHandler.postDelayed(scanTimer, 1000); // 3000 is for 3
															// seconds
				Toast.makeText(getApplicationContext(), "Scanning Started",
						Toast.LENGTH_SHORT).show();
				Log.d("Start", "Scan started");
				AverageSignals = (EditText) findViewById(R.id.AP1Signal);
			}
			break;
		case R.id.AP2:
			if (!checked) {
				Toast.makeText(getApplicationContext(),
						"Please choose Access Point.", Toast.LENGTH_SHORT)
						.show();
			} else {
				BSSID = "90:f6:52:98:c4:a2";
				Toast.makeText(getApplicationContext(), "AP2 checked",
						Toast.LENGTH_SHORT).show();
				run = true;
				scanHandler.postDelayed(scanTimer, 1000); // 3000 is for 3
															// seconds
				Toast.makeText(getApplicationContext(), "Scanning Started",
						Toast.LENGTH_SHORT).show();
				Log.d("Start", "Scan started");

				AverageSignals = (EditText) findViewById(R.id.AP2Signal);
			}
			break;
		case R.id.AP3:
			BSSID = "f8:d1:11:79:52:90";
			Toast.makeText(getApplicationContext(), "AP3 checked",
					Toast.LENGTH_SHORT).show();
			run = true;
			scanHandler.postDelayed(scanTimer, 1000); // 3000 is for 3														// seconds
			Toast.makeText(getApplicationContext(), "Scanning Started",
					Toast.LENGTH_SHORT).show();
			Log.d("Start", "Scan started");

			AverageSignals = (EditText) findViewById(R.id.AP3Signal);
			break;

		}
	}
	// This method scans signal from access points returns signal level
	public int onReceive() {
		wifiManager.startScan();
		wifiScanList = wifiManager.getScanResults();
		int ListSize = wifiScanList.size();
		int allvalues = 0;
		for (int i = 0; i < ListSize; i++) {
			String ssid = wifiScanList.get(i).SSID;
			String bssid = wifiScanList.get(i).BSSID;
			int intFrequency = wifiScanList.get(i).frequency;
			int intLevel = wifiScanList.get(i).level;
			String frequency = String.valueOf(intFrequency);
			String leveldBm = String.valueOf(intLevel);
			if (bssid.equals(BSSID)) {
				// allvalues = (leveldBm + " | " + ssid + " | " + bssid);

				Log.d("BSSID", bssid);
				Log.d("level okok", leveldBm);
				allvalues = intLevel;
			}

		}
		return allvalues;
	}
	private class Timer implements Runnable {
		@Override
		public void run() {
			if (run) {
				if (count != 5) {
					count++;
					scanCounter
							.setText("Scan Counter:" + String.valueOf(count));

					int dBm = Math.abs(onReceive());
					Log.d("DBM", String.valueOf(dBm));
					RSS = RSS + dBm;
					Log.d("RSS :", String.valueOf(RSS));

				} else {
					AvgRSS = RSS / 5;
					Log.d("Average", String.valueOf(AvgRSS));
					allAverageValues(AvgRSS);
					run = false;
					count = 0;
					RSS = 0;
				}
				scanHandler.postDelayed(scanTimer, 1000);
			}
		}
		private void allAverageValues(int avgRSS) {
			AverageSignals.setText("-" + String.valueOf(avgRSS));
		}
	}

	public void findStrongSignal() {

		AP1Signal = (EditText) findViewById(R.id.AP1Signal);
		AP2Signal = (EditText) findViewById(R.id.AP2Signal);
		AP3Signal = (EditText) findViewById(R.id.AP3Signal);
		int apsig1 = Math.abs(Integer.parseInt(AP1Signal.getText().toString()));
		int apsig2 = Math.abs(Integer.parseInt(AP2Signal.getText().toString()));
		int apsig3 = Math.abs(Integer.parseInt(AP3Signal.getText().toString()));
		Toast.makeText(getApplicationContext(), "THis is happening",
				Toast.LENGTH_SHORT).show();
		if (apsig1 == 0 || apsig2 == 0 || apsig3 == 0) {
			Toast.makeText(getApplicationContext(), "Access Point missing",
					Toast.LENGTH_SHORT).show();
		} else {
			Log.d("AP1Sig", String.valueOf(apsig1));
			// scanning from file
			if (AP1File.exists()) {
				String line;
				int AP1Sig = apsig1 - 1;
				for (int i = AP1Sig; i <= AP1Sig + 2; i++) {
					Log.d("AP1Sigbbb", String.valueOf(i));
					if (GetterSetter.x == 0 && GetterSetter.y == 0) {
						try {
							BufferedReader br = new BufferedReader(new FileReader(AP1File));							
							while ((line = br.readLine()) != null) {
								// display line starting with particular string

								if (line.startsWith(String.valueOf(i))) {
									Log.d("AP1Sig", String.valueOf(i));
				String[] oneFingerprintLine = line.split(" | ");
				X_AXIS = oneFingerprintLine[2];
				Y_AXIS = oneFingerprintLine[4];
				x_axis.setText(X_AXIS);
				y_axis.setText(Y_AXIS);
			GetterSetter.x = Float.parseFloat(X_AXIS);
			GetterSetter.y = Float.parseFloat(Y_AXIS);
									Toast.makeText(getApplicationContext(),											X_AXIS + Y_AXIS, Toast.LENGTH_SHORT)				.show();

		Log.d(X_AXIS, X_AXIS);
		Log.d(Y_AXIS, Y_AXIS);								break;
								}
							}
						} catch (IOException e) {
						}
					}
				}
			}
			else {
				Toast.makeText(getApplicationContext(), "File not found",
						Toast.LENGTH_SHORT).show();
			}
			if (AP2File.exists()) {
				String line;
				int AP2Sig = apsig2 - 1;
				for (int i = AP2Sig; i <= AP2Sig + 2; i++) {
					Log.d("AP2Sigbbb", String.valueOf(i));
if (GetterSetter.x == 0 && GetterSetter.y == 0) {
		try {
		BufferedReader br = new BufferedReader(									new FileReader(AP2File));
							while ((line = br.readLine()) != null) {
								// display line starting with particular string

								if (line.startsWith(String.valueOf(i))) {
									Log.d("AP2Sig", String.valueOf(i));
									String[] oneFingerprintLine = line
											.split(" | ");

									X_AXIS = oneFingerprintLine[2];
									Y_AXIS = oneFingerprintLine[4];

									x_axis.setText(X_AXIS);
									y_axis.setText(Y_AXIS);

									GetterSetter.x = Float.parseFloat(X_AXIS);
									GetterSetter.y = Float.parseFloat(Y_AXIS);

									Toast.makeText(getApplicationContext(),
											X_AXIS + Y_AXIS, Toast.LENGTH_SHORT)
											.show();

									Log.d(X_AXIS, X_AXIS);
									Log.d(Y_AXIS, Y_AXIS);

									break;
								}
							}
						} catch (IOException e) {

						}

					}

				}

				// set the edittext with the text of file}catch (IOException e){

			} else {
				Toast.makeText(getApplicationContext(), "File not found",
						Toast.LENGTH_SHORT).show();
			}
			if (AP3File.exists()) {
				String line;
				int AP3Sig = apsig3 - 1;

				for (int i = AP3Sig; i <= AP3Sig + 2; i++) {
					Log.d("AP3Sigbbb", String.valueOf(i));
					if (GetterSetter.x == 0 && GetterSetter.y == 0) {
						try {
							BufferedReader br = new BufferedReader(
									new FileReader(AP3File));
							while ((line = br.readLine()) != null) {
								// display line starting with particular string

								if (line.startsWith(String.valueOf(i))) {
									Log.d("AP3Sig", String.valueOf(i));
									String[] oneFingerprintLine = line
											.split(" | ");

									X_AXIS = oneFingerprintLine[2];
									Y_AXIS = oneFingerprintLine[4];

									x_axis.setText(X_AXIS);
									y_axis.setText(Y_AXIS);

									GetterSetter.x = Float.parseFloat(X_AXIS);
									GetterSetter.y = Float.parseFloat(Y_AXIS);

									Toast.makeText(getApplicationContext(),
											X_AXIS + Y_AXIS, Toast.LENGTH_SHORT)
											.show();

									Log.d(X_AXIS, X_AXIS);
									Log.d(Y_AXIS, Y_AXIS);

									break;

								}
							}
						} catch (IOException e) {

						}

					}

				}

				// set the edittext with the text of filecatch (IOException e){

			} else {
				Toast.makeText(getApplicationContext(), "File not found",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_position, menu);
		return true;
	}
}
