package com.example.it3176_smartnote;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.SQLiteController.it3176.SQLiteController;
import com.example.it3176_smartnote.model.Note;

public class ArchiveDetail extends Activity {
	CheckBox cb_remember_setting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_detail);
		ArrayList<String> list = getIntent().getStringArrayListExtra("resultArray");
		TextView etTitle = (TextView ) findViewById(R.id.noteTitleET);
		etTitle.setText(list.get(0).toString());
		TextView etContent = (TextView ) findViewById(R.id.contentTxt);
		etContent.setText(list.get(1).toString());
		TextView tvCate = (TextView) findViewById(R.id.cateChoice);
		tvCate.setText(list.get(2).toString());
		TextView timeTV = (TextView) findViewById(R.id.txtTime);
		timeTV.setText(list.get(3).toString());
		
		ActionBar actionBar	= getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.archive_detail, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.btn_reactivate) {
			AlertDialog.Builder builder = new AlertDialog.Builder(ArchiveDetail.this);
			builder.setTitle("Restore").setMessage("This note will be restored.");
			
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Note note = new Note(Integer.parseInt(getIntent().getStringExtra("note_id")));
					SQLiteController controller = new SQLiteController(ArchiveDetail.this);
					try{
						controller.open();
						controller.reactivateNote(note);
					} catch(SQLException e){
						System.out.println(e);
					} finally{
						controller.close();
						Intent refresh = new Intent(ArchiveDetail.this, ArchiveActivity.class);
						startActivity(refresh);
					}
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		
		return super.onOptionsItemSelected(item);
	}
}
