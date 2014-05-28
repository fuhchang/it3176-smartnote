package com.example.it3176_smartnote;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        //return true;
    	MenuInflater mif = getMenuInflater();
    	mif.inflate(R.menu.main_activity_action, menu);
    	
    	return super.onCreateOptionsMenu(menu);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		
		switch(item.getItemId()){
		case R.id.search_icon :
			Toast.makeText(getBaseContext(), "Click on the Search icon", Toast.LENGTH_LONG).show();
			return true;
		case R.id.new_icon:
			Toast.makeText(getBaseContext(), "Click on the add icon", Toast.LENGTH_LONG).show();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
    
}
