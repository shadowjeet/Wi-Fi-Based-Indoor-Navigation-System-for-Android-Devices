package com.example.average;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.example.average.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
public class ReadFile extends Activity {
	TextView showText;
	Button readBut;
	File folder, dbFile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_file);
		showText = (TextView) findViewById(R.id.showText);
		readBut = (Button) findViewById(R.id.readBut);

		folder = getExternalFilesDir("FingerPrints");
		readBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				readFromFile();
			}
		});
	}
	public void readFromFile() {
		if (dbFile.exists()) {
			StringBuilder text = new StringBuilder();
			try {
				BufferedReader br = new BufferedReader(new FileReader(dbFile));
				String line;
				while ((line = br.readLine()) != null) {
					// display line starting with particular string at starting
					// if (line.startsWith("-38")){
					// display line starting with particular string at end
					// if(line.endsWith("4")){
					// text.append(line);
					// text.append('\n');
					// int length = line.length();
					// Log.d("Length", String.valueOf(length));
					// }
					text.append(line);
					text.append('\n');
				}
			} catch (IOException e) {

			}
			// set the edittext with the text of file
			showText.setText(text);
		} else {
			showText.setText("Sorry file does not exists");
		}
	}

	public void chooseFile(View v) {
		boolean checked = ((RadioButton) v).isChecked();
		switch (v.getId()) {
		case R.id.AP1:
			if (checked) {
				dbFile = new File(folder, "AP1.txt");
				Toast.makeText(getApplicationContext(), "AP1 checked",
						Toast.LENGTH_SHORT).show();			}
			break;
		case R.id.AP2:
			if (checked) {
				dbFile = new File(folder, "AP2.txt");
				Toast.makeText(getApplicationContext(), "AP2 checked",
						Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.AP3:
			if (checked) {
				dbFile = new File(folder, "AP3.txt");
				Toast.makeText(getApplicationContext(), "AP3 checked",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
} 
