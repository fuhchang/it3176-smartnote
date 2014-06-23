package com.example.it3176_smartnote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.SQLiteController.it3176.SQLiteController;
import com.example.it3176_smartnote.model.Note;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SearchView;

@SuppressLint({ "ValidFragment", "SimpleDateFormat" })
public class ArchiveActivity extends Activity {
	int count, selected;
	ListView list;
	String[] cateArray;
	DatePicker dpInputDate;
	Integer[] imageId = { R.drawable.client, R.drawable.meeting,
			R.drawable.personnel };
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
        ArrayList<Note> temptArray = controller.retrieveNotes();
        controller.close();        
        for(int i = 0; i < temptArray.size(); i++){
        	if(temptArray.get(i).getNote_status().equals("archive")){
        		resultArray.add(temptArray.get(i));
        	}
        }
		Collections.sort(resultArray, new DateDesComparator());
		noteList notelist = new noteList(ArchiveActivity.this, resultArray, imageId);
		list = (ListView) findViewById(R.id.noteListView);
		list.setAdapter(notelist);
        
        list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent(getApplicationContext(), ArchiveDetail.class);
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
        
		ActionBar actionBar	= getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.archive_activity, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search_icon)
				.getActionView();
		if (searchView != null) {
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
		}

		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String arg0) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				ArrayList<Note> searchResult = new ArrayList<Note>();
				for(int i=0; i<resultArray.size(); i++){
					if(resultArray.get(i).getNote_name().equals(query)){
						searchResult.add(resultArray.get(i));
					}
				}
				noteList notelist = new noteList(ArchiveActivity.this, searchResult, imageId);
				list = (ListView) findViewById(R.id.noteListView);
		        list.setAdapter(notelist);
		        
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {

		case R.id.action_settings:
			final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			String selected_setting = sp.getString("selected_setting", "YourSetting");
			
			//If user's had chose a preference before
			if(selected_setting.equals("archive")){
				selected = 0;
			}
			else if(selected_setting.equals("delete")){
				selected = 1;
			}
			else if(selected_setting.equals("none")){
				selected = 2;
			}
			
			final CharSequence[] preferences = {"Archive", "Delete", "None"};
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Preference").setSingleChoiceItems(preferences, selected, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(preferences[which].equals("Archive")){
						selected = 0;
					}
					else if(preferences[which].equals("Delete")){
						selected = 1;						
					}
					else if(preferences[which].equals("None")){
						selected = 2;
					}
				}
			});
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(preferences[selected].equals("Archive")){
						savePreferences("selected_setting", "archive");
					}
					else if(preferences[selected].equals("Delete")){
						savePreferences("selected_setting", "delete");
					}
					else if(preferences[selected].equals("None")){
						savePreferences("selected_setting", "none");
					}
				}
			});
			AlertDialog prefDialog = builder.create();
			prefDialog.show();
			break;
		case R.id.search_type:
			MyCategoryDialog dialog = new MyCategoryDialog();
			dialog.show(getFragmentManager(), "myCategoryDialog");
			break;
		case R.id.search_date:
			MyDatePicker datepicker = new MyDatePicker();
			datepicker.show(getFragmentManager(), "myDatePicker");
			break;
		case R.id.sort_title:
			sortByTitle sortTitle = new sortByTitle();
			sortTitle.show(getFragmentManager(), "sortByTitle");
			break;
		case R.id.sort_date:
			sortByDate sortDate = new sortByDate();
			sortDate.show(getFragmentManager(), "sortByDate");
			break;
		case R.id.sort_type:
			sortByType sortType = new sortByType();
			sortType.show(getFragmentManager(), "sortByType");
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	private class MyDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		int pYear;
		int pDay;
		int pMonth;
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			pYear = year;
			pDay = dayOfMonth;
			pMonth = monthOfYear + 1;
			
			String monthString = null;

			switch (pMonth) {
			case 1:
				monthString = "Jan";
				break;
			case 2:
				monthString = "Feb";
				break;
			case 3:
				monthString = "Mar";
				break;
			case 4:
				monthString = "Apr";
				break;
			case 5:
				monthString = "May";
				break;
			case 6:
				monthString = "Jun";
				break;
			case 7:
				monthString = "Jul";
				break;
			case 8:
				monthString = "Aug";
				break;
			case 9:
				monthString = "Sep";
				break;
			case 10:
				monthString = "Oct";
				break;
			case 11:
				monthString = "Nov";
				break;
			case 12:
				monthString = "Dec";
				break;
			}
			DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			tempArray.clear();
			if (pYear > 0 || pDay > 0 || pMonth > 0) {
				if (!resultArray.isEmpty()) {
					for (int i = 0; i < resultArray.size(); i++) {
						DateTime noteDate = formatter.parseDateTime(resultArray.get(i).getNote_date());
						String selectedDate = pDay + "-" + monthString + "-" + pYear;
						String date1 = dateFormat.format(noteDate.toDate());
						
						if (date1.equals(selectedDate)) {
							tempArray.add(resultArray.get(i));
						} 
					}
				} 
				if(!tempArray.isEmpty()){
				noteList notelist = new noteList(ArchiveActivity.this, tempArray, imageId);
				list = (ListView) findViewById(R.id.noteListView);
				list.setAdapter(notelist);
				}else{
					Note note = new Note();
					note.setNote_name("NO result Found please check your input. Thank you");
					tempArray.add(note);
					noteList notelist = new noteList(ArchiveActivity.this, tempArray, imageId);
					list = (ListView) findViewById(R.id.noteListView);
					list.setAdapter(notelist);
				}
			}
		}
	}

	private class MyCategoryDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Select The Type");
			builder.setItems(R.array.category_choice,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							tempArray.clear();
							// TODO Auto-generated method stub
							String selected = cateArray[which];
							Log.d("Selected", selected);
							for (int i = 0; i < resultArray.size(); i++) {
								if (resultArray.get(i).getNote_category()
										.equals(selected)) {
									tempArray.add(resultArray.get(i));
								} 

							}

							if (!tempArray.isEmpty()) {
								noteList notelist = new noteList(getActivity(),
										tempArray, imageId);
								list = (ListView) getActivity().findViewById(
										R.id.noteListView);
								list.deferNotifyDataSetChanged();
								list.setAdapter(notelist);
							}else{
								Note note = new Note();
								note.setNote_name("NO result Found please check your input. Thank you");
								tempArray.add(note);
								noteList notelist = new noteList(ArchiveActivity.this,
										tempArray, imageId);
								list = (ListView) findViewById(R.id.noteListView);
								list.setAdapter(notelist);
							}
						}

					});
			return builder.create();
		}
		
	}
	
	private class sortByTitle extends DialogFragment{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Sort By");
			builder.setItems(R.array.sort_choice, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					if(which == 0){
						Collections.sort(resultArray, new TitleAscComparator());
						noteList notelist = new noteList(getActivity(),
								resultArray, imageId);
						list = (ListView) getActivity().findViewById(
								R.id.noteListView);
						list.deferNotifyDataSetChanged();
						list.setAdapter(notelist);
						
					}else{
						Collections.sort(resultArray, new TitleDesComparator());
						noteList notelist = new noteList(getActivity(),
								resultArray, imageId);
						list = (ListView) getActivity().findViewById(
								R.id.noteListView);
						list.deferNotifyDataSetChanged();
						list.setAdapter(notelist);
					}
				}
				
			});
			return builder.create();
		}
	}
	
	private class sortByDate extends DialogFragment{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Sort By");
			builder.setItems(R.array.sort_choice, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					if(arg1 == 0){
						Collections.sort(resultArray, new DateAscComparator());
						noteList notelist = new noteList(getActivity(),
								resultArray, imageId);
						list = (ListView) getActivity().findViewById(
								R.id.noteListView);
						list.deferNotifyDataSetChanged();
						list.setAdapter(notelist);
					}else{
						Collections.sort(resultArray, new DateDesComparator());
						noteList notelist = new noteList(getActivity(),
								resultArray, imageId);
						list = (ListView) getActivity().findViewById(
								R.id.noteListView);
						list.deferNotifyDataSetChanged();
						list.setAdapter(notelist);
					}
				}
				
			});
			return builder.create();
		}
		
	}

	private class sortByType extends DialogFragment{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Sort By");
			builder.setItems(R.array.sort_choice, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					if(arg1 == 0){
						Collections.sort(resultArray, new CategoryAscComparator());
						noteList notelist = new noteList(getActivity(),
								resultArray, imageId);
						list = (ListView) getActivity().findViewById(
								R.id.noteListView);
						list.deferNotifyDataSetChanged();
						list.setAdapter(notelist);
					}else{
						Collections.sort(resultArray, new CategoryDesComparator());
						noteList notelist = new noteList(getActivity(),
								resultArray, imageId);
						list = (ListView) getActivity().findViewById(
								R.id.noteListView);
						list.deferNotifyDataSetChanged();
						list.setAdapter(notelist);
					}
				}
			
			});
			return builder.create();
		}
		
	}

	@Override
	public void onBackPressed() {
		ArchiveActivity.this.finish();
		Intent refresh = new Intent(ArchiveActivity.this, MainActivity.class);
		refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(refresh);
		
		super.onBackPressed();
	}
	
	private void savePreferences(String key, String value){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}
	
	private class TitleAscComparator implements Comparator<Note>{

		@Override
		public int compare(Note arg0, Note arg1) {
			return arg0.getNote_name().compareTo(arg1.getNote_name());
		}		
	}
	
	private class TitleDesComparator implements Comparator<Note>{

		@Override
		public int compare(Note lhs, Note rhs) {
			return rhs.getNote_name().compareTo(lhs.getNote_name());
		}		
	}
	
	private class DateAscComparator implements Comparator<Note>{
		
		@Override
		public int compare(Note lhs, Note rhs) {
			return lhs.getNote_date().compareTo(rhs.getNote_date());
		}		
	}
	
	private class DateDesComparator implements Comparator<Note>{
		
		@Override
		public int compare(Note lhs, Note rhs) {
			return rhs.getNote_date().compareTo(lhs.getNote_date());
		}		
	}
	
	private class CategoryAscComparator implements Comparator<Note>{

		@Override
		public int compare(Note arg0, Note arg1) {			
			return arg0.getNote_category().compareTo(arg1.getNote_category());
		}		
	}
	
	private class CategoryDesComparator implements Comparator<Note>{
		
		@Override
		public int compare(Note lhs, Note rhs) {
			return rhs.getNote_category().compareTo(lhs.getNote_category());
		}		
	}
}
