package com.SQLLite.it3176;

import java.security.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

public class mySQLLite {

	public static final String note_id = "noteid";
	public static final String note_name = "notename";
	public static final String note_content = "notecontent";
	public static final String note_category = "category";
	public static String note_date = "notedate";

	private static final String database_name = "smartnotedb";
	private static final String database_table = "smartnotetable";
	private static final int database_version = 1;

	private DBHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;

	// help to create database or upgrade database
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, database_name, null, database_version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated stub

			db.execSQL("CREATE TABLE smartnotetable (noteid INTEGER PRIMARY KEY AUTOINCREMENT, notename TEXT, notecontent TEXT, category , notedate TEXT);");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + database_name);
			onCreate(db);

		}

	}

	public mySQLLite(Context c) {
		ourContext = c;
	}

	public mySQLLite open() throws SQLException {
		ourHelper = new DBHelper(ourContext);

		ourDatabase = ourHelper.getWritableDatabase();

		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public long createEntry(String name, String content, String category) {
		ContentValues cv = new ContentValues();
		cv.put(note_name, name);
		cv.put(note_content, content);
		cv.put(note_category, category);
		cv.put(note_date, getDateTime());
		return ourDatabase.insert(database_table, null, cv);
	}
	
	private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
}

}
