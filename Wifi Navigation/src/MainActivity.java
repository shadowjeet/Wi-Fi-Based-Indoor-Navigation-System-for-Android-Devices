package com.example.wifinavigation_find;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        Intent findPosition = new Intent(
				"com.example.wifinavigation_find.FINDPOSITION");
		startActivity(findPosition);        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_find_position, menu);
        return true;
    }    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}    
}
