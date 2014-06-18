package com.example.it3176_smartnote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.SQLLite.it3176.mySQLLite;
import com.example.it3176_smartnote.model.Note;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class CreateActivity extends Activity {
	
	private static Bitmap Image = null;
	private static Bitmap rotateImage = null;
	private ImageView imageView;
	private static final int GALLERY = 1;
	
	static int SELECTION_CHOICE_DIALOG=1;
	static String[] selectionArray;
	static String noteCategory="";
	//static ArrayList<Integer> categorySelected = new ArrayList<Integer>();
	
	TextView dateTimeCreation, categorySelection;
	static TextView categorySelectionChoice;
	EditText noteTitle, noteContent;
	Button btnSave;
	static String category="";

	final Context context = this;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		
		//Title, content, saveButton, imageView
		noteTitle=(EditText)findViewById(R.id.noteTitle);
		noteContent=(EditText)findViewById(R.id.noteContent);
		btnSave=(Button)findViewById(R.id.btnSave);
		imageView = (ImageView) findViewById(R.id.imageView1);
		getActionBar().setTitle("New Note");
		
		//Get time of pressing "New Note"
		dateTimeCreation = (TextView) findViewById(R.id.currentDateTimeOfCreation);		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMMM yyyy - hh:mm a");
		String strDate = sdf.format(c.getTime());
		dateTimeCreation.setText(strDate);
		
		//For selection
		selectionArray=getResources().getStringArray(R.array.category_choice);
		categorySelection = (TextView) findViewById(R.id.categorySelection);
		categorySelectionChoice = (TextView) findViewById(R.id.categorySelectionChoice);
		categorySelection.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CreateNoteDialog dialog = new CreateNoteDialog();
				dialog.setDialogType(SELECTION_CHOICE_DIALOG);
				dialog.show(getFragmentManager(), "CreateNoteDialog");
			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create, menu);
		return true;
	}

	
	
	/**Menu items**/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		int id = item.getItemId();
		
		if(id==R.id.backToMain){
			
			 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		      alertDialogBuilder.setMessage("Discard Note?");
		      alertDialogBuilder.setPositiveButton("Yes", 
		      new DialogInterface.OnClickListener() {
				
		         @Override
		         public void onClick(DialogInterface arg0, int arg1) {
		            /*Intent positveActivity = new Intent(getApplicationContext(),com.example.it3176_smartnote.MainActivity.class);
		            startActivity(positveActivity);
					*/
		        	 Toast.makeText(getApplicationContext(), "Note discarded.", Toast.LENGTH_LONG).show();
		        	 CreateActivity.this.finish();
		         }
		      });
		      alertDialogBuilder.setNegativeButton("No", 
		      new DialogInterface.OnClickListener() {
					
		         @Override
		         public void onClick(DialogInterface dialog, int which) {
		            
				 }
		      });
			    
		      AlertDialog alertDialog = alertDialogBuilder.create();
		      alertDialog.show();
		}
		else{
			if(id==R.id.reset){
				noteTitle.getText().clear();
				noteContent.getText().clear();
				imageView.setImageResource(android.R.color.transparent);
				noteCategory="";
				categorySelectionChoice.setText("Category: None Selected");
			}
			else{
				if(id==R.id.uploadImage){
					Intent intent = new Intent();
					intent.setType("image/*");
					//intent.setType("*/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
				}
			}
		}
		
		
		return super.onOptionsItemSelected(item);
	}

	/**Insert note into database**/
	public void onClick(View view){
		//Toast.makeText(getApplicationContext(), "Nooooo.", Toast.LENGTH_LONG).show();
		//Toast.makeText(getBaseContext(), v.getId(), Toast.LENGTH_LONG).show();
		
		String title = noteTitle.getText().toString();
		String content = noteContent.getText().toString();
		
		ArrayList<Note> resultArray = new ArrayList<Note>();
		mySQLLite getEntry = new mySQLLite(this);
        getEntry.open();
        resultArray.addAll(getEntry.selectEntry());
        getEntry.close();
        
       String duplicateTitle="";
		
        for(int i=0; i<resultArray.size(); i++){
        	if(resultArray.get(i).getNote_name().equals(noteTitle.getText().toString())){
        		duplicateTitle="yes";
        	}
        }
		
		
		if(title.matches("")||content.matches("")||noteCategory.matches("")){
			Toast.makeText(getApplicationContext(), "Please fill in all the required details", Toast.LENGTH_LONG).show();
		}
		else
			if(duplicateTitle.matches("yes")){
					Toast.makeText(getApplicationContext(), "Duplicate title found, unable to save note", Toast.LENGTH_LONG).show();
			}
			else{
				
					boolean result = true;
					try{
					
				
					mySQLLite entry = new mySQLLite(this);
					entry.open();
					entry.createEntry(title, content, noteCategory);
					entry.close();
					}catch(Exception e){
						result = false;
					}finally{
						if(result){
							Toast.makeText(getApplicationContext(), "Note Saved", Toast.LENGTH_LONG).show();
							CreateActivity.this.finish();
							Intent intent = new Intent(this, MainActivity.class);
							startActivity(intent);
							
						}
					}
				}	
				
	}

	/**Set the selected image from gallery and display in image view**/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == GALLERY && resultCode != 0) {
			Uri mImageUri = data.getData();
			try {
				Image = Media.getBitmap(this.getContentResolver(), mImageUri);
				imageView.setImageBitmap(Image);			
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
			} 
			catch (IOException e) {			
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**Creates a dialog with title and options from array**/
	public static class CreateNoteDialog extends DialogFragment{
		int dialogType;
		
		public void setDialogType(int type){
			dialogType=type;
		}
		
		
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			
			if(dialogType==SELECTION_CHOICE_DIALOG)
			{
				
				
				builder.setTitle("Select Category");
				builder.setItems(R.array.category_choice, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						noteCategory=selectionArray[which];
						updateChoice();
					}
				});
				/*builder.setTitle("Select Category(ies)");
				boolean[] checkedValues= new boolean[selectionArray.length];
				for(int i=0;i<categorySelected.size();i++){
					checkedValues[categorySelected.get(i)]=true;
				}
				builder.setMultiChoiceItems(R.array.category_choice, checkedValues, new DialogInterface.OnMultiChoiceClickListener(){
					public void onClick(DialogInterface dialog, int which, boolean isChecked)
					{
						if(isChecked){
							categorySelected.add(which);
							
						}
						else if(categorySelected.contains(which)){
							categorySelected.remove(Integer.valueOf(which));
						}
					}
					
				});
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						updateChoice();
					}
				});*/
				
			}
			return builder.create();
		}
	}
	
	
	/**Set textview to display selected category**/
	public static void updateChoice(){
		category="";
		
		if(noteCategory.length()>0){
			category="Category: " + noteCategory;
		}
		
		/*if(categorySelected.size()>0){
			category="Category: ";
			for(int i=0;i<categorySelected.size();i++){
				category+= "(" + selectionArray[categorySelected.get(i)] + ") ";
			
			Log.i("Here", selectionArray[categorySelected.get(i)]);
			}
		}
		else{
			category="Category: None Selected";
		}
	//	category += ".";*/
		categorySelectionChoice.setText(category);
	}

	
	
	/**For back pressed event**/
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	      alertDialogBuilder.setMessage("Discard Note?");
	      alertDialogBuilder.setPositiveButton("Yes", 
	      new DialogInterface.OnClickListener() {
			
	         @Override
	         public void onClick(DialogInterface arg0, int arg1) {
	        	 Toast.makeText(getApplicationContext(), "Note discarded.", Toast.LENGTH_LONG).show();
	        	 CreateActivity.this.finish();
	         }
	      });
	      alertDialogBuilder.setNegativeButton("No", 
	      new DialogInterface.OnClickListener() {
				
	         @Override
	         public void onClick(DialogInterface dialog, int which) {
	            
			 }
	      });
		    
	      AlertDialog alertDialog = alertDialogBuilder.create();
	      alertDialog.show();
	}
	
	
	
	
}	
	
	

