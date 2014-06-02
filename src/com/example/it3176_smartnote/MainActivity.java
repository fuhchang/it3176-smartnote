package com.example.it3176_smartnote;

import android.R.menu;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
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
        getMenuInflater().inflate(R.menu.main_activity_action, menu);
        
    	  SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
          SearchView searchView  = (SearchView) menu.findItem(R.id.search_icon).getActionView();
          searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
          
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
			Intent intent = new Intent(this, sqlTest.class);
			startActivity(intent);
			return true;
		case R.id.action_Delete:
			Toast.makeText(getBaseContext(), "DELETING", Toast.LENGTH_LONG).show();
			return true;
		case R.id.action_settings:
			Toast.makeText(getBaseContext(), "Setting!", Toast.LENGTH_LONG).show();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
    
}
