package com.example.it3176_smartnote;

import java.util.ArrayList;


import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
