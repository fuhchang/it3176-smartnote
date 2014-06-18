package com.example.it3176_smartnote;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.app.TabActivity;
import android.app.AlertDialog.Builder;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import com.SQLiteController.it3176.SQLiteController;
import com.example.it3176_smartnote.model.Note;

@SuppressLint("ValidFragment")
public class MainActivity extends Activity {
	int count;
	ListView list;
	String[] cateArray;
	DatePicker dpInputDate;
	
	ArrayList<Note> resultArray = new ArrayList<Note>();
	ArrayList<Note> tempArray = new ArrayList<Note>();
	ArrayList<Note> searchResult = new ArrayList<Note>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cateArray = getResources().getStringArray(R.array.category_choice);
        
        SQLiteController controller = new SQLiteController(this);
        controller.open();
        //resultArray.addAll(controller.retrieveNotes());
        ArrayList<Note> temptArray = controller.retrieveNotes();
        temptArray = controller.autoUpdateNoteStatus(temptArray);
        controller.close();
        for(int i = 0; i < temptArray.size(); i++){
        	if(temptArray.get(i).getNote_status().equals("active")){
        		resultArray.add(temptArray.get(i));
        	}
        }
        
        
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
				intent.putExtra("note_id", Integer.toString(resultArray.get(position).getNote_id()));
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
          if(searchView != null){
          searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
          }
         
          
          searchView.setOnQueryTextListener(new OnQueryTextListener(){

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				//Toast.makeText(getBaseContext(), newText, Toast.LENGTH_LONG).show();
				return false;
			}
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				ArrayList<Note> searchResult = new ArrayList<Note>();
				for(int i=0; i<resultArray.size(); i++){
					if(resultArray.get(i).getNote_name().equals(query)){
						searchResult.add(resultArray.get(i));
					}
				}
				noteList notelist = new noteList(MainActivity.this, searchResult);
				list = (ListView) findViewById(R.id.noteListView);
		        list.setAdapter(notelist);
		        
				return false;
			}
        	  
        	  
          });
          
    	return super.onCreateOptionsMenu(menu);
          
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		
		switch(item.getItemId()){
		
		case R.id.new_icon:
			Intent intent = new Intent(this, CreateActivity.class);
			startActivity(intent);
			this.finish();
			break;
		case R.id.action_archive:
			Intent archive_intent = new Intent(this, ArchiveActivity.class);
			startActivity(archive_intent);
			this.finish();
			break;
		case R.id.action_settings:
			
			
			break;
		case R.id.search_type:
			MyCategoryDialog dialog = new MyCategoryDialog();
			dialog.show(getFragmentManager(), "myCategoryDialog");
			break;
		case R.id.search_date:
			MyDatePicker datepicker = new MyDatePicker();
			datepicker.show(getFragmentManager(), "myDatePicker");
			
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	private class MyDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{
		int pYear, pDay, pMonth;
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			pYear = year;
			pDay = dayOfMonth;
			pMonth = monthOfYear;
		}
		
		
	}
	
	
	private class MyCategoryDialog extends DialogFragment{
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Select The Type");
			builder.setItems(R.array.category_choice, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					tempArray.clear();
					// TODO Auto-generated method stub
					String selected = cateArray[which];
					Log.d("Selected", selected);
					for(int i=0; i< resultArray.size(); i++){
						if(resultArray.get(i).getNote_category().equals(selected)){
							tempArray.add(resultArray.get(i));
						}else{
							Log.d("result", "not found");
						}
						
					}
					
					if(!tempArray.isEmpty()){
					noteList notelist = new noteList(getActivity(), tempArray);
					list = (ListView) getActivity().findViewById(R.id.noteListView);
					list.deferNotifyDataSetChanged();
					list.setAdapter(notelist);
					}
				}
				
			});
			return builder.create();
			
		}
	}

	
	

	
    
}
