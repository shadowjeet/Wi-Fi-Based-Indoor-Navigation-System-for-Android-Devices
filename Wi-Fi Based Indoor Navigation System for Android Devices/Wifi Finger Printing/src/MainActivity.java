package com.example.average;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import com.example.average.R;
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

public class MainActivity extends Activity {
	WifiManager wifiManager;
	List<ScanResult> wifiScanList;
	TextView counterText;
	EditText x_co, y_co;
	String x_axis, y_axis;
	Button scanBut, stopBut, readFile;
	RadioGroup APS;
	String BSSID;
	File folder, dbFile;
	public Handler scanHandler;
	boolean run = true;
	int count = 0;
	int RSS = 0;
	int AvgRSS = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// calling wifi scanning service
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		folder = getExternalFilesDir("FingerPrints");		

		// counting no of line inserted in file
		counterText = (TextView) findViewById(R.id.counter);
		// EditText where x and y co-ordinates are entered.
		x_co = (EditText) findViewById(R.id.X_CO);
		y_co = (EditText) findViewById(R.id.Y_CO);		
		APS = (RadioGroup) findViewById(R.id.rGroup);
		// button to start scanning
		scanBut = (Button) findViewById(R.id.scanBut);
		// button to stop scanning
		stopBut = (Button) findViewById(R.id.stopBut);
		// button to start ReadFile activity
		readFile = (Button) findViewById(R.id.startReadActivity);
		// calling scanHandler class
		scanHandler = new Handler();
		// scanHandler.postDelayed(scanTimer, 3000); // 3000 is for 3 seconds
		// addValues();
		scanBut.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				run = true;
				scanHandler.postDelayed(scanTimer, 1000); // 3000 is for 3 //seconds
				Toast.makeText(getApplicationContext(), "Scanning Started",
						Toast.LENGTH_SHORT).show();
				Log.d("Start", "Scan started");
			}
		});

		stopBut.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				run = false;
				count = 0;
				RSS = 0;
				Toast.makeText(getApplicationContext(), "Scanning Stopped",
						Toast.LENGTH_SHORT).show();
				Log.d("Stop", "Scan Stopped");
			}
		});

		readFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent("com.example.average.ReadFile");
				startActivity(intent);
			}
		});
		APS.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int Id) {
				switch (Id) {
				case R.id.AP1:
					BSSID = "00:15:6d:5e:ec:7e";
					dbFile = new File(folder, "AP1.txt");

					Toast.makeText(getApplicationContext(), "AP1 checked",							Toast.LENGTH_SHORT).show();
					break;
				case R.id.AP2:
					BSSID = "f8:d1:11:8c:16:42";
					dbFile = new File(folder, "AP2.txt");
					Toast.makeText(getApplicationContext(), "AP2 checked",
							Toast.LENGTH_SHORT).show();
					break;
				case R.id.AP3:
					BSSID = "f8:d1:11:79:52:90";
					dbFile = new File(folder, "AP3.txt");
					Toast.makeText(getApplicationContext(), "AP3 checked",							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

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
			double doubleDistance = calculateDistance(intLevel, intFrequency);
			String distance = String.format("%.05s m",
					String.valueOf(doubleDistance));
			// Log.d("bssid", bssid);
			if (bssid.equals(BSSID)) {
				// allvalues = (leveldBm + " | " + ssid + " | " + bssid);
				Log.d("BSSID", bssid);
				Log.d("level okok", leveldBm);
				allvalues = intLevel;
			}			
		}
		return allvalues;
	}

	

	public void addValues(int avgRSS) {
		x_axis = x_co.getText().toString();
		y_axis = y_co.getText().toString();
		String data = String.valueOf(avgRSS) + " | " + x_axis + " | " + y_axis
				+ " | " + BSSID;
		// File folder = getExternalFilesDir("Scanner");
		// File dbFile = new File(folder, "data.txt");
		writeData(dbFile, data);
	}
	public void writeData(File dbFile, String data) {
		FileWriter fos = null;
		try {
			fos = new FileWriter(dbFile, true);
			BufferedWriter bw = new BufferedWriter(fos);			
			Message.message(
					this,
					data + "was written Successfully--"
							+ dbFile.getAbsolutePath());
			bw.write(data);
			bw.newLine();
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	private Runnable scanTimer = new Runnable() {
		@Override
		public void run() {
			if (run) {
				if (count != 20) {
					count++;
					counterText.setText(String.valueOf(count));
					int dBm = Math.abs(onReceive());
					Log.d("DBM", String.valueOf(dBm));
					RSS = RSS + dBm;
					Log.d("RSS :", String.valueOf(RSS));
				} else {
					AvgRSS = RSS / 20;
					Log.d("Average", String.valueOf(AvgRSS));
					addValues(AvgRSS);
					run = false;
					count = 0;
					RSS = 0;
				}
				scanHandler.postDelayed(scanTimer, 1000);
			}
		}
	};
	// Here signalLevel is in dBm and frequency is in MHz respectively
	public double calculateDistance(double level, double frq) {
		double exp = (27.55 - (20 * Math.log10(frq)) + Math.abs(level)) / 20.0;
		return Math.pow(10.0, exp);
	}
}
