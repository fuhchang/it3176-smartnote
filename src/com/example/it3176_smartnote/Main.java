package com.example.it3176_smartnote;

import java.util.ArrayList;

import com.SQLiteController.it3176.SQLiteController;
import com.example.it3176_smartnote.model.Note;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class Main extends Activity{
	ArrayList<Note> note_list = new ArrayList<Note>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_list);
		
		SQLiteController getNoteList = new SQLiteController(this);
		getNoteList.open();
		note_list.addAll(getNoteList.retrieveNotes());
		getNoteList.close();
	}

	@Override
	public View onCreateView(View parent, String name, Context context,
			AttributeSet attrs) {
		return super.onCreateView(parent, name, context, attrs);
	}
	
}
