package com.example.it3176_smartnote;

import java.util.ArrayList;

import com.SQLLite.it3176.mySQLLite;
import com.example.it3176_smartnote.model.Note;
import com.example.it3176_smartnote_model.note;

import android.R.menu;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends Activity {
	int count;
	ListView list;
	
	ArrayList<Note> resultArray = new ArrayList<Note>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mySQLLite getEntry = new mySQLLite(this);
        getEntry.open();
        resultArray.addAll(getEntry.selectEntry());
        getEntry.close();
        
        noteList notelist = new noteList(MainActivity.this, resultArray);
        list = (ListView) findViewById(R.id.noteListView);
        list.setAdapter(notelist);
        
        list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), NoteDetail.class);
				ArrayList<String> selectedNote = new ArrayList<String>();
				selectedNote.add(resultArray.get(position).getNote_name());
				selectedNote.add(resultArray.get(position).getNote_content());
				selectedNote.add(resultArray.get(position).getNote_category());
				selectedNote.add(resultArray.get(position).getNote_date());
				intent.putStringArrayListExtra("resultArray", selectedNote);
		        startActivity(intent);
				
			}
        	
        });
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
			Intent intent = new Intent(this, CreateActivity.class);
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
