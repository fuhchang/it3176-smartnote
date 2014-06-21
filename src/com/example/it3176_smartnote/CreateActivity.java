/**************CREATE NOTE DONE BY KEITH**********************/
package com.example.it3176_smartnote;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.SQLiteController.it3176.SQLiteController;
import com.example.it3176_smartnote.model.Note;

public class CreateActivity extends Activity {
	
	LinearLayout mLinearLayout;
	LinearLayout mLinearLayoutHeader;
	
	
	private static Bitmap Image = null;
	private ImageView imageView;
	private VideoView videoView;
	private VideoView audioView;
	
	int notifyID=1088;
	private static final int PICK_IMAGE = 1;
	private static final int PICK_VIDEO=2;
	private static final int PICK_AUDIO=3;
	
	static int SELECTION_CHOICE_DIALOG=1;
	
	static String[] selectionArray;
	
	TextView dateTimeCreation, categorySelection,
	attachment, hrTv, imageUriTv, videoUriTv, 
	audioUriTv, tapToAddTags, tags,
	addTv, currentLocation, attachments;
	static TextView categorySelectionChoice;
	EditText noteTitle, noteContent;
	AutoCompleteTextView suggestTitle;
	Button btnSave;
	
	MediaController videoMC;
	MediaController audioMC;
	
	static String category="";
	
	
	/**Values to be stored in database**/
	String titleOfNote="";
	String content="";
	String uriOfImage ="",uriOfVideo="", uriOfAudio="";
	static String noteCategory="";
	
	
	String calendarDuplicateTitle="";
	
	static String noteTags="";
	
	 Location location; 
	 Double MyLat, MyLong;
	 float accLoc;
	 String Address="";
	 String City="";
	 private boolean gps_enabled=false;
	 private boolean network_enabled=false;
	
	
	
	final Context context = this;
	
