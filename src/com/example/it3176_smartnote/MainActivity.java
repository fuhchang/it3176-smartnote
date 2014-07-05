package com.example.it3176_smartnote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.SQLiteController.it3176.SQLiteController;
import com.dropbox.chooser.android.DbxChooser;
import com.dropbox.client2.*;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.example.it3176_smartnote.dropbox.DownloadFromDropbox;
import com.example.it3176_smartnote.dropbox.UploadToDropbox;
import com.example.it3176_smartnote.model.Note;
import com.example.it3176_smartnote.util.readFileAsString;

@SuppressLint("ValidFragment")
public class MainActivity extends Activity {
	String selected_setting;
	ArrayList<Note> selected_notes = new ArrayList<Note>();
	SharedPreferences sp;
	int count, selected;
	noteList notelist;
	ListView list;
	String[] cateArray;
	DatePicker dpInputDate;
	private static final int pick_file = 1;
	private DatePickerDialog datePicker;
	private DatePickerDialog.OnDateSetListener dateListener;
	Integer[] imageId = { R.drawable.client, R.drawable.meeting,
			R.drawable.personnel, R.drawable.ic_launcher };
	ArrayList<Note> resultArray = new ArrayList<Note>();
	ArrayList<Note> tempArray = new ArrayList<Note>();
	ArrayList<Note> searchResult = new ArrayList<Note>();

	//Initialize for Dropbox
	final static private String APP_KEY = "ajddbjayy7yheai";
	final static private String APP_SECRET = "hzlxix5dla74hkj";
	private AccessType type = AccessType.DROPBOX;
	private DropboxAPI<AndroidAuthSession> mDBApi;
	AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
	AndroidAuthSession session = new AndroidAuthSession(appKeys, type);
	String token;
	String token2;

	private DbxChooser mChooser;
	static final int DBX_CHOOSER_REQUEST = 0; // You can change this if needed

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		cateArray = getResources().getStringArray(R.array.category_choice);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		token2 = sp.getString("token2", null);

		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		if ((token2 == null) || (token2.length() == 0)
				|| (token2.equalsIgnoreCase(null))) {
			mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
		} else {
			mDBApi.getSession().setOAuth2AccessToken(token2);
		}

