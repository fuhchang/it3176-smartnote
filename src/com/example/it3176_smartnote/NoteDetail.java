package com.example.it3176_smartnote;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.SQLiteController.it3176.SQLiteController;
import com.example.it3176_smartnote.model.Note;

public class NoteDetail extends Activity {
	CheckBox cb_remember_setting;
	private Bitmap Image = null;
	InputStream is = null;
	BufferedInputStream bis = null;
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
		ImageView imgView = (ImageView) findViewById(R.id.imgView);
		
		/*
		try {
			ImageView imgView = (ImageView) findViewById(R.id.imgView);
			Uri myUri = Uri.parse(list.get(4));
			Image = Media.getBitmap(this.getContentResolver(), myUri);
			imgView.setImageBitmap(Image);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		ActionBar actionBar	= getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_detail, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.btn_remove) {
			
			final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			String selected_setting = sp.getString("selected_setting", "none");
			
			//User's preference is archiving note
			if(selected_setting.equals("archive")){
				//Prompt for archive confirmation
				AlertDialog.Builder archiveBuilder = new AlertDialog.Builder(NoteDetail.this);
				archiveBuilder.setTitle("Archive").setMessage("This note will be archived.");
				
				archiveBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						archiveNote();
					}
				});
				archiveBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});
				AlertDialog deleteDialog = archiveBuilder.create();
				deleteDialog.show();
			}
			
			//User's preference is deleting note
			else if(selected_setting.equals("delete")){
				//Prompt for delete confirmation
				AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(NoteDetail.this);
				deleteBuilder.setTitle("Delete").setMessage("This note will be deleted.");
				
				deleteBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteNote();
					}
				});
				deleteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});
				AlertDialog deleteDialog = deleteBuilder.create();
				deleteDialog.show();
			}
			//User did not set preference
			else{
				LayoutInflater inflater = LayoutInflater.from(this);
				final View setting_view = inflater.inflate(R.layout.setting_dialog, null);
				cb_remember_setting = (CheckBox) setting_view.findViewById(R.id.cb_remember_setting);
				savePreferences("preference", false);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(setting_view);
				builder.setTitle("Select an action");
				
				//Checking whether user set preference
				cb_remember_setting.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						if(cb_remember_setting.isChecked()){
							savePreferences("preference", true);
						}
						else{
							savePreferences("preference", false);
						}
					}					
				});
				
				//Select action "Archive"
				builder.setNegativeButton("Archive", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						//Prompt for archive confirmation
						AlertDialog.Builder archiveBuilder = new AlertDialog.Builder(NoteDetail.this);
						archiveBuilder.setTitle("Archive").setMessage("This note will be archived.");

						archiveBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(sp.getBoolean("preference", true)){
									savePreferences("selected_setting", "archive");
								}
								archiveNote();
							}
						});
						archiveBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {}
						});
						AlertDialog deleteDialog = archiveBuilder.create();
						deleteDialog.show();
					}
				});
				
				//Select action "Delete"
				builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						//Prompt for delete confirmation
						AlertDialog.Builder archiveBuilder = new AlertDialog.Builder(NoteDetail.this);
						archiveBuilder.setTitle("Delete").setMessage("This note will be deleted.");

						archiveBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(sp.getBoolean("preference", true)){
									savePreferences("selected_setting", "delete");
								}
								deleteNote();
							}
						});
						archiveBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {}
						});
						AlertDialog deleteDialog = archiveBuilder.create();
						deleteDialog.show();
					}
				});
				
				AlertDialog dialog = builder.create();
				dialog.show();
			}

		}
		return super.onOptionsItemSelected(item);
	}
	
	private void archiveNote(){
		Note note = new Note(Integer.parseInt(getIntent().getStringExtra("note_id")));
		SQLiteController controller = new SQLiteController(NoteDetail.this);
		try{
			controller.open();
			controller.updateNoteStatus(note, "archive");
		} catch(SQLException e){
			System.out.println(e);
		} finally{
			controller.close();
			Intent refresh = new Intent(NoteDetail.this, MainActivity.class);
			startActivity(refresh);
			Toast.makeText(getBaseContext(), "Note archived", Toast.LENGTH_LONG).show();
		}
	}
	
	private void deleteNote(){
		Note note = new Note(Integer.parseInt(getIntent().getStringExtra("note_id")));
		SQLiteController controller = new SQLiteController(NoteDetail.this);
		try{
			controller.open();
			controller.deleteNote(note);
		} catch(SQLException e){
			System.out.println(e);
		} finally{
			controller.close();
			Intent refresh = new Intent(NoteDetail.this, MainActivity.class);
			startActivity(refresh);
			Toast.makeText(getBaseContext(), "Note deleted", Toast.LENGTH_LONG).show();
		}
	}
	
	private void savePreferences(String key, boolean value){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}
	
	private void savePreferences(String key, String value){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}
}