	private Cursor calendarEventTitleCursor;
	private Cursor onGoingEventCursor;
	String eventTitle;
	String eventMatchCriteria ="";
	long currentDateTime;
	Long eventStartDateTime;
	Long eventEndDateTime;
	String calendarOnGoingEvent="";
	String fullEventDetails="";
	ArrayList<String> eventTitles = new ArrayList<String>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_note);

		//noteTitle=(EditText)findViewById(R.id.noteTitle);
		noteContent=(EditText)findViewById(R.id.noteContent);
		btnSave=(Button)findViewById(R.id.btnSave);	
		//attachment=(TextView)findViewById(R.id.attachment);
		//attachment=(TextView)findViewById(R.id.att);
		//attachment.setVisibility(View.GONE);
		//hrTv=(TextView) findViewById(R.id.hrTv);
		
		
		imageView = (ImageView) findViewById(R.id.imageView);
		videoView = (VideoView) findViewById(R.id.videoView);
		imageUriTv = (TextView) findViewById(R.id.imageUriTv);
		videoUriTv = (TextView) findViewById(R.id.videoUriTv);
		audioView = (VideoView) findViewById(R.id.audioView);
		audioUriTv = (TextView) findViewById(R.id.audioUriTv);
		addTv=(TextView) findViewById(R.id.addTv);
		currentLocation = (TextView) findViewById(R.id.currentLocation);
		
		
		imageView.setVisibility(View.GONE);
		videoView.setVisibility(View.GONE);
		imageUriTv.setVisibility(View.GONE);
		videoUriTv.setVisibility(View.GONE);
		audioView.setVisibility(View.GONE);
		audioUriTv.setVisibility(View.GONE);
		
		addTv.setVisibility(View.GONE);
		currentLocation.setVisibility(View.GONE);

		videoMC = new MediaController(this);
        videoMC.setAnchorView(videoView);
        
        audioMC = new MediaController(this);
        audioMC.setAnchorView(audioView);
		
		getActionBar().setTitle("New Note");
		
		
		calendarEventTitleCursor=getContentResolver().query(CalendarContract.Events.CONTENT_URI, new String[]{CalendarContract.Events.TITLE},null,null,null);
		if (!(calendarEventTitleCursor.moveToFirst()) || calendarEventTitleCursor.getCount() ==0){
			//Toast.makeText(getApplicationContext(), "You dont have any events in calendar", Toast.LENGTH_LONG).show();
			Log.d("CALENDAR TITLE SUGGESTION", "You dont have any events in calendar for note title suggestion");
		}
		else{
			calendarEventTitleCursor.moveToFirst();
				do{
					
					//Toast.makeText(getApplicationContext(), calendarEventTitleCursor.getString(calendarEventTitleCursor.getColumnIndex(CalendarContract.Events.TITLE)), Toast.LENGTH_SHORT).show();
					eventTitles.add(calendarEventTitleCursor.getString(calendarEventTitleCursor.getColumnIndex(CalendarContract.Events.TITLE)));
				}while(calendarEventTitleCursor.moveToNext());
			
		}
					suggestTitle= (AutoCompleteTextView) findViewById(R.id.noteTitle);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, eventTitles);
					suggestTitle.setAdapter(adapter);
		
		
		
		
		//Get time of pressing "New Note"
		dateTimeCreation = (TextView) findViewById(R.id.currentDateTimeOfCreation);		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMMM yyyy - hh:mm a");
		String strDate = sdf.format(c.getTime());
		dateTimeCreation.setText(strDate);
		
		//For selection of category
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
		
		/*
		//For adding of tags
		tapToAddTags=(TextView)findViewById(R.id.tapToAddTags);
		tags=(TextView)findViewById(R.id.tags);
		tapToAddTags.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.activity_create_input_dialog, null);
 
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
 
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);
 
				final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
 
				// set dialog message
				alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("OK",
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {*/
						
					   /**values to be stored for noteTags column added here**/ 	
			/*		   noteTags=userInput.getText().toString();
					   
						   if(userInput.getText().toString().equals("")){
							   tags.setText("Tags: -");
						   }
						   else{
							   tags.setText("Tags: (" + userInput.getText() + ")");
						    }
					   }
					  })
					.setNegativeButton("Cancel",
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					    }
					  });
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();

			}
			
		});
		*/
		
		
		
		
		
		attachments=(TextView)findViewById(R.id.clickme);
		mLinearLayout = (LinearLayout) findViewById(R.id.expandable);
        //set visibility to GONE
        mLinearLayout.setVisibility(View.GONE);
        mLinearLayoutHeader = (LinearLayout) findViewById(R.id.header);
        mLinearLayoutHeader.setVisibility(View.GONE);
        
        hrTv=(TextView)findViewById(R.id.hrTv);
		hrTv.setVisibility(View.GONE);


        mLinearLayoutHeader.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mLinearLayout.getVisibility()==View.GONE){
                    expand();
                }else{
                    collapse();
                   
                }
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
		        	 titleOfNote="";
				     content="";
				     noteCategory="";
		        	 Toast.makeText(getApplicationContext(), "Note discarded.", Toast.LENGTH_LONG).show();
		        	 Intent intent = new Intent(CreateActivity.this, MainActivity.class);
						startActivity(intent);
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
	/*	else if(id==R.id.reset){
				suggestTitle.getText().clear();
				noteContent.getText().clear();
				imageView.setImageResource(android.R.color.transparent);
				noteCategory="";
				categorySelectionChoice.setText("Category: None Selected");
		}*/
		else if(id==R.id.uploadImage){
				Intent intent = new Intent();
				intent.setType("image/*");
				//intent.setType("*/*");
				//intent.setAction(Intent.ACTION_GET_CONTENT);
				intent.setAction(Intent.ACTION_PICK);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);	
		}
		else if(id==R.id.saveNote){
				saveNote();
			}
		
		else if(id==R.id.uploadVideo){
			Intent intent = new Intent();
	        intent.setType("video/*");
	       // intent.setAction(Intent.ACTION_GET_CONTENT);
	        intent.setAction(Intent.ACTION_PICK);
	        startActivityForResult(Intent.createChooser(intent, "Complete action using"),PICK_VIDEO);
			
		}
		
		/*else if(id==R.id.attachAudio){
			 Intent intent = new Intent();
	         intent.setType("audio/*");
	         //intent.setAction(Intent.ACTION_GET_CONTENT);
	         intent.setAction(Intent.ACTION_PICK);
	         startActivityForResult(Intent.createChooser(intent, "Complete action using"),PICK_AUDIO);
		}*/
		
		else if(id==R.id.attachLocation){
			getMyCurrentLocation();
		}	
		
		else if(id==R.id.addToCalendar){
			String title = suggestTitle.getText().toString();
			String content = noteContent.getText().toString();
			if(title.matches("")||content.matches("")||noteCategory.matches("")){
				Toast.makeText(getApplicationContext(), "Please fill in all the required details", Toast.LENGTH_LONG).show();
			}
				else{
				Calendar cal = Calendar.getInstance();     
		        Intent intent = new Intent(Intent.ACTION_EDIT);
		        intent.setType("vnd.android.cursor.item/event");
		        intent.putExtra("beginTime", cal.getTimeInMillis());
		        intent.putExtra("allDay", true);
		        //intent.putExtra("rrule", "FREQ=YEARLY");
		        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
		        intent.putExtra("title", title);
		        intent.putExtra("description", content);
		        startActivity(intent);
			}
		}
				
		return super.onOptionsItemSelected(item);
	}

	/**Set the selected image from gallery and display in image view**/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(resultCode == RESULT_OK){
			switch(requestCode){
				case PICK_IMAGE:
					Uri mImageUri = data.getData();
					try {
						Image = Media.getBitmap(this.getContentResolver(), mImageUri);
					//	attachment.setVisibility(View.VISIBLE);
					//	hrTv.setVisibility(View.VISIBLE);
						hrTv.setVisibility(View.VISIBLE);
				        mLinearLayoutHeader.setVisibility(View.VISIBLE);
						
						
						uriOfImage = mImageUri.toString();
						
						imageUriTv.setVisibility(View.VISIBLE);
						String uriOfImage = "<b>Image: </b>" +mImageUri.toString();
						imageUriTv.setText(Html.fromHtml(uriOfImage));
						imageView.setVisibility(View.VISIBLE);
						imageView.setImageBitmap(Image);
		
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();			
					} 
					catch (IOException e) {			
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
					
					
				case PICK_VIDEO:
					Uri mVideoURI = data.getData();        
		         //   attachment.setVisibility(View.VISIBLE);
				//	hrTv.setVisibility(View.VISIBLE);
					
					hrTv.setVisibility(View.VISIBLE);
			        mLinearLayoutHeader.setVisibility(View.VISIBLE);
					
					uriOfVideo = mVideoURI.toString();
					
					videoUriTv.setVisibility(View.VISIBLE);
					videoView.setVisibility(View.VISIBLE);
					String uriOfVideo = "<b>Video: </b>" + mVideoURI.toString();
					videoUriTv.setText(Html.fromHtml(uriOfVideo));
					videoView.setVideoURI(mVideoURI);
					videoView.setMediaController(videoMC);
					videoView.requestFocus();
					videoView.setOnPreparedListener(new OnPreparedListener(){

						@Override
						public void onPrepared(MediaPlayer mp) {
							// TODO Auto-generated method stub
							//videoView.start();
							videoMC.show(0);
						}
						
					});

					break;
					
			/*	case PICK_AUDIO:
					 attachment.setVisibility(View.VISIBLE);
				//	 hrTv.setVisibility(View.VISIBLE);
					 audioUriTv.setVisibility(View.VISIBLE);
					 audioView.setVisibility(View.VISIBLE);
					
					 Uri mAudioURI = data.getData();  
					
					 
					 uriOfAudio = mAudioURI.toString();
					
					 String uriOfAudio = "<b>Audio: </b>" + mAudioURI.toString();
					 audioUriTv.setText(Html.fromHtml(uriOfAudio));
									 
					 audioView.setMediaController(audioMC);
					 audioView.setVideoURI(mAudioURI);  
					 audioView.requestFocus();
				 
					 audioView.setOnPreparedListener(new OnPreparedListener(){

						@Override
						public void onPrepared(MediaPlayer arg0) {
							// TODO Auto-generated method stub
							//audioView.start();
							//audioMC.show(0);
						}
		            });		
					break;*/
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
		categorySelectionChoice.setText(category);
	}
	
	/**Notification for saving of note**/
	public void notifySuccess(){
		NotificationCompat.Builder mBuilder = 
				new NotificationCompat.Builder(this)
					.setSmallIcon(R.drawable.app_icon)
					.setContentTitle(getString(R.string.app_name))
					.setContentText(suggestTitle.getText().toString() + " successfully saved.");
		
		NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notifyID,mBuilder.build());
	}
	
	
	
	
	
	/** Check the type of GPS Provider available at that instance and  collect the location informations
    @Output Latitude and Longitude
   * */
   void getMyCurrentLocation() {    

       LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /*   if(location==null){  
       		location=locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
           }
	        if (location != null) {          
	        	accLoc=location.getAccuracy();
	            MyLat = location.getLatitude();
	            MyLong = location.getLongitude();
	        }  */
       try{
       	gps_enabled=locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
       }catch(Exception ex){
       	
       }
       try{
       	network_enabled=locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
       }catch(Exception ex){
       	
       }

       if(gps_enabled){
           location=locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

       }          

       
       if(network_enabled && location==null)    {
           location=locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

       }

   if (location != null) {          
   	accLoc=location.getAccuracy();
       MyLat = location.getLatitude();
       MyLong = location.getLongitude();
   } 
   
      try
       {
   	   //Getting address from found locations.
       Geocoder geocoder;  
       List<Address> addresses;
       geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(MyLat, MyLong, 1);
 
        Address = addresses.get(0).getAddressLine(0);
        City = addresses.get(0).getAddressLine(1);
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }

      if (Address != null && !Address.isEmpty()) {
    	  // doSomething
          addTv.setVisibility(View.VISIBLE);
    	  currentLocation.setVisibility(View.VISIBLE);
    	//  attachment.setVisibility(View.VISIBLE);
    //	  hrTv.setVisibility(View.VISIBLE);
    	  hrTv.setVisibility(View.VISIBLE);
          mLinearLayoutHeader.setVisibility(View.VISIBLE);
          currentLocation.setText(Address  +"\n" + City + ". \n(Co-ordinates:" + MyLat + ", " + MyLong + "). \nAccuracy: "+accLoc + " meters from actual location.");
    	}
      else{ 
          addTv.setVisibility(View.VISIBLE);
        //  attachment.setVisibility(View.VISIBLE);
          hrTv.setVisibility(View.VISIBLE);
          mLinearLayoutHeader.setVisibility(View.VISIBLE);
          
          currentLocation.setVisibility(View.VISIBLE);
          currentLocation.setText("Unavailable. Check if your GPS and Network are turned on");
    	// Toast.makeText(getApplicationContext(), "Check if your GPS and Network are turned on", Toast.LENGTH_LONG).show();
      }
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
	        	titleOfNote="";
				content="";
				noteCategory="";
	        	 Toast.makeText(getApplicationContext(), "Note discarded.", Toast.LENGTH_LONG).show();
	        	 Intent intent = new Intent(CreateActivity.this, MainActivity.class);
					startActivity(intent);
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

	private void expand() {
	     //set Visible
	     mLinearLayout.setVisibility(View.VISIBLE);
	     
	     final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
	     final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
	     mLinearLayout.measure(widthSpec, heightSpec);
	     
	     ValueAnimator mAnimator = slideAnimator(0, mLinearLayout.getMeasuredHeight());
	     
	     noteContent.setVisibility(View.GONE);
	     attachments.setText("Attachment(s) - tap to close");
	     
	     mAnimator.start();
	}
	 
	private void collapse() {
	     int finalHeight = mLinearLayout.getHeight();

	     ValueAnimator mAnimator = slideAnimator(finalHeight, 0);
	     
	     
	     mAnimator.addListener(new Animator.AnimatorListener() {
	    	 
			
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				mLinearLayout.setVisibility(View.GONE);
				 noteContent.setVisibility(View.VISIBLE);
				 attachments.setText("Attachment(s) - tap to view");

			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
	     mAnimator.start();
	     
	}

	private ValueAnimator slideAnimator(int start, int end) {
		  
	    ValueAnimator animator = ValueAnimator.ofInt(start, end);
	  
	    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	         @Override
	         public void onAnimationUpdate(ValueAnimator valueAnimator) {
	            //Update Height
	            int value = (Integer) valueAnimator.getAnimatedValue();
	            ViewGroup.LayoutParams layoutParams = mLinearLayout.getLayoutParams();
	            layoutParams.height = value;
	            mLinearLayout.setLayoutParams(layoutParams);
	         }
	    });
	    return animator;
	}
	
	
	
	
	public void saveNote(){
		titleOfNote = suggestTitle.getText().toString();
		content = noteContent.getText().toString();	
        
        Format df = DateFormat.getDateFormat(this);
		Format tf = DateFormat.getTimeFormat(this);
		
		onGoingEventCursor=getContentResolver().query(CalendarContract.Events.CONTENT_URI, new String[]{CalendarContract.Events.TITLE,CalendarContract.Events.DTSTART,CalendarContract.Events.DTEND},null,null,null);
		if (!(onGoingEventCursor.moveToFirst()) || onGoingEventCursor.getCount() ==0){
			//Toast.makeText(getApplicationContext(), "You dont have any events in calendar", Toast.LENGTH_LONG).show();
		}
		else{
		onGoingEventCursor.moveToFirst();
		
			do{

				eventTitle = onGoingEventCursor.getString(onGoingEventCursor.getColumnIndex(CalendarContract.Events.TITLE));
							
				eventStartDateTime = onGoingEventCursor.getLong(onGoingEventCursor.getColumnIndex(CalendarContract.Events.DTSTART));
				eventEndDateTime=onGoingEventCursor.getLong(onGoingEventCursor.getColumnIndex(CalendarContract.Events.DTEND));
				
				//Toast.makeText(getApplicationContext(), df.format(eventEndDateTime), Toast.LENGTH_LONG).show();
				
					long currentDateTime = new Date().getTime();

				if(eventStartDateTime<currentDateTime){
					if(currentDateTime<eventEndDateTime){
				//	Toast.makeText(getApplicationContext(), "Event on " + df.format(eventStartDateTime) + " at " + tf.format(eventStartDateTime) + " is currently happening. Today is " + df.format(currentDateTime) + " " +tf.format(currentDateTime), Toast.LENGTH_LONG).show();
					
					eventMatchCriteria = eventTitle;
					calendarOnGoingEvent = "MATCH";
					//replaceTitle();
					fullEventDetails="Event: " + eventTitle + ". \nStart: " + df.format(eventStartDateTime) + ", " + tf.format(eventStartDateTime) + ". \nEnd: " + df.format(eventEndDateTime) + ", " + tf.format(eventEndDateTime) + ". \n\nCurrent date and time: " +  df.format(currentDateTime) + ", " +tf.format(currentDateTime) +". \n\nReplace the current note title with event title?"  ;
					}
				}
				/*
				else if(eventStartDateTime>currentDateTime){
					Toast.makeText(getApplicationContext(), "Event on " + df.format(eventStartDateTime) + " at " + tf.format(eventStartDateTime) + " has yet to happen. Today is " + df.format(currentDateTime) + " " +tf.format(currentDateTime), Toast.LENGTH_LONG).show();
				}*/
			}while(onGoingEventCursor.moveToNext());
		}
		
		/**Get all notes in database**/
		ArrayList<Note> resultArray = new ArrayList<Note>();
		SQLiteController getEntry = new SQLiteController(this);
        getEntry.open();
        resultArray.addAll(getEntry.retrieveNotes());
        getEntry.close();
        
       String duplicateTitle="";
		
       
       
       /**Check for duplicate titles**/
        for(int i=0; i<resultArray.size(); i++){
        	//if(resultArray.get(i).getNote_name().equals(noteTitle.getText().toString())){
        	if(resultArray.get(i).getNote_name().equals(suggestTitle.getText().toString())){
        		duplicateTitle="yes";
        	}
        	else if(resultArray.get(i).getNote_name().equals(eventMatchCriteria)){
        		calendarDuplicateTitle="yes";
        	}
        }
		
        
		
		if(titleOfNote.matches("")||content.matches("")||noteCategory.matches("")){
			Toast.makeText(getApplicationContext(), "Please fill in all the required details", Toast.LENGTH_LONG).show();
		}
		else{ 
			/**1-2**/
			if(calendarOnGoingEvent.equals("MATCH")){
			
				/***3***/
				if(calendarDuplicateTitle.matches("yes")){					
					//Toast.makeText(getApplicationContext(), "Duplicate title found, unable to save note. \n(On-going calendar event)", Toast.LENGTH_LONG).show();
				
					
					/**5**/
					if(duplicateTitle.matches("yes")){
						Toast.makeText(getApplicationContext(), "Duplicate title found, unable to save note (AFTER ONGOING CALENDAR EVENT)", Toast.LENGTH_LONG).show();
					}
					
					/***6**/
					else{
					//	Toast.makeText(getApplicationContext(), "AFTER ONGOING CALENDAR EVENT, NO DUPLICATE TITLE", Toast.LENGTH_LONG).show();
						saveNoteToDB();
					}
				}
				/**4**/
				else{
					/**7**/
					if(duplicateTitle.matches("yes")){
						Toast.makeText(getApplicationContext(), "NO DUPLICATE EVENT TITLE BUT Duplicate title found, unable to save note", Toast.LENGTH_LONG).show();
					}
					/**8**/
					else{
					//	Toast.makeText(getApplicationContext(), "~NO DUPLICATE EVENT TITLE, NO DUPLICATE title found", Toast.LENGTH_LONG).show();
						replaceTitle();
					}
				}
			}
			else{
				if(duplicateTitle.matches("yes")){
						Toast.makeText(getApplicationContext(), "Duplicate title found, unable to save note", Toast.LENGTH_LONG).show();
				}
				else{
					saveNoteToDB();	
				}	
			}
		}
	}
	
	
	public void replaceTitle(){
		
		AlertDialog.Builder builder1 = new AlertDialog.Builder(CreateActivity.this);
       // builder1.setMessage("Event: " + eventMatchCriteria + " is currently on going, replace the current note title with event title?");
        builder1.setTitle("On-going Calendar Event Alert");
		builder1.setMessage(fullEventDetails);
		builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	/**9**/
            	titleOfNote = eventMatchCriteria;
            	// Toast.makeText(getApplicationContext(), "Note title replaced.", Toast.LENGTH_LONG).show();
            	saveNoteToDB();
               
            }
        });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               /**10**/
            	saveNoteToDB();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
	}
	
	
	
	public void saveNoteToDB(){
		boolean result = true;
		try{
		
			Note note = new Note(titleOfNote, content, noteCategory, uriOfImage, uriOfVideo, uriOfAudio);
		//Note note = new Note(title, content, noteCategory);
	
		SQLiteController entry = new SQLiteController(this);
		entry.open();
		entry.insertNote(note);
		entry.close();
		}catch(Exception e){
			result = false;
		}finally{
			if(result){
				notifySuccess();
				
				titleOfNote="";
				content="";
				noteCategory="";

				
				
				
				Toast.makeText(getApplicationContext(), "Note Saved", Toast.LENGTH_LONG).show();
				CreateActivity.this.finish();
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				
			}
			else{
				Toast.makeText(getApplicationContext(), "ERRORRRR", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	
	
}//class
	
	

