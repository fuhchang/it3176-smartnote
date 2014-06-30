package com.example.it3176_smartnote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.SQLiteController.it3176.SQLiteController;
import com.example.it3176_smartnote.CreateActivity.CreateNoteDialog;
import com.example.it3176_smartnote.model.Note;
import com.example.it3176_smartnote.util.ImageFullScreenActivity;
import com.example.it3176_smartnote.util.VideoPlayerActivity;

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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.CursorLoader;
import android.text.Html;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class UpdateActivity extends Activity {

	static LinearLayout mLinearLayout;
	static LinearLayout mLinearLayoutHeader;

	private static Bitmap Image = null;
	private static ImageView imageView;
	private static VideoView videoView;
	private VideoView audioView;

	private int noteID;
	private Note note;

	/*** Request Code **/
	int notifyID = 1088;
	private static final int PICK_IMAGE = 1;
	private static final int CAPTURE_PHOTO = 100;
	private static final int PICK_VIDEO = 2;
	private static final int CAPTURE_VIDEO = 200;
	private static final int PICK_AUDIO = 3;

	/*** Dialogs ***/
	static int SELECTION_CHOICE_DIALOG = 1;
	static int REMOVAL_CHOICE_DIALOG = 2;

	static String[] selectionArray;
	static String[] attachmentArray;

	TextView dateTimeCreation, categorySelection, attachment;
	static TextView hrTv;
	static TextView imageUriTv;
	static TextView videoUriTv;
	TextView audioUriTv;
	TextView tapToAddTags;
	TextView tags;
	static TextView addTv;
	static TextView currentLocation;
	TextView attachments;
	static TextView categorySelectionChoice;
	EditText noteTitle;
	static EditText noteContent;
	AutoCompleteTextView suggestTitle;
	Button btnSave;

	MediaController videoMC;
	MediaController audioMC;

	/** Values to be stored in database **/
	String titleOfNote = "";
	String content = "";
	static String uriOfImage = "";
	static String uriOfVideo = "";
	String uriOfAudio = "";
	static String noteCategory = "";
	static String storingAddress = "";

	String calendarDuplicateTitle = "";
	static String category = "";

	static String noteTags = "";

	Location location;
	Double MyLat, MyLong;
	float accLoc;
	String Address = "";
	String City = "";
	private boolean gps_enabled = false;
	private boolean network_enabled = false;

	final Context context = this;
	private Cursor calendarEventTitleCursor;
	private Cursor onGoingEventCursor;
	String eventTitle;
	String eventMatchCriteria = "";
	long currentDateTime;
	Long eventStartDateTime;
	Long eventEndDateTime;
	String calendarOnGoingEvent = "";
	String fullEventDetails = "";
	ArrayList<String> eventTitles = new ArrayList<String>();

	static TextView imageFilePathTextView;
	static TextView videoFilePathTextView;

	ScrollView sv;
	FrameLayout frameLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);

		try {
			noteID = getIntent().getIntExtra("noteID", 0);
			SQLiteController getEntry = new SQLiteController(this);
			getEntry.open();
			note = getEntry.retrieveNote(noteID);
			getEntry.close();
		} catch (Exception ex) {
			Toast.makeText(this, "Unable to retrieve note. Please try again.",
					Toast.LENGTH_LONG).show();
			// TODO Auto-generated method stub
			titleOfNote = "";
			content = "";
			noteCategory = "";
			uriOfImage = "";
			uriOfVideo = "";
			storingAddress = "";
			Intent intent = new Intent(getApplicationContext(),
					NoteDetail.class);
			intent.putExtra("noteID", note.getNote_id());
			startActivity(intent);
			UpdateActivity.this.finish();
		}

		noteContent = (EditText) findViewById(R.id.noteContent);
		noteContent.setText(note.getNote_content());

		//sv = (ScrollView) findViewById(R.id.sv);
		frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
		
		btnSave = (Button) findViewById(R.id.btnSave);

		imageView = (ImageView) findViewById(R.id.imageView);
		videoView = (VideoView) findViewById(R.id.videoView);
		imageUriTv = (TextView) findViewById(R.id.imageUriTv);
		videoUriTv = (TextView) findViewById(R.id.videoUriTv);
		addTv = (TextView) findViewById(R.id.addTv);
		currentLocation = (TextView) findViewById(R.id.currentLocation);
		imageFilePathTextView = (TextView) findViewById(R.id.imageFilePathTextView);
		videoFilePathTextView = (TextView) findViewById(R.id.videoFilePathTextView);

		imageView.setVisibility(View.GONE);
		videoView.setVisibility(View.GONE);
		imageFilePathTextView.setVisibility(View.GONE);
		videoFilePathTextView.setVisibility(View.GONE);

		addTv.setVisibility(View.GONE);
		currentLocation.setVisibility(View.GONE);

		attachments = (TextView) findViewById(R.id.clickme);
		mLinearLayout = (LinearLayout) findViewById(R.id.expandable);
		// set visibility to GONE
		mLinearLayout.setVisibility(View.GONE);
		mLinearLayoutHeader = (LinearLayout) findViewById(R.id.header);
		mLinearLayoutHeader.setVisibility(View.GONE);
		hrTv = (TextView) findViewById(R.id.hrTv);
		hrTv.setVisibility(View.GONE);

		mLinearLayoutHeader.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mLinearLayout.getVisibility() == View.GONE) {
					expand();
				} else {
					collapse();

				}
			}
		});

		if (!note.getNote_img().toString().equals("")) {
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			hrTv.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageURI(Uri.parse(note.getNote_img()));
			uriOfImage = note.getNote_img();
			imageFilePathTextView.setVisibility(View.VISIBLE);
			final int rotateImage = getCameraPhotoOrientation(
					UpdateActivity.this, Uri.parse(note.getNote_img()), uriOfImage);
			imageFilePathTextView.setText(note.getNote_img().substring(
					note.getNote_img().lastIndexOf("/") + 1,
					note.getNote_img().length()));
			Bitmap yourSelectedImage = decodeSampledBitmapFromResource(uriOfImage,
					140, 100);
			imageView.setImageBitmap(yourSelectedImage);
			int width = imageView.getWidth();
			int height = imageView.getHeight();
			// Bitmap newImage =
			// decodeSampledBitmapFromResource(selectedImagePath,width,height);
			// imageUploaded.setImageBitmap(newImage);
			// imageUploaded.setImageURI(selectedImageUri);
			Matrix matrix = new Matrix();
			imageView.setScaleType(ScaleType.MATRIX); // required
			matrix.postRotate((float) rotateImage, imageView.getDrawable()
					.getBounds().width() / 2, imageView.getDrawable()
					.getBounds().height() / 2);
			imageView.setImageMatrix(matrix);
			imageView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					int action = event.getAction();
					switch (action) {
					case MotionEvent.ACTION_UP:

						Intent reviewImageFullScreen = new Intent(
								UpdateActivity.this,
								ImageFullScreenActivity.class);
						ByteArrayOutputStream bs = new ByteArrayOutputStream();
						BitmapDrawable drawable = (BitmapDrawable) imageView
								.getDrawable();
						Bitmap bitmap = drawable.getBitmap();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
						reviewImageFullScreen.putExtra("byteArray",
								bs.toByteArray());
						reviewImageFullScreen.putExtra("uri",
								imageFilePathTextView.getText().toString());
						reviewImageFullScreen.putExtra("rotateImage",
								rotateImage);
						startActivity(reviewImageFullScreen);
						break;
					}
					return true;
				}
			});

		}
		if (!note.getNote_video().toString().equals("")) {
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			hrTv.setVisibility(View.VISIBLE);
			videoView.setVisibility(View.VISIBLE);
			videoFilePathTextView.setVisibility(View.VISIBLE);
			videoFilePathTextView.setText(note.getNote_video().substring(
					note.getNote_video().lastIndexOf("/") + 1,
					note.getNote_video().length()));
			uriOfVideo = note.getNote_video();

			videoView.setVideoURI(Uri.parse(note.getNote_video()));

			videoView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					Intent videoAudioPlayer = new Intent(UpdateActivity.this,
							VideoPlayerActivity.class);
					videoAudioPlayer.putExtra("uri", uriOfVideo);
					startActivity(videoAudioPlayer);
					return false;
				}
			});
		}
		if (!note.getNote_address().toString().equals("")
				&& (note.getNote_address() != null)) {
			addTv.setVisibility(View.VISIBLE);
			currentLocation.setVisibility(View.VISIBLE);
			hrTv.setVisibility(View.VISIBLE);
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			currentLocation.setText(note.getNote_address().toString());
			//frameLayout.getLayoutParams().height = 770;
		}
		else{
			//sv.getLayoutParams().height = 300;
			//frameLayout.removeView(sv);
			frameLayout.getLayoutParams().height = 470;
		}

		calendarEventTitleCursor = getContentResolver().query(
				CalendarContract.Events.CONTENT_URI,
				new String[] { CalendarContract.Events.TITLE }, null, null,
				null);
		if (!(calendarEventTitleCursor.moveToFirst())
				|| calendarEventTitleCursor.getCount() == 0) {
			// Toast.makeText(getApplicationContext(),
			// "You dont have any events in calendar",
			// Toast.LENGTH_LONG).show();
			Log.d("CALENDAR TITLE SUGGESTION",
					"You dont have any events in calendar for note title suggestion");
		} else {
			calendarEventTitleCursor.moveToFirst();
			do {

				// Toast.makeText(getApplicationContext(),
				// calendarEventTitleCursor.getString(calendarEventTitleCursor.getColumnIndex(CalendarContract.Events.TITLE)),
				// Toast.LENGTH_SHORT).show();
				eventTitles
						.add(calendarEventTitleCursor.getString(calendarEventTitleCursor
								.getColumnIndex(CalendarContract.Events.TITLE)));
			} while (calendarEventTitleCursor.moveToNext());

		}
		suggestTitle = (AutoCompleteTextView) findViewById(R.id.noteTitle);
		suggestTitle.setText(note.getNote_name());

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, eventTitles);
		suggestTitle.setAdapter(adapter);

		// Get time of pressing "New Note"
		dateTimeCreation = (TextView) findViewById(R.id.currentDateTimeOfCreation);
		String strDate = note.getNote_date();
		dateTimeCreation.setText(strDate);

		// For selection of category
		selectionArray = getResources().getStringArray(R.array.category_choice);
		categorySelection = (TextView) findViewById(R.id.categorySelection);

		categorySelectionChoice = (TextView) findViewById(R.id.categorySelectionChoice);
		noteCategory = note.getNote_category();
		categorySelectionChoice.setText("Category: " + noteCategory);

		categorySelection.setOnClickListener(new OnClickListener() {
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		videoView.seekTo(10000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update, menu);
		return true;
	}

	/** Menu items **/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		if (id == R.id.backToMain) {
			titleOfNote = "";
			content = "";
			noteCategory = "";
			uriOfImage = "";
			uriOfVideo = "";
			uriOfAudio = "";
			storingAddress = "";
			Intent intent = new Intent(UpdateActivity.this, NoteDetail.class);
			intent.putExtra("noteID", note.getNote_id());
			startActivity(intent);
			UpdateActivity.this.finish();
		} else if (id == R.id.uploadImage) {
			Intent intent = new Intent();
			intent.setType("image/*");
			// intent.setType("*/*");
			intent.setAction(Intent.ACTION_PICK);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
		} else if (id == R.id.captureImage) {
			Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intentPicture, CAPTURE_PHOTO);
		} else if (id == R.id.saveNote) {
			saveNote();
		}

		else if (id == R.id.uploadVideo) {
			Intent intent = new Intent();
			intent.setType("video/*");
			intent.setAction(Intent.ACTION_PICK);
			startActivityForResult(
					Intent.createChooser(intent, "Complete action using"),
					PICK_VIDEO);

		}

		else if (id == R.id.captureVideo) {
			Intent intent = new Intent(
					android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
			startActivityForResult(intent, CAPTURE_VIDEO);

		}

		else if (id == R.id.attachLocation) {
			getMyCurrentLocation();
			//frameLayout.getLayoutParams().height = 880;
		}

		else if (id == R.id.removeAtt) {
			attachmentArray = getResources().getStringArray(
					R.array.attachment_choice);

			CreateNoteDialog dialog = new CreateNoteDialog();
			dialog.setDialogType(REMOVAL_CHOICE_DIALOG);
			dialog.show(getFragmentManager(), "CreateNoteDialog");
		}

		else if (id == R.id.addToCalendar) {
			String title = suggestTitle.getText().toString();
			String content = noteContent.getText().toString();
			if (title.matches("") || content.matches("")
					|| noteCategory.matches("")) {
				Toast.makeText(getApplicationContext(),
						"Please fill in all the required details",
						Toast.LENGTH_LONG).show();
			} else {
				Calendar cal = Calendar.getInstance();
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra("beginTime", cal.getTimeInMillis());
				intent.putExtra("allDay", true);
				// intent.putExtra("rrule", "FREQ=YEARLY");
				intent.putExtra("endTime",
						cal.getTimeInMillis() + 60 * 60 * 1000);
				intent.putExtra("title", title);
				intent.putExtra("description", content);
				startActivity(intent);
			}
		}

		/*
		 * else if(id==R.id.captureImage){ Intent intent = new
		 * Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		 * startActivityForResult(intent, CAPTURE_IMAGE); }
		 */

		return super.onOptionsItemSelected(item);
	}

	/** Insert note into database **/
	/*
	 * public void onClick(View view){ //Toast.makeText(getApplicationContext(),
	 * "Nooooo.", Toast.LENGTH_LONG).show(); //Toast.makeText(getBaseContext(),
	 * v.getId(), Toast.LENGTH_LONG).show();
	 * 
	 * String title = noteTitle.getText().toString(); String content =
	 * noteContent.getText().toString();
	 * 
	 * ArrayList<Note> resultArray = new ArrayList<Note>(); SQLiteController
	 * getEntry = new SQLiteController(this); getEntry.open();
	 * resultArray.addAll(getEntry.retrieveNotes()); getEntry.close();
	 * 
	 * String duplicateTitle="";
	 * 
	 * for(int i=0; i<resultArray.size(); i++){
	 * if(resultArray.get(i).getNote_name
	 * ().equals(noteTitle.getText().toString())){ duplicateTitle="yes"; } }
	 * 
	 * 
	 * if(title.matches("")||content.matches("")||noteCategory.matches("")){
	 * Toast.makeText(getApplicationContext(),
	 * "Please fill in all the required details", Toast.LENGTH_LONG).show(); }
	 * else if(duplicateTitle.matches("yes")){
	 * Toast.makeText(getApplicationContext(),
	 * "Duplicate title found, unable to save note", Toast.LENGTH_LONG).show();
	 * } else{
	 * 
	 * boolean result = true; try{
	 * 
	 * Note note = new Note(title, content, noteCategory);
	 * 
	 * SQLiteController entry = new SQLiteController(this); entry.open();
	 * entry.insertNote(note); entry.close(); }catch(Exception e){ result =
	 * false; }finally{ if(result){ Toast.makeText(getApplicationContext(),
	 * "Note Saved", Toast.LENGTH_LONG).show(); CreateActivity.this.finish();
	 * Intent intent = new Intent(this, MainActivity.class);
	 * startActivity(intent);
	 * 
	 * } } }
	 * 
	 * }
	 */

	/** Set the selected image from gallery and display in image view **/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PICK_IMAGE:
				Uri mImageUri = data.getData();
				try {
					Image = Media.getBitmap(this.getContentResolver(),
							mImageUri);
					hrTv.setVisibility(View.VISIBLE);
					mLinearLayoutHeader.setVisibility(View.VISIBLE);

					uriOfImage = getRealPathFromURI(mImageUri);
					final int rotateImage = getCameraPhotoOrientation(
							UpdateActivity.this, mImageUri, uriOfImage);
					imageFilePathTextView.setVisibility(View.VISIBLE);
					imageView.setVisibility(View.VISIBLE);
					Image = decodeSampledBitmapFromResource(uriOfImage, 140,
							100);
					imageView.setImageBitmap(Image);
					int width = imageView.getWidth();
					int height = imageView.getHeight();
					// Bitmap newImage =
					// decodeSampledBitmapFromResource(selectedImagePath,width,height);
					// imageUploaded.setImageBitmap(newImage);
					// imageUploaded.setImageURI(selectedImageUri);
					Matrix matrix = new Matrix();
					imageView.setScaleType(ScaleType.MATRIX); // required
					matrix.postRotate((float) rotateImage, imageView
							.getDrawable().getBounds().width() / 2, imageView
							.getDrawable().getBounds().height() / 2);
					imageView.setImageMatrix(matrix);
					imageFilePathTextView.setText(uriOfImage.substring(
							uriOfImage.lastIndexOf("/") + 1,
							uriOfImage.length()));
					imageView.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View arg0, MotionEvent event) {
							int action = event.getAction();
							switch (action) {
							case MotionEvent.ACTION_UP:
								Intent reviewImageFullScreen = new Intent(
										UpdateActivity.this,
										ImageFullScreenActivity.class);
								ByteArrayOutputStream bs = new ByteArrayOutputStream();
								BitmapDrawable drawable = (BitmapDrawable) imageView
										.getDrawable();
								Bitmap bitmap = drawable.getBitmap();
								bitmap.compress(Bitmap.CompressFormat.JPEG,
										100, bs);
								reviewImageFullScreen.putExtra("byteArray",
										bs.toByteArray());
								reviewImageFullScreen.putExtra("uri",
										imageFilePathTextView.getText()
												.toString());
								reviewImageFullScreen.putExtra("rotateImage",
										rotateImage);
								startActivity(reviewImageFullScreen);
								break;
							}
							return true;
						}
					});

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case CAPTURE_PHOTO:
				Uri capturedImageUri = data.getData();
				InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(
							capturedImageUri);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bitmap yourSelectedImage = BitmapFactory
						.decodeStream(imageStream);
				hrTv.setVisibility(View.VISIBLE);
				mLinearLayoutHeader.setVisibility(View.VISIBLE);
				uriOfImage = getRealPathFromURI(capturedImageUri);

				final int rotateImage = getCameraPhotoOrientation(
						UpdateActivity.this, capturedImageUri, uriOfImage);
				imageFilePathTextView.setVisibility(View.VISIBLE);
				imageFilePathTextView.setText(uriOfImage.substring(
						uriOfImage.lastIndexOf("/") + 1, uriOfImage.length()));
				imageView.setVisibility(View.VISIBLE);
				yourSelectedImage = decodeSampledBitmapFromResource(uriOfImage,
						140, 100);
				imageView.setImageBitmap(yourSelectedImage);
				int width = imageView.getWidth();
				int height = imageView.getHeight();
				// Bitmap newImage =
				// decodeSampledBitmapFromResource(selectedImagePath,width,height);
				// imageUploaded.setImageBitmap(newImage);
				// imageUploaded.setImageURI(selectedImageUri);
				Matrix matrix = new Matrix();
				imageView.setScaleType(ScaleType.MATRIX); // required
				matrix.postRotate((float) rotateImage, imageView.getDrawable()
						.getBounds().width() / 2, imageView.getDrawable()
						.getBounds().height() / 2);
				imageView.setImageMatrix(matrix);
				imageView.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View arg0, MotionEvent event) {
						int action = event.getAction();
						switch (action) {
						case MotionEvent.ACTION_UP:
							Intent reviewImageFullScreen = new Intent(
									UpdateActivity.this,
									ImageFullScreenActivity.class);
							ByteArrayOutputStream bs = new ByteArrayOutputStream();
							BitmapDrawable drawable = (BitmapDrawable) imageView
									.getDrawable();
							Bitmap bitmap = drawable.getBitmap();
							bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
							reviewImageFullScreen.putExtra("byteArray",
									bs.toByteArray());
							reviewImageFullScreen.putExtra("uri",
									imageFilePathTextView.getText().toString());
							reviewImageFullScreen.putExtra("rotateImage",
									rotateImage);
							startActivity(reviewImageFullScreen);
							break;
						}
						return true;
					}
				});
				break;
			case PICK_VIDEO:
				Uri mVideoURI = data.getData();
				hrTv.setVisibility(View.VISIBLE);
				mLinearLayoutHeader.setVisibility(View.VISIBLE);

				uriOfVideo = getRealPathFromURI(mVideoURI);

				videoView.setVisibility(View.VISIBLE);
				videoFilePathTextView.setVisibility(View.VISIBLE);
				videoFilePathTextView.setText(uriOfVideo.substring(
						uriOfVideo.lastIndexOf("/") + 1, uriOfVideo.length()));
				videoView.setVideoURI(mVideoURI);
				videoView.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub

						Intent videoAudioPlayer = new Intent(
								UpdateActivity.this, VideoPlayerActivity.class);
						videoAudioPlayer.putExtra("uri", uriOfVideo);
						startActivity(videoAudioPlayer);
						return false;
					}
				});
				break;

			case CAPTURE_VIDEO:
				try {
					Uri capturedVideoURI = data.getData();
					hrTv.setVisibility(View.VISIBLE);
					mLinearLayoutHeader.setVisibility(View.VISIBLE);
					// uriOfVideo = capturedVideoURI.toString();
					uriOfVideo = getRealPathFromURI(capturedVideoURI);
					videoFilePathTextView.setVisibility(View.VISIBLE);
					videoView.setVisibility(View.VISIBLE);
					// String uriOfVideo = "<b>Video: </b>" +
					// capturedVideoURI.toString();
					// videoUriTv.setText(Html.fromHtml(uriOfVideo));
					videoFilePathTextView.setText(uriOfVideo.substring(
							uriOfVideo.lastIndexOf("/") + 1,
							uriOfVideo.length()));
					videoView.setVideoURI(capturedVideoURI);
					videoView.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							// TODO Auto-generated method stub

							Intent videoAudioPlayer = new Intent(
									UpdateActivity.this,
									VideoPlayerActivity.class);
							videoAudioPlayer.putExtra("uri", uriOfVideo);
							startActivity(videoAudioPlayer);
							return false;
						}
					});
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}

	}

	public static Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public int getCameraPhotoOrientation(Context context, Uri imageUri,
			String imagePath) {
		int rotate = 0;
		try {
			context.getContentResolver().notifyChange(imageUri, null);
			File imageFile = new File(imagePath);

			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			default:
				rotate = 0;
				break;
			}

			Log.i("RotateImage", "Exif orientation: " + orientation);
			Log.i("RotateImage", "Rotate value: " + rotate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rotate;
	}

	// And to convert the image URI to the direct file system path of the image
	// file
	public String getRealPathFromURI(Uri contentUri) {

		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	/** Creates a dialog with title and options from array **/
	public static class CreateNoteDialog extends DialogFragment {
		int dialogType;

		public void setDialogType(int type) {
			dialogType = type;
		}

		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			if (dialogType == SELECTION_CHOICE_DIALOG) {
				builder.setTitle("Select Category");
				builder.setItems(R.array.category_choice,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								noteCategory = selectionArray[which];
								updateChoice();
							}
						});
			}
			if (dialogType == REMOVAL_CHOICE_DIALOG) {
				builder.setTitle("Which attachment do you want to remove?");
				builder.setItems(R.array.attachment_choice,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								if (which == 0) {
									if (uriOfImage.equals("")) {
										Toast.makeText(
												getActivity(),
												"You do not have any image attachment.",
												Toast.LENGTH_SHORT).show();
									} else {
										imageFilePathTextView
												.setVisibility(View.GONE);
										uriOfImage = "";
										imageView.setVisibility(View.GONE);

										if ((imageView.getVisibility() == View.GONE)
												&& (videoView.getVisibility() == View.GONE)
												&& (currentLocation
														.getVisibility() == View.GONE)) {
											hrTv.setVisibility(View.GONE);
											mLinearLayoutHeader
													.setVisibility(View.GONE);
											mLinearLayout
													.setVisibility(View.GONE);
											noteContent
													.setVisibility(View.VISIBLE);
										}
									}
								} else if (which == 1) {
									if (uriOfVideo.equals("")) {
										Toast.makeText(
												getActivity(),
												"You do not have any video attachment.",
												Toast.LENGTH_SHORT).show();
									} else {
										uriOfVideo = "";
										videoFilePathTextView
												.setVisibility(View.GONE);
										videoView.setVisibility(View.GONE);

										if ((imageView.getVisibility() == View.GONE)
												&& (videoView.getVisibility() == View.GONE)
												&& (currentLocation
														.getVisibility() == View.GONE)) {
											hrTv.setVisibility(View.GONE);
											mLinearLayoutHeader
													.setVisibility(View.GONE);
											mLinearLayout
													.setVisibility(View.GONE);
											noteContent
													.setVisibility(View.VISIBLE);
										}
									}
								} else if (which == 2) {

									if (currentLocation.getText().toString()
											.equals("")) {
										Toast.makeText(
												getActivity(),
												"You do not have any location attachment.",
												Toast.LENGTH_SHORT).show();
									} else {
										addTv.setVisibility(View.GONE);
										currentLocation
												.setVisibility(View.GONE);
										currentLocation.setText("");
										storingAddress = "";
										System.out.println(mLinearLayout.getHeight());
										if ((imageView.getVisibility() == View.GONE)
												&& (videoView.getVisibility() == View.GONE)
												&& (currentLocation
														.getVisibility() == View.GONE)) {
											hrTv.setVisibility(View.GONE);
											mLinearLayoutHeader
													.setVisibility(View.GONE);
											mLinearLayout
													.setVisibility(View.GONE);
											noteContent
													.setVisibility(View.VISIBLE);
										}
									}
								} else if (which == 3) {
									if ((uriOfImage.equals(""))
											&& (uriOfVideo.equals(""))
											&& (currentLocation.getText()
													.toString().equals(""))) {
										Toast.makeText(
												getActivity(),
												"You do not have any attachment.",
												Toast.LENGTH_SHORT).show();
									} else {
										imageFilePathTextView
												.setVisibility(View.GONE);
										uriOfImage = "";
										imageView.setVisibility(View.GONE);

										uriOfVideo = "";
										videoFilePathTextView
												.setVisibility(View.GONE);
										videoView.setVisibility(View.GONE);

										addTv.setVisibility(View.GONE);
										currentLocation
												.setVisibility(View.GONE);
										currentLocation.setText("");
										storingAddress = "";

										if ((imageView.getVisibility() == View.GONE)
												&& (videoView.getVisibility() == View.GONE)
												&& (currentLocation
														.getVisibility() == View.GONE)) {
											hrTv.setVisibility(View.GONE);
											mLinearLayoutHeader
													.setVisibility(View.GONE);
											mLinearLayout
													.setVisibility(View.GONE);
											noteContent
													.setVisibility(View.VISIBLE);
										}
									}
								}
							}
						});

			}
			return builder.create();
		}
	}

	/** Set textview to display selected category **/
	public static void updateChoice() {
		category = "";

		if (noteCategory.length() > 0) {
			category = "Category: " + noteCategory;
		}
		categorySelectionChoice.setText(category);
	}

	/** Notification for saving of note **/
	public void notifySuccess() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(
						suggestTitle.getText().toString()
								+ " successfully saved.");

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notifyID, mBuilder.build());
	}

	/** For back pressed event **/
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		titleOfNote = "";
		content = "";
		noteCategory = "";
		uriOfImage = "";
		uriOfVideo = "";
		storingAddress = "";
		Intent intent = new Intent(getApplicationContext(), NoteDetail.class);
		intent.putExtra("noteID", note.getNote_id());
		startActivity(intent);
		UpdateActivity.this.finish();
	}

	/**
	 * Check the type of GPS Provider available at that instance and collect the
	 * location informations
	 * 
	 * @Output Latitude and Longitude
	 * */
	void getMyCurrentLocation() {

		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		/*
		 * if(location==null){
		 * location=locManager.getLastKnownLocation(LocationManager
		 * .NETWORK_PROVIDER); } if (location != null) {
		 * accLoc=location.getAccuracy(); MyLat = location.getLatitude(); MyLong
		 * = location.getLongitude(); }
		 */
		try {
			gps_enabled = locManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {

		}
		try {
			network_enabled = locManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {

		}

		if (gps_enabled) {
			location = locManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		}

		if (network_enabled && location == null) {
			location = locManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		}

		if (location != null) {
			accLoc = location.getAccuracy();
			MyLat = location.getLatitude();
			MyLong = location.getLongitude();
		}

		try {
			// Getting address from found locations.
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(this, Locale.getDefault());
			addresses = geocoder.getFromLocation(MyLat, MyLong, 1);

			Address = addresses.get(0).getAddressLine(0);
			City = addresses.get(0).getAddressLine(1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Address != null && !Address.isEmpty()) {
			// doSomething
			addTv.setVisibility(View.VISIBLE);
			currentLocation.setVisibility(View.VISIBLE);
			hrTv.setVisibility(View.VISIBLE);
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			currentLocation.setText(Address + "\n" + City
					+ ". \n(Co-ordinates:" + MyLat + ", " + MyLong
					+ "). \nAccuracy: " + accLoc
					+ " meters from actual location.");
			storingAddress = Address + "\n" + City + ". \n(Co-ordinates:"
					+ MyLat + ", " + MyLong + "). \nAccuracy: " + accLoc
					+ " meters from actual location.";
		} else {
			AlertDialog.Builder builder1 = new AlertDialog.Builder(
					UpdateActivity.this);
			builder1.setTitle("Service Unavailable");
			builder1.setMessage("Unable to get your location, check if your GPS and Network are turned on.");
			builder1.setCancelable(true);
			builder1.setNegativeButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert11 = builder1.create();
			alert11.show();
		}
	}

	private void expand() {
		// set Visible
		mLinearLayout.setVisibility(View.VISIBLE);

		final int widthSpec = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		final int heightSpec = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		mLinearLayout.measure(widthSpec, heightSpec);

		ValueAnimator mAnimator = slideAnimator(0,
				mLinearLayout.getMeasuredHeight());

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
				// Update Height
				int value = (Integer) valueAnimator.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = mLinearLayout
						.getLayoutParams();
				layoutParams.height = value;
				mLinearLayout.setLayoutParams(layoutParams);
			}
		});
		return animator;
	}

	public void saveNote() {
		titleOfNote = suggestTitle.getText().toString();
		content = noteContent.getText().toString();

		Format df = DateFormat.getDateFormat(this);
		Format tf = DateFormat.getTimeFormat(this);

		onGoingEventCursor = getContentResolver().query(
				CalendarContract.Events.CONTENT_URI,
				new String[] { CalendarContract.Events.TITLE,
						CalendarContract.Events.DTSTART,
						CalendarContract.Events.DTEND }, null, null, null);
		if (!(onGoingEventCursor.moveToFirst())
				|| onGoingEventCursor.getCount() == 0) {
			// Toast.makeText(getApplicationContext(),
			// "You dont have any events in calendar",
			// Toast.LENGTH_LONG).show();
		} else {
			onGoingEventCursor.moveToFirst();

			do {

				eventTitle = onGoingEventCursor.getString(onGoingEventCursor
						.getColumnIndex(CalendarContract.Events.TITLE));

				eventStartDateTime = onGoingEventCursor
						.getLong(onGoingEventCursor
								.getColumnIndex(CalendarContract.Events.DTSTART));
				eventEndDateTime = onGoingEventCursor
						.getLong(onGoingEventCursor
								.getColumnIndex(CalendarContract.Events.DTEND));

				// Toast.makeText(getApplicationContext(),
				// df.format(eventEndDateTime), Toast.LENGTH_LONG).show();

				long currentDateTime = new Date().getTime();

				if (eventStartDateTime < currentDateTime) {
					if (currentDateTime < eventEndDateTime) {
						// Toast.makeText(getApplicationContext(), "Event on " +
						// df.format(eventStartDateTime) + " at " +
						// tf.format(eventStartDateTime) +
						// " is currently happening. Today is " +
						// df.format(currentDateTime) + " "
						// +tf.format(currentDateTime),
						// Toast.LENGTH_LONG).show();

						eventMatchCriteria = eventTitle;
						calendarOnGoingEvent = "MATCH";
						// replaceTitle();
						fullEventDetails = "Event: "
								+ eventTitle
								+ ". \nStart: "
								+ df.format(eventStartDateTime)
								+ ", "
								+ tf.format(eventStartDateTime)
								+ ". \nEnd: "
								+ df.format(eventEndDateTime)
								+ ", "
								+ tf.format(eventEndDateTime)
								+ ". \n\nCurrent date and time: "
								+ df.format(currentDateTime)
								+ ", "
								+ tf.format(currentDateTime)
								+ ". \n\nReplace the current note title with event title?";
					}
				}
				/*
				 * else if(eventStartDateTime>currentDateTime){
				 * Toast.makeText(getApplicationContext(), "Event on " +
				 * df.format(eventStartDateTime) + " at " +
				 * tf.format(eventStartDateTime) +
				 * " has yet to happen. Today is " + df.format(currentDateTime)
				 * + " " +tf.format(currentDateTime), Toast.LENGTH_LONG).show();
				 * }
				 */
			} while (onGoingEventCursor.moveToNext());
		}

		/** Get all notes in database **/
		ArrayList<Note> resultArray = new ArrayList<Note>();
		SQLiteController getEntry = new SQLiteController(this);
		getEntry.open();
		resultArray.addAll(getEntry.retrieveNotes());
		getEntry.close();

		String duplicateTitle = "";

		if (titleOfNote.matches("") || content.matches("")
				|| noteCategory.matches("")) {
			Toast.makeText(getApplicationContext(),
					"Please fill in all the required details",
					Toast.LENGTH_LONG).show();
		} else {
			/** 1-2 **/
			if (calendarOnGoingEvent.equals("MATCH")) {

				/*** 3 ***/
				if (calendarDuplicateTitle.matches("yes")) {
					// Toast.makeText(getApplicationContext(),
					// "Duplicate title found, unable to save note. \n(On-going calendar event)",
					// Toast.LENGTH_LONG).show();

					// Toast.makeText(getApplicationContext(),
					// "AFTER ONGOING CALENDAR EVENT, NO DUPLICATE TITLE",
					// Toast.LENGTH_LONG).show();
					saveNoteToDB();
				}
				/** 4 **/
				else {
					/** 7 **/
					// Toast.makeText(getApplicationContext(),
					// "~NO DUPLICATE EVENT TITLE, NO DUPLICATE title found",
					// Toast.LENGTH_LONG).show();
					replaceTitle();
				}
			} else {
				saveNoteToDB();
			}
		}
	}

	public void replaceTitle() {

		AlertDialog.Builder builder1 = new AlertDialog.Builder(
				UpdateActivity.this);
		// builder1.setMessage("Event: " + eventMatchCriteria +
		// " is currently on going, replace the current note title with event title?");
		builder1.setTitle("On-going Calendar Event Alert");
		builder1.setMessage(fullEventDetails);
		builder1.setCancelable(true);
		builder1.setNegativeButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						/** 9 **/
						titleOfNote = eventMatchCriteria;
						// Toast.makeText(getApplicationContext(),
						// "Note title replaced.", Toast.LENGTH_LONG).show();
						saveNoteToDB();

					}
				});
		builder1.setNeutralButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				/** 10 **/
				saveNoteToDB();
			}
		});

		builder1.setPositiveButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();

					}

				});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

	public void saveNoteToDB() {
		boolean result = true;
		try {
			Note note = new Note(titleOfNote, content, noteCategory,
					uriOfImage, uriOfVideo, storingAddress);
			// Note note = new Note(title, content, noteCategory);
			note.setNote_id(noteID);
			note.setNote_audio(uriOfAudio);
			SQLiteController entry = new SQLiteController(this);
			entry.open();
			entry.updateNote(note);
			entry.close();
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			if (result) {
				notifySuccess();

				titleOfNote = "";
				content = "";
				noteCategory = "";
				uriOfImage = "";
				uriOfVideo = "";
				uriOfAudio = "";
				storingAddress = "";

				Toast.makeText(getApplicationContext(), "Note Saved",
						Toast.LENGTH_LONG).show();
				UpdateActivity.this.finish();
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);

			} else {
				Toast.makeText(getApplicationContext(),
						"Something went wrong while storing.",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
