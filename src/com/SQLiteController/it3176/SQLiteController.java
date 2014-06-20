package com.SQLiteController.it3176;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.it3176_smartnote.model.Note;

public class SQLiteController{
	public static final String note_id = "noteid";
	public static final String note_name = "notename";
	public static final String note_content = "notecontent";
	public static final String note_category = "category";
	public static final String note_date = "notedate";
	public static final String note_img = "noteimg";
	public static final String note_video = "notevideo";
	public static final String note_audio = "noteaudio";
	public static final String note_address = "noteaddress";
	public static final String note_tags = "notetags";
	public static final String note_status = "notestatus";
	
	private static final String database_name = "smartnotedb";
	private static final String database_table = "smartnotetable";
	private static final int database_version = 1;
	private static final String LOGCAT = null;
	
	private DBHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	
	private static class DBHelper extends SQLiteOpenHelper{
		public DBHelper(Context context){
			super(context, database_name, null, database_version);
			Log.d(LOGCAT, "Created");
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + database_table + " (noteid INTEGER PRIMARY KEY AUTOINCREMENT, notename TEXT, notecontent TEXT, category Text, notedate DATETIME, noteimg Text, notevideo Text, noteaudio Text, noteaddress Text, notetags Text, notestatus TEXT);");
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + database_name);
			onCreate(db);
		}
	}
	
	public SQLiteController(Context context){
		ourContext = context;
	}
	
	public SQLiteController open() throws SQLException{
		ourHelper = new DBHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		
		return this;
	}
	
	public void close(){
		ourHelper.close();
	}

	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	//Creating new note
	public long insertNote(Note note){
		ContentValues cv = new ContentValues();
		cv.put(note_name, note.getNote_name());
		cv.put(note_content, note.getNote_content());
		cv.put(note_category, note.getNote_category());
		cv.put(note_date, getDateTime());
		cv.put(note_img, note.getNote_img());
		cv.put(note_video, note.getNote_video());
		cv.put(note_audio, note.getNote_audio());
		cv.put(note_address, note.getNote_address());
		cv.put(note_tags, note.getNote_tags());
		cv.put(note_status, "active");
		
		Log.d(LOGCAT, "Inserting new note");
		return ourDatabase.insert(database_table, null, cv);
	}
	
	//Creating new note with self-declared date
	public long insertNoteWithDate(Note note){
		ContentValues cv = new ContentValues();
		cv.put(note_name, note.getNote_name());
		cv.put(note_content, note.getNote_content());
		cv.put(note_category, note.getNote_category());
		cv.put(note_date, note.getNote_date());
		cv.put(note_img, note.getNote_img());
		cv.put(note_video, note.getNote_video());
		cv.put(note_audio, note.getNote_audio());
		cv.put(note_address, note.getNote_address());
		cv.put(note_tags, note.getNote_tags());
		cv.put(note_status, "active");
		
		Log.d(LOGCAT, "Inserting new note with self-declared date");
		return ourDatabase.insert(database_table, null, cv);
	}
	
	//Retrieving all notes
	public ArrayList<Note> retrieveNotes(){
		//Cursor cursor = ourDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		Cursor cursor = ourDatabase.query(database_table, new String[] {note_id, note_name, note_content, note_category, note_date, note_img, note_video, note_audio, note_address, note_tags, note_status}, null, null, null, null, null, null);
		ArrayList<Note> note_list = new ArrayList<Note>();
			if(cursor != null){
				Log.d(LOGCAT, "Retrieving each note");
				if(cursor.moveToFirst()){
					do{
						Note note = new Note();
						note.setNote_id(cursor.getInt(cursor.getColumnIndex(note_id)));
						note.setNote_name(cursor.getString(cursor.getColumnIndex(note_name)));
						note.setNote_content(cursor.getString(cursor.getColumnIndex(note_content)));
						note.setNote_category(cursor.getString(cursor.getColumnIndex(note_category)));
						note.setNote_date(cursor.getString(cursor.getColumnIndex(note_date)));
						note.setNote_img(cursor.getString(cursor.getColumnIndex(note_img)));
						note.setNote_video(cursor.getString(cursor.getColumnIndex(note_video)));
						note.setNote_audio(cursor.getString(cursor.getColumnIndex(note_audio)));
						note.setNote_address(cursor.getString(cursor.getColumnIndex(note_address)));
						note.setNote_tags(cursor.getString(cursor.getColumnIndex(note_tags)));
						note.setNote_status(cursor.getString(cursor.getColumnIndex(note_status)));
						note_list.add(note);						
					} while(cursor.moveToNext());					
				}
			}
		Log.d(LOGCAT, "Retrieved all notes");
		return note_list;
	}
	
	//Deleting note
	public long deleteNote(Note note){
		Log.d(LOGCAT, "Delete note");
		return ourDatabase.delete(database_table, "noteid= " + note.getNote_id(), null);
	}
	
	//Update note status
	public long updateNoteStatus(Note note, String status){
		ContentValues cv = new ContentValues();
		cv.put(note_status, status);
		
		Log.d(LOGCAT, "Update note status");
		return ourDatabase.update(database_table, cv, "noteid= " + note.getNote_id(), null);
	}
	
	//Reactivate note
	public long reactivateNote(Note note){
		ContentValues cv = new ContentValues();
		cv.put(note_date, getDateTime());
		cv.put(note_status, "active");
		
		Log.d(LOGCAT, "Reactivate note");
		return ourDatabase.update(database_table, cv, "noteid= " + note.getNote_id(), null);
	}
	
	//Auto update note status
	public ArrayList<Note> autoUpdateNoteStatus(ArrayList<Note> note_list){
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss");
		DateTime today_date = formatter.parseDateTime(getDateTime());
		
		for(int i = 0; i < note_list.size(); i++){
			DateTime note_date = formatter.parseDateTime(note_list.get(i).getNote_date());
			
			if(note_list.get(i).getNote_status().equals("active") && note_date.plusDays(30).isBefore(today_date)){
				ContentValues cv = new ContentValues();
				cv.put(note_status, "archive");
				
				Log.d(LOGCAT, "Updating NoteId: " + note_list.get(i).getNote_id() + "  status");
				ourDatabase.update(database_table, cv, "noteid= " + note_list.get(i).getNote_id(), null);
			}
		}
		return retrieveNotes();
	}
	
	//Update note
	public long updateNote(Note note){
		ContentValues cv = new ContentValues();
		cv.put(note_name, note.getNote_name());
		cv.put(note_content, note.getNote_content());
		cv.put(note_category, note.getNote_category());
		cv.put(note_date, getDateTime());
		cv.put(note_img, note.getNote_img());
		cv.put(note_video, note.getNote_video());
		cv.put(note_audio, note.getNote_audio());
		cv.put(note_address, note.getNote_address());
		cv.put(note_tags, note.getNote_tags());
		cv.put(note_status, "active");
		
		Log.d(LOGCAT, "Updating existing note");
		return ourDatabase.update(database_table, cv, "noteid= " + note.getNote_id(), null);
	}
	
	//Retrieving note
	public Note retrieveNote(int noteID){
		//Cursor cursor = ourDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		String whereClause = "noteid = ?";
		String[] whereArgs = new String[] {String.valueOf(noteID)};
		Cursor cursor = ourDatabase.query(database_table, new String[] {note_id, note_name, note_content, note_category, note_date, note_img, note_video, note_audio, note_address, note_tags, note_status}, whereClause, whereArgs, null, null, null, null);
		Note note = new Note();
			if(cursor != null){
				Log.d(LOGCAT, "Retrieving each note");
				if(cursor.moveToFirst()){
					do{
						note.setNote_id(cursor.getInt(cursor.getColumnIndex(note_id)));
						note.setNote_name(cursor.getString(cursor.getColumnIndex(note_name)));
						note.setNote_content(cursor.getString(cursor.getColumnIndex(note_content)));
						note.setNote_category(cursor.getString(cursor.getColumnIndex(note_category)));
						note.setNote_date(cursor.getString(cursor.getColumnIndex(note_date)));
						note.setNote_img(cursor.getString(cursor.getColumnIndex(note_img)));
						note.setNote_video(cursor.getString(cursor.getColumnIndex(note_video)));
						note.setNote_audio(cursor.getString(cursor.getColumnIndex(note_audio)));
						note.setNote_address(cursor.getString(cursor.getColumnIndex(note_address)));
						note.setNote_tags(cursor.getString(cursor.getColumnIndex(note_tags)));
						note.setNote_status(cursor.getString(cursor.getColumnIndex(note_status)));
					} while(cursor.moveToNext());					
				}
			}
		Log.d(LOGCAT, "Retrieved all notes");
		return note;
	}
}
