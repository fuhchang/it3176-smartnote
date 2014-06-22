package com.example.it3176_smartnote;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.MediaStore.Images.Media;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.SQLiteController.it3176.SQLiteController;
import com.example.it3176_smartnote.model.Note;

public class NoteDetail extends Activity {
	CheckBox cb_remember_setting;
	LinearLayout mLinearLayout;
	LinearLayout mLinearLayoutHeader;
	private ImageView imageView;
	private VideoView videoView;
	private VideoView audioView;
	
	private Cursor calendarEventTitleCursor;
	
	TextView dateTimeCreation, categorySelection, attachment, hrTv, imageUriTv,
			videoUriTv, audioUriTv, tapToAddTags, tags, addTv, currentLocation,
			attachments, noteTitle, noteContent;
	Note note;
	MediaController videoMC;
	MediaController audioMC;
	int noteID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_detail);
		noteID = getIntent().getIntExtra("noteID", 0);
		SQLiteController getEntry = new SQLiteController(this);
		getEntry.open();
		note = getEntry.retrieveNote(noteID);
		getEntry.close();
		
		
		
		noteTitle = (TextView) findViewById(R.id.noteTitle);
		noteTitle.setText(note.getNote_name());
		noteContent = (TextView) findViewById(R.id.noteContent);
		noteContent.setText(note.getNote_content());
		categorySelection = (TextView) findViewById(R.id.categorySelectionChoice);
		categorySelection.setText(note.getNote_category());
		dateTimeCreation = (TextView) findViewById(R.id.currentDateTimeOfCreation);
		dateTimeCreation.setText(note.getNote_date());

		imageView = (ImageView) findViewById(R.id.imageView);
		imageUriTv = (TextView) findViewById(R.id.imageUriTv);
		videoView = (VideoView) findViewById(R.id.videoView);
		videoUriTv = (TextView) findViewById(R.id.videoUriTv);
		audioView = (VideoView) findViewById(R.id.audioView);
		audioUriTv = (TextView) findViewById(R.id.audioUriTv);
		addTv=(TextView) findViewById(R.id.addTv);
		currentLocation = (TextView) findViewById(R.id.currentLocation);
		
		
		imageView.setVisibility(View.GONE);
		imageUriTv.setVisibility(View.GONE);
		videoView.setVisibility(View.GONE);
		videoUriTv.setVisibility(View.GONE);
		audioView.setVisibility(View.GONE);
		audioUriTv.setVisibility(View.GONE);

		addTv.setVisibility(View.GONE);
		currentLocation.setVisibility(View.GONE);

		videoMC = new MediaController(this);
		videoMC.setAnchorView(videoView);

		audioMC = new MediaController(this);
		audioMC.setAnchorView(audioView);

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
		
		if (!note.getNote_img().equals("")) {
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			imageView.setImageURI(Uri.parse(note.getNote_img()));
			imageView.setVisibility(View.VISIBLE);
			String uriOfImage = "<b>Image: </b>" + note.getNote_img();
			imageUriTv.setText(Html.fromHtml(uriOfImage));
			imageUriTv.setVisibility(View.VISIBLE);
		}
		
		if(!note.getNote_video().equals("")){
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			hrTv.setVisibility(View.VISIBLE);		
			videoView.setVisibility(View.VISIBLE);
			videoUriTv.setVisibility(View.VISIBLE);
			String uriOfVideo = "<b>Video: </b>" + note.getNote_video();
			videoUriTv.setText(Html.fromHtml(uriOfVideo));
			videoView.setVideoURI(Uri.parse(note.getNote_video()));
			videoView.setMediaController(videoMC);
			videoView.requestFocus();
			videoView.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					// videoView.start();
					videoMC.show(0);
				}
			});
		}
		/*
		if(!note.getNote_audio().equals("")){
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			hrTv.setVisibility(View.VISIBLE);
			audioUriTv.setVisibility(View.VISIBLE);
			audioView.setVisibility(View.VISIBLE);
			
			String uriOfAudio = "<b>Audio: </b>" + note.getNote_audio();
			audioUriTv.setText(Html.fromHtml(uriOfAudio));

			audioView.setMediaController(audioMC);
			audioView.setVideoURI(Uri.parse(note.getNote_audio()));
			audioView.requestFocus();

			audioView.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					// audioView.start();
					audioMC.show(0);
				}
			});
		}
		*/
		if(!note.getNote_address().equals("")){
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

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.btn_remove) {

			final SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(this);
			String selected_setting = sp.getString("selected_setting", "none");

			// User's preference is archiving note
			if (selected_setting.equals("archive")) {
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
				AlertDialog deleteDialog = archiveBuilder.create();
				deleteDialog.show();
			}

			// User's preference is deleting note
			else if (selected_setting.equals("delete")) {
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

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(setting_view);
				builder.setTitle("Select an action");

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
				builder.setNegativeButton("Archive",
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
												if (sp.getBoolean("preference",
														true)) {
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
								AlertDialog deleteDialog = archiveBuilder
										.create();
								deleteDialog.show();
							}
						});

				// Select action "Delete"
				builder.setPositiveButton("Delete",
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
												if (sp.getBoolean("preference",
														true)) {
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

				AlertDialog dialog = builder.create();
				dialog.show();
			}

		}else if(item.getItemId() == R.id.btn_edit){
			Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
			intent.putExtra("noteID", noteID);
			startActivity(intent);
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void archiveNote() {
		Note note = new Note(Integer.parseInt(getIntent().getStringExtra(
				"note_id")));
		SQLiteController controller = new SQLiteController(NoteDetail.this);
		try {
			controller.open();
			controller.updateNoteStatus(note, "archive");
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			controller.close();
			Intent refresh = new Intent(NoteDetail.this, MainActivity.class);
			startActivity(refresh);
			Toast.makeText(getBaseContext(), "Note archived", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void deleteNote() {
		Note note = new Note(Integer.parseInt(getIntent().getStringExtra(
				"note_id")));
		SQLiteController controller = new SQLiteController(NoteDetail.this);
		try {
			controller.open();
			controller.deleteNote(note);
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			controller.close();
			Intent refresh = new Intent(NoteDetail.this, MainActivity.class);
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
}
