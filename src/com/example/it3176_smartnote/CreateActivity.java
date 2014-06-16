package com.example.it3176_smartnote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import java.io.FileNotFoundException;
import java.io.IOException;

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
	static ArrayList<Integer> categorySelected = new ArrayList<Integer>();
	
	TextView dateTimeCreation, categorySelection;
	static TextView categorySelectionChoice;
	EditText noteTitle, noteContent;
	Button btnSave;
	static String category="";

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
		SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMMM yyyy - KK:mm a");
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
				categorySelected.clear();
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

	public void onClick(View view){
		Toast.makeText(getApplicationContext(), "Nooooo.", Toast.LENGTH_LONG).show();
		/*Intent intent;
		try {
			switch (view.getId()) {
			case R.id.btnSave:
				intent = new Intent(Temp_main.this, Volunteer_Register.class);
				startActivity(intent);
				this.finish();
				break;
			case R.id.btnView:
				intent = new Intent(Temp_main.this, Volunteer_View.class);
				startActivity(intent);
				this.finish();
			case R.id.btnEdit:
				intent = new Intent(Temp_main.this, Volunteer_Edit.class);
				startActivity(intent);
				this.finish();
			default:
				Temp_main.this.finish();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/

	}
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
				builder.setTitle("Select Category(ies)");
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
				});
				
			}
			return builder.create();
		}
	}
	
	
	
	public static void updateChoice(){
		category="";
		
		if(categorySelected.size()>0){
			category="Category: ";
			for(int i=0;i<categorySelected.size();i++){
				category+= "(" + selectionArray[categorySelected.get(i)] + ") ";
			
			Log.i("Here", selectionArray[categorySelected.get(i)]);
			}
		}
		else{
			category="Category: None Selected";
		}
	//	category += ".";
		categorySelectionChoice.setText(category);
	}
}	
	
	

