package com.example.it3176_smartnote;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.SQLiteController.it3176.SQLiteController;
import com.example.it3176_smartnote.model.Note;
import com.example.it3176_smartnote.util.ImageFullScreenActivity;
import com.example.it3176_smartnote.util.VideoPlayerActivity;

public class ArchiveDetail extends Activity {
	CheckBox cb_remember_setting;
	LinearLayout mLinearLayout;
	LinearLayout mLinearLayoutHeader;
	
	private ImageView imageView;
	private VideoView videoView;
	private VideoView audioView;
	
	private Cursor calendarEventTitleCursor;
	
	TextView dateTimeCreation, categorySelection, attachment, hrTv,	tapToAddTags, tags, addTv, currentLocation,	attachments, noteTitle, noteContent;
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
		videoView = (VideoView) findViewById(R.id.videoView);
		audioView = (VideoView) findViewById(R.id.audioView);
		addTv=(TextView) findViewById(R.id.addTv);
		currentLocation = (TextView) findViewById(R.id.currentLocation);
		
		
		imageView.setVisibility(View.GONE);
		videoView.setVisibility(View.GONE);

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
		//check and display if there is image
		if (!note.getNote_img().equals("")) {
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			imageView.setImageURI(Uri.parse(note.getNote_img()));
			imageView.setVisibility(View.VISIBLE);
			imageView.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					// TODO Auto-generated method stub
					int action = event.getAction();
					switch(action){
					case MotionEvent.ACTION_UP:
					Intent reviewImageFullScreen = new Intent(ArchiveDetail.this,ImageFullScreenActivity.class);
					reviewImageFullScreen.putExtra("uri", note.getNote_img());
					 startActivity(reviewImageFullScreen);
					 break;
					}
					return true;
				}
				
			});
		}
		//check and display if there is video
		if(!note.getNote_video().equals("")){
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			hrTv.setVisibility(View.VISIBLE);		
			videoView.setVisibility(View.VISIBLE);
			videoView.setVideoURI(Uri.parse(note.getNote_video()));
			videoView.setMediaController(videoMC);
			videoView.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					Intent videoAudioPlayer = new Intent(ArchiveDetail.this,VideoPlayerActivity.class);
					videoAudioPlayer.putExtra("uri", note.getNote_video());
					startActivity(videoAudioPlayer);
					return false;
				}
				
			});
			
		}
		
		if(!note.getNote_address().equals("")){
			mLinearLayoutHeader.setVisibility(View.VISIBLE);
			hrTv.setVisibility(View.VISIBLE);
			addTv.setVisibility(View.VISIBLE);
			addTv.setText(note.getNote_address());
		}
		
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
					SQLiteController controller = new SQLiteController(ArchiveDetail.this);
					try{
						controller.open();
						controller.reactivateNote(note);
					} catch(SQLException e){
						System.out.println(e);
					} finally{
						controller.close();
						ArchiveDetail.this.finish();
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

	@Override
	public void onBackPressed() {
		ArchiveDetail.this.finish();
		
		super.onBackPressed();
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
