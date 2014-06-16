package com.SQLiteController.it3176;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.example.it3176_smartnote.model.Note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteController{
	public static final String note_id = "noteid";
	public static final String note_name = "notename";
	public static final String note_content = "notecontent";
	public static final String note_category = "category";
	public static String note_date = "notedate";
	public static String note_status = "notestatus";
	
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
			db.execSQL("CREATE TABLE " + database_table + " (NoteId INTEGER PRIMARY KEY AUTOINCREMENT, NoteName TEXT, NoteContent TEXT, Category Text, NoteDate DATETIME, NoteImg Text, NoteVideo Text, NoteAudio Text, NoteStatus TEXT);");
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
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
		cv.put(note_status, "active");
		
		Log.d(LOGCAT, "Inserting new note");
		return ourDatabase.insert(database_table, null, cv);
	}
	
	//Deleting note
	public long deleteNote(Note note){
		Log.d(LOGCAT, "Deleting note");
		return ourDatabase.delete(database_table, "NoteId= " + note.getNote_id(), null);
	}
	
	//Updating note status
	public long updateNoteStatus(Note note){
		ContentValues cv = new ContentValues();
		cv.put(note_name, note.getNote_name());
		cv.put(note_content, note.getNote_content());
		cv.put(note_category, note.getNote_category());
		cv.put(note_date, getDateTime());
		cv.put(note_status, "archive");
		
		Log.d(LOGCAT, "Updating note status");
		return ourDatabase.update(database_table, cv, "NoteId= " + note.getNote_id(), null);
	}
}
