package com.example.it3176_smartnote;

import java.util.ArrayList;

import com.SQLiteController.it3176.SQLiteController;
import com.example.it3176_smartnote.model.Note;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ArchiveActivity extends Activity {
	ListView list;
	
	ArrayList<Note> resultArray = new ArrayList<Note>();
	ArrayList<Note> tempArray = new ArrayList<Note>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        SQLiteController controller = new SQLiteController(this);
        controller.open();
        
        ArrayList<Note> temptArray = controller.retrieveNotes();
        controller.close();
        
        for(int i = 0; i < temptArray.size(); i++){
        	if(temptArray.get(i).getNote_status().equals("archive")){
        		resultArray.add(temptArray.get(i));
        	}
        }
        
        noteList notelist = new noteList(ArchiveActivity.this, resultArray);
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
	public void onBackPressed() {
		ArchiveActivity.this.finish();
		Intent refresh = new Intent(ArchiveActivity.this, MainActivity.class);
		refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(refresh);
		
		super.onBackPressed();
	}
}
