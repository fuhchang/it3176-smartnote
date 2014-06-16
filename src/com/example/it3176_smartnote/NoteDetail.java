package com.example.it3176_smartnote;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.SQLiteController.it3176.SQLiteController;
import com.example.it3176_smartnote.model.Note;

public class NoteDetail extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_detail);
		ArrayList<String> list = getIntent().getStringArrayListExtra("resultArray");
		EditText etTitle = (EditText) findViewById(R.id.noteTitleET);
		etTitle.setText(list.get(0).toString());
		EditText etContent = (EditText) findViewById(R.id.contentTxt);
		etContent.setText(list.get(1).toString());
		TextView tvCate = (TextView) findViewById(R.id.cateChoice);
		tvCate.setText(list.get(2).toString());
		TextView timeTV = (TextView) findViewById(R.id.txtTime);
		timeTV.setText(list.get(3).toString());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_detail, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Note note = new Note(Integer.parseInt(getIntent().getStringExtra("note_id")));
			SQLiteController controller = new SQLiteController(this);
			try{
				controller.open();
				controller.deleteNote(note);
			} catch(SQLException e){
				return false;
			} finally{
				controller.close();
				Intent refresh = new Intent(this, MainActivity.class);
				startActivity(refresh);
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
