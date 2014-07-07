package com.example.it3176_smartnote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;

import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.SQLiteController.it3176.SQLiteController;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.example.it3176_smartnote.dropbox.UploadToDropbox;
import com.example.it3176_smartnote.model.Note;
import com.example.it3176_smartnote.util.ImageFullScreenActivity;
import com.example.it3176_smartnote.util.VideoPlayerActivity;

public class NoteDetail extends Activity {
	CheckBox cb_remember_setting;
	Menu menu;
	LinearLayout mLinearLayout;
	LinearLayout mLinearLayoutHeader;
	NfcAdapter nfc;
	private ImageView imageView;
	private VideoView videoView;
	Intent intent;
	private Intent mIntent;
	// private File mParentPath;
	

	private Uri[] mFileUris = new Uri[10];
	Uri fileUri;
	BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	TextView dateTimeCreation, categorySelection, attachment, hrTv,
			tapToAddTags, tags, addTv, currentLocation, attachments, noteTitle,
			noteContent, imageFilePathTextView, videoFilePathTextView;
	Note note;
	MediaController videoMC;
	MediaController audioMC;
	int noteID, selected;
	
	String formattedDateTime;
	
	//Initalize for Dropbox
	final static private String APP_KEY = "ajddbjayy7yheai";
	final static private String APP_SECRET = "hzlxix5dla74hkj";
	private AccessType type = AccessType.DROPBOX;
	private DropboxAPI<AndroidAuthSession> mDBApi;
	AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
	AndroidAuthSession session = new AndroidAuthSession(appKeys, type);
	String token2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_detail);

		noteID = getIntent().getIntExtra("noteID", 0);
		SQLiteController getEntry = new SQLiteController(this);
		getEntry.open();
		note = getEntry.retrieveNote(noteID);
		getEntry.close();
	
		//Retrieve the token v2 from Dropbox Core API
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		token2 = sp.getString("token2", null);

		noteTitle = (TextView) findViewById(R.id.noteTitle);
		noteTitle.setText(note.getNote_name());
		noteContent = (TextView) findViewById(R.id.noteContent);
		noteContent.setText(note.getNote_content());
		categorySelection = (TextView) findViewById(R.id.categorySelectionChoice);
		categorySelection.setText(note.getNote_category());
		dateTimeCreation = (TextView) findViewById(R.id.currentDateTimeOfCreation);
		dateTimeCreation.setText(note.getNote_date());
	
		String newDate = dateTimeCreation.getText().toString().replaceAll("-", "");
		String newDateTime = newDate.replaceAll(":", "");
		formattedDateTime = newDateTime.replaceAll(" ", "");
		
		imageView = (ImageView) findViewById(R.id.imageView);
		videoView = (VideoView) findViewById(R.id.videoView);

		imageFilePathTextView = (TextView) findViewById(R.id.imageFilePathTextView);
		videoFilePathTextView = (TextView) findViewById(R.id.videoFilePathTextView);
		addTv = (TextView) findViewById(R.id.addTv);
		currentLocation = (TextView) findViewById(R.id.currentLocation);

		imageView.setVisibility(View.GONE);
		videoView.setVisibility(View.GONE);

		imageFilePathTextView.setVisibility(View.GONE);
		videoFilePathTextView.setVisibility(View.GONE);

		addTv.setVisibility(View.GONE);
		currentLocation.setVisibility(View.GONE);

		videoMC = new MediaController(this);
		videoMC.setAnchorView(videoView);

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
		// check and display if there is image
		if (!note.getNote_img().equals("") && (note.getNote_img() != null)) {
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			imageView.setImageURI(Uri.parse(note.getNote_img()));
			imageView.setVisibility(View.VISIBLE);
			imageFilePathTextView.setText(note.getNote_img().substring(
					note.getNote_img().lastIndexOf("/") + 1,
					note.getNote_img().length()));
			imageFilePathTextView.setVisibility(View.VISIBLE);

			final int rotateImage = getCameraPhotoOrientation(NoteDetail.this,
					Uri.parse(note.getNote_img()), note.getNote_img());
			Bitmap yourSelectedImage = decodeSampledBitmapFromResource(
					note.getNote_img(), 140, 100);
			imageView.setImageBitmap(yourSelectedImage);
			Matrix matrix = new Matrix();
			imageView.setScaleType(ScaleType.MATRIX);
			matrix.postRotate(rotateImage, imageView.getDrawable().getBounds()
					.width() / 2,
					imageView.getDrawable().getBounds().height() / 2);

			imageView.setImageMatrix(matrix);
			imageView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					int action = event.getAction();
					switch (action) {
					case MotionEvent.ACTION_UP:

						Intent reviewImageFullScreen = new Intent(
								NoteDetail.this, ImageFullScreenActivity.class);
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
		// check and display if there is video
		if (!note.getNote_video().equals("") && (note.getNote_video() != null)) {
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			hrTv.setVisibility(View.VISIBLE);
			videoView.setVisibility(View.VISIBLE);
			videoView.setVideoURI(Uri.parse(note.getNote_video()));
			videoView.setMediaController(videoMC);
			videoFilePathTextView.setText(note.getNote_video().substring(
					note.getNote_video().lastIndexOf("/") + 1,
					note.getNote_video().length()));
			videoFilePathTextView.setVisibility(View.VISIBLE);
			videoView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					Intent videoAudioPlayer = new Intent(NoteDetail.this,
							VideoPlayerActivity.class);
					videoAudioPlayer.putExtra("uri", note.getNote_video());
					startActivity(videoAudioPlayer);
					return false;
				}

			});

		}

		if (!note.getNote_address().equals("") && (note.getNote_address() != null)) {
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			hrTv.setVisibility(View.VISIBLE);
			addTv.setVisibility(View.VISIBLE);
			addTv.setText(note.getNote_address());
		}

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_detail, menu);

		final SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		String selected_setting = sp.getString("selected_setting",
				"YourSetting");

		if (selected_setting.equals("archive")) {
			this.menu = menu;
			menu.getItem(1).setIcon(R.drawable.ic_action_collection);
		}

		else if (selected_setting.equals("delete")) {
			this.menu = menu;
			menu.getItem(1).setIcon(R.drawable.ic_action_discard);
		}

		else {
			this.menu = menu;
			menu.getItem(1).setIcon(R.drawable.ic_action_remove);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean isConnected = haveNetworkConnection();
		switch (item.getItemId()) {
		case R.id.sendNFC:
			if(btAdapter == null){
				Toast.makeText(getApplicationContext(), "no blue tooth", Toast.LENGTH_LONG).show();
			}else{
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_SEND);
				intent.setType("text/plain");
				if(note.getNote_address().equals("")){
					intent.putExtra(Intent.EXTRA_TEXT, "^" + note.getNote_name() + "^" + note.getNote_category() +"^" +  note.getNote_content()+ "^");
				}else{
				intent.putExtra(Intent.EXTRA_TEXT, "^" + note.getNote_name() + "^" + note.getNote_category() +"^" +  note.getNote_content()+ "^" + note.getNote_address() + "^");
				}
				startActivity(intent);
			}
			break;
		case R.id.send_email:
			Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);
			email.putExtra(Intent.EXTRA_SUBJECT, note.getNote_name() + " (" + note.getNote_category() + ")");
			email.putExtra(Intent.EXTRA_TEXT, note.getNote_content() + "\n\n\nNote saved at: " + note.getNote_address());
			if(!note.getNote_img().isEmpty() || !note.getNote_video().isEmpty()){
				ArrayList<Uri> uris = new ArrayList<Uri>();
				if(!note.getNote_img().isEmpty()){
					uris.add(Uri.parse("file://" + note.getNote_img()));
				}
				if(!note.getNote_video().isEmpty()){
					uris.add(Uri.parse("file://" + note.getNote_video()));
				}
				email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			}
			email.setType("message/rfc822");
			startActivity(Intent.createChooser(email, "Choose an Email client: "));
			break;

		case R.id.upload_dropbox:
			mDBApi = new DropboxAPI<AndroidAuthSession>(session);
			if ((token2 == null) || (token2.length() == 0)
					|| (token2.equalsIgnoreCase(null))) {
				mDBApi.getSession().startOAuth2Authentication(NoteDetail.this);
			} else {
				mDBApi.getSession().setOAuth2AccessToken(token2);
			}
			if (isConnected) {
				String FILENAME = "/smartnote-"+formattedDateTime+".txt";
				String string = "Title:" + noteTitle.getText().toString() + "\nCategory:" + categorySelection.getText().toString() 
						+ "\nContent:" + noteContent.getText().toString();
				FileOutputStream outputStream;
				File file = new File(Environment.getExternalStorageDirectory()
						+ "/Download/", FILENAME);
				try {
					outputStream = new FileOutputStream(file);
					outputStream.write(string.getBytes());
					outputStream.close();
					File newFile = file;
					FileInputStream inputStream = new FileInputStream(newFile);
					UploadToDropbox upload = new UploadToDropbox(
							NoteDetail.this, mDBApi, "/Smart_note/", file);
					upload.execute();
					inputStream.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(
						"There is no wifi connection or mobile data connection available. Please turn on either the wifi connection or mobile data connection.")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// do things
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
			break;
		case R.id.action_settings:

			final SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(this);
			String selected_setting = sp.getString("selected_setting",
					"YourSetting");

			// If user's had chose a preference before
			if (selected_setting.equals("archive")) {
				selected = 0;
			} else if (selected_setting.equals("delete")) {
				selected = 1;
			} else if (selected_setting.equals("none")) {
				selected = 2;
			}

			final CharSequence[] preferences = { "Archive", "Delete", "None" };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Preference").setSingleChoiceItems(preferences,
					selected, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (preferences[which].equals("Archive")) {
								selected = 0;
							} else if (preferences[which].equals("Delete")) {
								selected = 1;
							} else if (preferences[which].equals("None")) {
								selected = 2;
							}
						}
					});
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (preferences[selected].equals("Archive")) {
								savePreferences("selected_setting", "archive");
							} else if (preferences[selected].equals("Delete")) {
								savePreferences("selected_setting", "delete");
							} else if (preferences[selected].equals("None")) {
								savePreferences("selected_setting", "none");
							}
							NoteDetail.this.invalidateOptionsMenu();
						}
					});
			AlertDialog prefDialog = builder.create();
			prefDialog.show();
			break;

		case R.id.btn_remove:
			final SharedPreferences rsp = PreferenceManager
					.getDefaultSharedPreferences(this);
			String rselected_setting = rsp
					.getString("selected_setting", "none");

			// User's preference is archiving note
			if (rselected_setting.equals("archive")) {
				// Prompt for archive confirmation
				AlertDialog.Builder archiveBuilder = new AlertDialog.Builder(
						NoteDetail.this);
				archiveBuilder.setTitle("Archive").setMessage(
						"This note will be archived.");

				archiveBuilder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								archiveNote();
							}
						});
				archiveBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				AlertDialog dialog = archiveBuilder.create();
				dialog.show();
			}

			// User's preference is deleting note
			else if (rselected_setting.equals("delete")) {
				// Prompt for delete confirmation
				AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(
						NoteDetail.this);
				deleteBuilder.setTitle("Delete").setMessage(
						"This note will be deleted.");

				deleteBuilder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								deleteNote();
							}
						});
				deleteBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				AlertDialog deleteDialog = deleteBuilder.create();
				deleteDialog.show();
			}

			// User did not set preference
			else {
				LayoutInflater inflater = LayoutInflater.from(this);
				final View setting_view = inflater.inflate(
						R.layout.setting_dialog, null);
				cb_remember_setting = (CheckBox) setting_view
						.findViewById(R.id.cb_remember_setting);
				savePreferences("preference", false);

				AlertDialog.Builder optionBuilder = new AlertDialog.Builder(
						this);
				optionBuilder.setView(setting_view);
				optionBuilder.setTitle("Select an action");

				// Checking whether user set preference
				cb_remember_setting
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(CompoundButton arg0,
									boolean arg1) {
								if (cb_remember_setting.isChecked()) {
									savePreferences("preference", true);
								} else {
									savePreferences("preference", false);
								}
							}
						});

				// Select action "Archive"
				optionBuilder.setNegativeButton("Archive",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Prompt for archive confirmation
								AlertDialog.Builder archiveBuilder = new AlertDialog.Builder(
										NoteDetail.this);
								archiveBuilder.setTitle("Archive").setMessage(
										"This note will be archived.");

								archiveBuilder.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												if (rsp.getBoolean(
														"preference", true)) {
													savePreferences(
															"selected_setting",
															"archive");
												}
												archiveNote();
											}
										});
								archiveBuilder.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
											}
										});
								AlertDialog archiveDialog = archiveBuilder
										.create();
								archiveDialog.show();
							}
						});

				// Select action "Delete"
				optionBuilder.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Prompt for delete confirmation
								AlertDialog.Builder archiveBuilder = new AlertDialog.Builder(
										NoteDetail.this);
								archiveBuilder.setTitle("Delete").setMessage(
										"This note will be deleted.");

								archiveBuilder.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												if (rsp.getBoolean(
														"preference", true)) {
													savePreferences(
															"selected_setting",
															"delete");
												}
												deleteNote();
											}
										});
								archiveBuilder.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
											}
										});
								AlertDialog deleteDialog = archiveBuilder
										.create();
								deleteDialog.show();
							}
						});
				AlertDialog dialog = optionBuilder.create();
				dialog.show();
			}
			break;
		case R.id.btn_edit:
			Intent intent = new Intent(getApplicationContext(),
					UpdateActivity.class);
			intent.putExtra("noteID", noteID);
			startActivity(intent);
			this.finish();
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * This method is to check if there is any existing connection
	 * @return boolean
	 */
	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	private void archiveNote() {
		note = new Note(getIntent().getIntExtra("noteID", 0));
		SQLiteController controller = new SQLiteController(NoteDetail.this);
		try {
			controller.open();
			controller.updateNoteStatus(note, "archive");
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			controller.close();
			this.finish();
			Intent refresh = new Intent(NoteDetail.this, MainActivity.class);
			refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(refresh);
			Toast.makeText(getBaseContext(), "Note archived", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void deleteNote() {
		note = new Note(getIntent().getIntExtra("noteID", 0));
		SQLiteController controller = new SQLiteController(NoteDetail.this);
		try {
			controller.open();
			controller.deleteNote(note);
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			controller.close();
			this.finish();
			Intent refresh = new Intent(NoteDetail.this, MainActivity.class);
			refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(refresh);
			Toast.makeText(getBaseContext(), "Note deleted", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void savePreferences(String key, boolean value) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	private void savePreferences(String key, String value) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	@Override
	public void onBackPressed() {
		NoteDetail.this.finish();
		Intent refresh = new Intent(NoteDetail.this, MainActivity.class);
		refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(refresh);

		super.onBackPressed();
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

}