		SQLiteController controller = new SQLiteController(this);
		controller.open();
		ArrayList<Note> temptArray = controller.retrieveNotes();
		temptArray = controller.autoUpdateNoteStatus(temptArray);
		controller.close();
		for (int i = 0; i < temptArray.size(); i++) {
			if (temptArray.get(i).getNote_status().equals("active")) {
				resultArray.add(temptArray.get(i));
			}
		}
		Collections.sort(resultArray, new DateDesComparator());
		notelist = new noteList(MainActivity.this, resultArray, imageId);
		list = (ListView) findViewById(R.id.noteListView);
		list.setAdapter(notelist);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getApplicationContext(),
						NoteDetail.class);
				intent.putExtra("noteID", resultArray.get(position)
						.getNote_id());
				startActivity(intent);
				MainActivity.this.finish();
			}

		});

		sp = PreferenceManager.getDefaultSharedPreferences(this);
		selected_setting = sp.getString("selected_setting", "YourSetting");

		if (selected_setting.equals("archive")
				|| selected_setting.equals("delete")) {
			list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
			list.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				@Override
				public boolean onActionItemClicked(ActionMode mode,
						MenuItem item) {
					switch (item.getItemId()) {

					case R.id.btn_remove:

						// Calls getSelectedIds method from noteList Class
						SparseBooleanArray selected = notelist.getSelectedIds();

						// Captures all selected ids with a loop
						for (int i = (selected.size() - 1); i >= 0; i--) {
							if (selected.valueAt(i)) {
								selected_notes.add(notelist.getItem(selected
										.keyAt(i)));
							}
						}

						if (selected_setting.equals("archive")) {
							// Prompt for archive confirmation
							AlertDialog.Builder archiveBuilder = new AlertDialog.Builder(
									MainActivity.this);

							archiveBuilder.setTitle("Archive").setMessage(
									"These note(s) will be archived.");

							archiveBuilder.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											for (int i = 0; i < selected_notes
													.size(); i++) {
												archiveNote(selected_notes
														.get(i));
											}
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
							AlertDialog dialog = archiveBuilder.create();
							dialog.show();
						}

						else if (selected_setting.equals("delete")) {
							// Prompt for delete confirmation
							AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(
									MainActivity.this);

							deleteBuilder.setTitle("Delete").setMessage(
									"These note(s) will be deleted.");

							deleteBuilder.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											for (int i = 0; i < selected_notes
													.size(); i++) {
												deleteNote(selected_notes
														.get(i));
											}
										}
									});
							deleteBuilder.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									});
							AlertDialog dialog = deleteBuilder.create();
							dialog.show();
						}

						// Close CAB
						mode.finish();
						break;
					}
					return false;
				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					mode.getMenuInflater().inflate(R.menu.remove, menu);

					if (selected_setting.equals("archive")) {
						menu.getItem(0)
								.setIcon(R.drawable.ic_action_collection);
					}

					else if (selected_setting.equals("delete")) {
						menu.getItem(0).setIcon(R.drawable.ic_action_discard);
					}
					return true;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					notelist.removeSelection();
				}

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode,
						int position, long id, boolean checked) {
					// Capture total checked items
					final int checkedCount = list.getCheckedItemCount();
					// Set the CAB title according to total checked items
					mode.setTitle(checkedCount + " Selected");
					// Calls toggleSelection method from noteList Class
					notelist.toggleSelection(position);
				}
			});
		}

		if (selected_setting.equals("archive")
				|| selected_setting.equals("delete")) {
			// Create a ListView-specific touch listener. ListViews are given
			// special treatment because
			// by default they handle touches for their list items... i.e.
			// they're in charge of drawing
			// the pressed state (the list selector), handling list item clicks,
			// etc.
			SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
					list,
					new SwipeDismissListViewTouchListener.DismissCallbacks() {

						@Override
						public void onDismiss(ListView listView,
								int[] reverseSortedPositions) {
							for (int position : reverseSortedPositions) {
								final int tempt = position;
								// notelist.remove(notelist.getItem(position));
								if (selected_setting.equals("archive")) {
									// Prompt for archive confirmation
									AlertDialog.Builder archiveBuilder = new AlertDialog.Builder(
											MainActivity.this);

									archiveBuilder
											.setTitle("Archive")
											.setMessage(
													notelist.getItem(position)
															.getNote_name()
															+ " will be archived.");

									archiveBuilder
											.setPositiveButton(
													"Ok",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															archiveNote(notelist
																	.getItem(tempt));
														}
													});
									archiveBuilder
											.setNegativeButton(
													"Cancel",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															Intent refresh = new Intent(
																	MainActivity.this,
																	MainActivity.class);
															refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
															startActivity(refresh);
														}
													});
									AlertDialog dialog = archiveBuilder
											.create();
									dialog.show();
								} else if (selected_setting.equals("delete")) {
									// Prompt for delete confirmation
									AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(
											MainActivity.this);

									deleteBuilder
											.setTitle("Delete")
											.setMessage(
													notelist.getItem(position)
															.getNote_name()
															+ " will be deleted.");

									deleteBuilder
											.setPositiveButton(
													"Ok",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															deleteNote(notelist
																	.getItem(tempt));
														}
													});
									deleteBuilder
											.setNegativeButton(
													"Cancel",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															Intent refresh = new Intent(
																	MainActivity.this,
																	MainActivity.class);
															refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
															startActivity(refresh);
														}
													});
									AlertDialog dialog = deleteBuilder.create();
									dialog.show();
								}
							}
							notelist.notifyDataSetChanged();
						}

						@Override
						public boolean canDismiss(int position) {
							return true;
						}
					});
			list.setOnTouchListener(touchListener);
			// Setting this scroll listener is required to ensure that during
			// ListView scrolling, we don't look for swipes.
			list.setOnScrollListener(touchListener.makeScrollListener());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mDBApi.getSession().authenticationSuccessful()) {
			try {
				// Required to complete auth, sets the access token on the
				// session
				mDBApi.getSession().finishAuthentication();

				token2 = mDBApi.getSession().getOAuth2AccessToken();
				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(this);
				Editor edit = sp.edit();
				edit.putString("token2", token2);
				edit.commit();
			} catch (IllegalStateException e) {
				Log.i("DbAuthLog", "Error authenticating", e);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_action, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search_icon)
				.getActionView();
		if (searchView != null) {
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
		}

		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				// Toast.makeText(getBaseContext(), newText,
				// Toast.LENGTH_LONG).show();
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				searchResult.clear();
				for (int i = 0; i < resultArray.size(); i++) {
					if (resultArray.get(i).getNote_name().equals(query)) {
						searchResult.add(resultArray.get(i));
					}
				}
				noteList notelist = new noteList(MainActivity.this,
						searchResult, imageId);
				list = (ListView) findViewById(R.id.noteListView);
				list.setAdapter(notelist);
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getApplicationContext(),
								NoteDetail.class);
						intent.putExtra("noteID", searchResult.get(position)
								.getNote_id());
						startActivity(intent);
					}

				});

				return false;
			}

		});

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		boolean isConnected = haveNetworkConnection();
		switch (item.getItemId()) {
		case R.id.import_file:
			Intent intentFile = new Intent(Intent.ACTION_GET_CONTENT);
			Uri uri = Uri.parse(Environment.getExternalStorageDirectory()
					.getPath() + "/myFolder/");
			intentFile.setDataAndType(uri, "text/csv");
			// startActivity(Intent.createChooser(intentFile , "Open folder"));
			startActivityForResult(
					Intent.createChooser(intentFile, "Select File"), pick_file);
			break;
		case R.id.download_dropbox:
			if (isConnected) {
				mChooser = new DbxChooser(APP_KEY);
				mChooser.forResultType(DbxChooser.ResultType.PREVIEW_LINK)
						.launch(MainActivity.this, DBX_CHOOSER_REQUEST);
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
		case R.id.new_icon:
			Intent intent = new Intent(this, CreateActivity.class);
			startActivity(intent);
			this.finish();
			break;
		case R.id.action_archive:
			Intent archive_intent = new Intent(this, ArchiveActivity.class);
			startActivity(archive_intent);
			break;
		case R.id.action_settings:
			sp = PreferenceManager.getDefaultSharedPreferences(this);
			selected_setting = sp.getString("selected_setting", "YourSetting");

			// If user's had chose a preference before
			if (selected_setting.equals("archive")) {
				selected = 0;
			} else if (selected_setting.equals("delete")) {
				selected = 1;
			} else {
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

							MainActivity.this.finish();
							Intent refresh = new Intent(MainActivity.this,
									MainActivity.class);
							startActivity(refresh);
						}
					});
			AlertDialog prefDialog = builder.create();
			prefDialog.show();
			break;
		case R.id.search_type:
			MyCategoryDialog dialog = new MyCategoryDialog();
			dialog.show(getFragmentManager(), "myCategoryDialog");
			break;
		case R.id.search_date:
			MyDatePicker datepicker = new MyDatePicker();
			datepicker.show(getFragmentManager(), "myDatePicker");
			break;
		case R.id.sort_title:
			sortByTitle sortTitle = new sortByTitle();
			sortTitle.show(getFragmentManager(), "sortByTitle");
			break;
		case R.id.sort_date:
			sortByDate sortDate = new sortByDate();
			sortDate.show(getFragmentManager(), "sortByDate");
			break;
		case R.id.sort_type:
			sortByType sortType = new sortByType();
			sortType.show(getFragmentManager(), "sortByType");
			break;
		case R.id.sort_location:
			sortByAddress sortAddress = new sortByAddress();
			sortAddress.show(getFragmentManager(), "sortByAdress");
			break;
		case R.id.refresh_icon:
			Intent refresh = new Intent(this, MainActivity.class);
			startActivity(refresh);
			this.finish();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			switch (requestCode) {
				case pick_file:
					ArrayList<String> readList = new ArrayList<String>();
					String file;
					Uri fileUri = data.getData();
					readFileAsString readFile = new readFileAsString();
				
					 try  {
						file = readFile.getStringFromFile(fileUri.toString().substring(7));
						StringTokenizer st = new StringTokenizer(file, "^");
						readList.clear();
						while(st.hasMoreElements()){
							readList.add(st.nextElement().toString());
						}
						
						
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(readList.size() != 0){
						Note note = new Note();
						note.setNote_name(readList.get(1));
						note.setNote_category(readList.get(2));
						note.setNote_content(readList.get(3));
						note.setNote_img("");
						note.setNote_video("");
						note.setNote_address("");
						SQLiteController entry = new SQLiteController(this);
						entry.open();
						entry.insertNote(note);
						entry.close();
						refresh();
					}
					break;
				case DBX_CHOOSER_REQUEST:
					//check to use switch case
					DbxChooser.Result result = new DbxChooser.Result(data);
					Log.d("main", "Link to selected file: " + result.getLink());
					String link = result.getLink().toString();
					String fileName = link.substring(link.lastIndexOf("/") + 1,link.length());
					System.out.print(fileName);
					String validatingFileName = link.substring(link.lastIndexOf(".") + 1,link.length());
					if(!validatingFileName.equals("txt")){
						Toast.makeText(this, "Please select a text file.", Toast.LENGTH_LONG).show();
						break;
					}
					if(!fileName.contains("smartnote-")){
						Toast.makeText(this, "Please select a appropriate note.", Toast.LENGTH_LONG).show();
						break;
					}
					DownloadFromDropbox download = new DownloadFromDropbox(
							MainActivity.this, mDBApi, "/Smart_note/", result
									.getLink().toString());
					download.execute();
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

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

	// convert InputStream to String
	private static String getStringFromInputStream(InputStream in)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder out = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			out.append(line);
		}
		reader.close();
		return out.toString(); // Prints the string content read from input
								// stream

	}

	private void archiveNote(Note note) {
		SQLiteController controller = new SQLiteController(MainActivity.this);
		try {
			controller.open();
			controller.updateNoteStatus(note, "archive");
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			controller.close();
			this.finish();
			Intent refresh = new Intent(MainActivity.this, MainActivity.class);
			refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(refresh);
			Toast.makeText(getBaseContext(), "Note(s) archived",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void deleteNote(Note note) {
		SQLiteController controller = new SQLiteController(MainActivity.this);
		try {
			controller.open();
			controller.deleteNote(note);
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			controller.close();
			this.finish();
			Intent refresh = new Intent(MainActivity.this, MainActivity.class);
			refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(refresh);
			Toast.makeText(getBaseContext(), "Note(s) deleted",
					Toast.LENGTH_SHORT).show();
		}
	}

	private class MyDatePicker extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		int pYear;
		int pDay;
		int pMonth;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			pYear = year;
			pDay = dayOfMonth;
			pMonth = monthOfYear + 1;

			String monthString = null;
			String dayString = null;
			if (pDay < 10) {
				dayString = "0" + pDay;
			}
			switch (pMonth) {
			case 1:
				monthString = "Jan";
				break;
			case 2:
				monthString = "Feb";
				break;
			case 3:
				monthString = "Mar";
				break;
			case 4:
				monthString = "Apr";
				break;
			case 5:
				monthString = "May";
				break;
			case 6:
				monthString = "Jun";
				break;
			case 7:
				monthString = "Jul";
				break;
			case 8:
				monthString = "Aug";
				break;
			case 9:
				monthString = "Sep";
				break;
			case 10:
				monthString = "Oct";
				break;
			case 11:
				monthString = "Nov";
				break;
			case 12:
				monthString = "Dec";
				break;
			}
			DateTimeFormatter formatter = DateTimeFormat
					.forPattern("dd-MMM-yyyy HH:mm:ss");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			tempArray.clear();
			if (pYear > 0 || pDay > 0 || pMonth > 0) {
				if (!resultArray.isEmpty()) {
					for (int i = 0; i < resultArray.size(); i++) {
						DateTime noteDate = formatter.parseDateTime(resultArray
								.get(i).getNote_date());
						String selectedDate = dayString + "-" + monthString
								+ "-" + pYear;
						String date1 = dateFormat.format(noteDate.toDate());
						Log.d("date selected", selectedDate);
						Log.d("date 1", date1);
						if (date1.equals(selectedDate)) {
							tempArray.add(resultArray.get(i));
						}
					}
				}

				if (!tempArray.isEmpty()) {
					noteList notelist = new noteList(MainActivity.this,
							tempArray, imageId);
					list = (ListView) findViewById(R.id.noteListView);
					list.setAdapter(notelist);
				} else {
					Note note = new Note();
					note.setNote_name("NO result Found please check your input. Thank you");
					note.setNote_category("");
					tempArray.add(note);
					noteList notelist = new noteList(MainActivity.this,
							tempArray, imageId);
					list = (ListView) findViewById(R.id.noteListView);
					list.setAdapter(notelist);
				}
			}
		}
	}

	private class MyCategoryDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Select The Type");
			builder.setItems(R.array.category_choice,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							tempArray.clear();
							// TODO Auto-generated method stub
							String selected = cateArray[which];
							Log.d("Selected", selected);
							for (int i = 0; i < resultArray.size(); i++) {
								if (resultArray.get(i).getNote_category()
										.equals(selected)) {
									tempArray.add(resultArray.get(i));
								}

							}

							if (!tempArray.isEmpty()) {
								noteList notelist = new noteList(getActivity(),
										tempArray, imageId);
								list = (ListView) getActivity().findViewById(
										R.id.noteListView);
								list.deferNotifyDataSetChanged();
								list.setAdapter(notelist);
							} else {
								Note note = new Note();
								note.setNote_name("NO result Found please check your input. Thank you");
								note.setNote_category("");
								tempArray.add(note);
								noteList notelist = new noteList(
										MainActivity.this, tempArray, imageId);
								list = (ListView) findViewById(R.id.noteListView);
								list.setAdapter(notelist);
							}
						}

					});
			return builder.create();

		}
	}

	private class sortByTitle extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Sort By");
			builder.setItems(R.array.sort_choice,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							if (which == 0) {
								Collections.sort(resultArray,
										new TitleAscComparator());
								noteList notelist = new noteList(getActivity(),
										resultArray, imageId);
								list = (ListView) getActivity().findViewById(
										R.id.noteListView);
								list.deferNotifyDataSetChanged();
								list.setAdapter(notelist);

							} else {
								Collections.sort(resultArray,
										new TitleDesComparator());
								noteList notelist = new noteList(getActivity(),
										resultArray, imageId);
								list = (ListView) getActivity().findViewById(
										R.id.noteListView);
								list.deferNotifyDataSetChanged();
								list.setAdapter(notelist);
							}
						}

					});
			return builder.create();
		}

	}

	private class sortByDate extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Sort By");
			builder.setItems(R.array.sort_choice,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							if (arg1 == 0) {
								Collections.sort(resultArray,
										new DateAscComparator());
								noteList notelist = new noteList(getActivity(),
										resultArray, imageId);
								list = (ListView) getActivity().findViewById(
										R.id.noteListView);
								list.deferNotifyDataSetChanged();
								list.setAdapter(notelist);
							} else {
								Collections.sort(resultArray,
										new DateDesComparator());
								noteList notelist = new noteList(getActivity(),
										resultArray, imageId);
								list = (ListView) getActivity().findViewById(
										R.id.noteListView);
								list.deferNotifyDataSetChanged();
								list.setAdapter(notelist);
							}
						}

					});
			return builder.create();
		}

	}

	private class sortByType extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Sort By");
			builder.setItems(R.array.sort_choice,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							if (arg1 == 0) {
								Collections.sort(resultArray,
										new CategoryAscComparator());
								noteList notelist = new noteList(getActivity(),
										resultArray, imageId);
								list = (ListView) getActivity().findViewById(
										R.id.noteListView);
								list.deferNotifyDataSetChanged();
								list.setAdapter(notelist);
							} else {
								Collections.sort(resultArray,
										new CategoryDesComparator());
								noteList notelist = new noteList(getActivity(),
										resultArray, imageId);
								list = (ListView) getActivity().findViewById(
										R.id.noteListView);
								list.deferNotifyDataSetChanged();
								list.setAdapter(notelist);
							}
						}

					});
			return builder.create();
		}

	}

	private class sortByAddress extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Sort By");
			builder.setItems(R.array.sort_choice,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							if (arg1 == 0) {
								Collections.sort(resultArray,
										new AddressAscComparator());
								noteList notelist = new noteList(getActivity(),
										resultArray, imageId);
								list = (ListView) getActivity().findViewById(
										R.id.noteListView);
								list.deferNotifyDataSetChanged();
								list.setAdapter(notelist);
							} else {
								Collections.sort(resultArray,
										new AddressDesComparator());
								noteList notelist = new noteList(getActivity(),
										resultArray, imageId);
								list = (ListView) getActivity().findViewById(
										R.id.noteListView);
								list.deferNotifyDataSetChanged();
								list.setAdapter(notelist);
							}
						}

					});
			return builder.create();
		}

	}

	private void savePreferences(String key, String value) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	private class TitleAscComparator implements Comparator<Note> {

		@Override
		public int compare(Note arg0, Note arg1) {
			// TODO Auto-generated method stub
			return arg0.getNote_name().compareTo(arg1.getNote_name());
		}

	}

	private class TitleDesComparator implements Comparator<Note> {

		@Override
		public int compare(Note lhs, Note rhs) {
			// TODO Auto-generated method stub
			return rhs.getNote_name().compareTo(lhs.getNote_name());
		}

	}

	private class DateAscComparator implements Comparator<Note> {

		@Override
		public int compare(Note lhs, Note rhs) {
			// TODO Auto-generated method stub
			return lhs.getNote_date().compareTo(rhs.getNote_date());
		}

	}

	private class DateDesComparator implements Comparator<Note> {

		@Override
		public int compare(Note lhs, Note rhs) {
			// TODO Auto-generated method stub
			return rhs.getNote_date().compareTo(lhs.getNote_date());
		}

	}

	private class CategoryAscComparator implements Comparator<Note> {

		@Override
		public int compare(Note arg0, Note arg1) {
			// TODO Auto-generated method stub
			return arg0.getNote_category().compareTo(arg1.getNote_category());
		}

	}

	private class CategoryDesComparator implements Comparator<Note> {

		@Override
		public int compare(Note lhs, Note rhs) {
			// TODO Auto-generated method stub
			return rhs.getNote_category().compareTo(lhs.getNote_category());
		}

	}

	private class AddressAscComparator implements Comparator<Note> {

		@Override
		public int compare(Note arg0, Note arg1) {
			// TODO Auto-generated method stub
			return arg0.getNote_address().compareTo(arg1.getNote_address());
		}

	}

	private class AddressDesComparator implements Comparator<Note> {

		@Override
		public int compare(Note lhs, Note rhs) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		AlertDialog.Builder exitBuilder = new AlertDialog.Builder(
				MainActivity.this);

		exitBuilder.setTitle("Exit SmartNote?");

		exitBuilder.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		exitBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog dialog = exitBuilder.create();
		dialog.show();
		// super.onBackPressed();
	}
	
	private void refresh(){
		Intent refresh = new Intent(this, MainActivity.class);
		startActivity(refresh);
		this.finish();
	}

}
